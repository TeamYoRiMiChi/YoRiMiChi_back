package com.yorimichi.yorimichi.domain.mypage.dto;

import com.yorimichi.yorimichi.domain.order.entity.OrderAddress;

import lombok.Getter;

/**
 * 배송지 응답 (마이페이지 - 배송지 관리)
 *
 * ADDRESS 테이블은 order 패키지가 주문서 조회용으로 이미 쓰고 있어서
 * 엔티티(OrderAddress)를 그대로 재사용하고, 화면에 보여줄 형태로만 옮겨 담습니다.
 */
@Getter
public class AddressResponseDto {

    private final Long addressId;
    private final String addressName;
    private final String receiverName;
    private final String receiverPhone;
    private final String postalCode;
    private final String address;
    private final String addressDetail;
    private final Boolean isDefault;

    public AddressResponseDto(OrderAddress a) {
        this.addressId = a.getAddressId();
        this.addressName = a.getAddressName();
        this.receiverName = a.getReceiverName();
        this.receiverPhone = a.getReceiverPhone();
        this.postalCode = a.getPostalCode();
        this.address = a.getAddress();
        this.addressDetail = a.getAddressDetail();
        this.isDefault = Boolean.TRUE.equals(a.getIsDefault());
    }
}
