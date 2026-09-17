package com.yorimichi.yorimichi.domain.mypage.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.AddressRequestDto;
import com.yorimichi.yorimichi.domain.mypage.dto.AddressResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.AddressService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 마이페이지 - 배송지 관리 (고객용 CRUD)
 *
 * 프론트: src/components/MyPage/AddressManagement/AddressManagement.jsx
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ApiResponse<List<AddressResponseDto>> getMyAddresses(
            @AuthenticationPrincipal Long memberId
    ) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.success(addressService.getMyAddresses(memberId));
    }

    @PostMapping
    public ApiResponse<AddressResponseDto> createAddress(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody AddressRequestDto request
    ) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.success(
                addressService.createAddress(memberId, request),
                "配送先を追加しました。"
        );
    }

    @PutMapping("/{addressId}")
    public ApiResponse<AddressResponseDto> updateAddress(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto request
    ) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.success(
                addressService.updateAddress(memberId, addressId, request),
                "配送先を修正しました。"
        );
    }

    @PatchMapping("/{addressId}/default")
    public ApiResponse<AddressResponseDto> setDefaultAddress(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long addressId
    ) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.success(
                addressService.setDefaultAddress(memberId, addressId),
                "基本配送先を変更しました。"
        );
    }

    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> deleteAddress(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long addressId
    ) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        addressService.deleteAddress(memberId, addressId);

        return ApiResponse.<Void>success(null, "配送先を削除しました。");
    }
}
