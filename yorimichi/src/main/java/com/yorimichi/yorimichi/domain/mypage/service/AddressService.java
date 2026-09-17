package com.yorimichi.yorimichi.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.mypage.dto.AddressRequestDto;
import com.yorimichi.yorimichi.domain.mypage.dto.AddressResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.AddressMapper;
import com.yorimichi.yorimichi.domain.order.entity.OrderAddress;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * 마이페이지 - 배송지 관리
 *
 * ADDRESS 테이블은 order 패키지가 주문서 조회용으로 이미 쓰고 있어서
 * 엔티티(OrderAddress)를 그대로 재사용합니다.
 *
 * "기본 배송지"는 회원당 한 건만 유지합니다.
 *   - 첫 배송지를 등록하면 자동으로 기본 배송지가 됩니다.
 *   - 기본 배송지를 삭제하면 남은 배송지 중 가장 최근에 등록한 것이 자동으로 승격됩니다.
 */
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressMapper addressMapper;

    public List<AddressResponseDto> getMyAddresses(Long memberId) {
        return addressMapper.findMyAddresses(memberId).stream()
                .map(AddressResponseDto::new)
                .toList();
    }

    @Transactional
    public AddressResponseDto createAddress(Long memberId, AddressRequestDto request) {
        boolean isFirstAddress = !addressMapper.existsAnyAddress(memberId);

        OrderAddress address = OrderAddress.builder()
                .memberId(memberId)
                .addressName(request.getAddressName().trim())
                .receiverName(request.getReceiverName().trim())
                .receiverPhone(request.getReceiverPhone().trim())
                .postalCode(request.getPostalCode().trim())
                .address(request.getAddress().trim())
                .addressDetail(trimOrNull(request.getAddressDetail()))
                /* 첫 배송지는 자동으로 기본 배송지가 됩니다 */
                .isDefault(isFirstAddress)
                .build();

        addressMapper.insertAddress(address);

        return new AddressResponseDto(address);
    }

    @Transactional
    public AddressResponseDto updateAddress(Long memberId, Long addressId, AddressRequestDto request) {
        OrderAddress existing = addressMapper.findByIdAndMember(addressId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));

        OrderAddress updated = OrderAddress.builder()
                .addressId(addressId)
                .memberId(memberId)
                .addressName(request.getAddressName().trim())
                .receiverName(request.getReceiverName().trim())
                .receiverPhone(request.getReceiverPhone().trim())
                .postalCode(request.getPostalCode().trim())
                .address(request.getAddress().trim())
                .addressDetail(trimOrNull(request.getAddressDetail()))
                /* 기본 배송지 여부는 이 API로 바꾸지 않고 기존 값을 유지합니다 */
                .isDefault(existing.getIsDefault())
                .build();

        int updatedCount = addressMapper.updateAddress(updated);
        if (updatedCount == 0) {
            throw new CustomException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        return new AddressResponseDto(updated);
    }

    @Transactional
    public AddressResponseDto setDefaultAddress(Long memberId, Long addressId) {
        OrderAddress existing = addressMapper.findByIdAndMember(addressId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));

        if (!Boolean.TRUE.equals(existing.getIsDefault())) {
            addressMapper.clearDefault(memberId);
            addressMapper.setDefault(addressId, memberId);
        }

        OrderAddress updated = addressMapper.findByIdAndMember(addressId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));

        return new AddressResponseDto(updated);
    }

    @Transactional
    public void deleteAddress(Long memberId, Long addressId) {
        OrderAddress existing = addressMapper.findByIdAndMember(addressId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));

        addressMapper.deleteAddress(addressId, memberId);

        /* 기본 배송지를 지웠고 남은 배송지가 있으면, 그중 하나를 기본으로 승격합니다 */
        if (Boolean.TRUE.equals(existing.getIsDefault())) {
            addressMapper.promoteLatestToDefault(memberId);
        }
    }

    private String trimOrNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
