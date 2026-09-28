package com.yorimichi.yorimichi.domain.mypage.controller;

import com.yorimichi.yorimichi.domain.mypage.dto.AddressRequestDto;
import com.yorimichi.yorimichi.domain.mypage.dto.AddressResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.AddressService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 로그인한 회원의 배송지를 관리하는 API입니다.
 *
 * GET    /api/addresses                     배송지 목록 조회
 * POST   /api/addresses                     배송지 추가
 * PUT    /api/addresses/{addressId}         배송지 수정
 * PATCH  /api/addresses/{addressId}/default 기본 배송지 설정
 * DELETE /api/addresses/{addressId}         배송지 삭제
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * Returns every address owned by the authenticated member.
     */
    @GetMapping
    public ApiResponse<List<AddressResponseDto>> getMyAddresses(
            @CurrentMemberId Long memberId
    ) {
        return ApiResponse.success(
                addressService.getMyAddresses(memberId)
        );
    }

    /**
     * Creates an address for the authenticated member.
     */
    @PostMapping
    public ApiResponse<AddressResponseDto> createAddress(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody AddressRequestDto request
    ) {
        return ApiResponse.success(
                addressService.createAddress(memberId, request),
                "配送先を追加しました。"
        );
    }

    /**
     * Updates an address owned by the authenticated member.
     */
    @PutMapping("/{addressId}")
    public ApiResponse<AddressResponseDto> updateAddress(
            @CurrentMemberId Long memberId,
            @PathVariable("addressId") Long addressId,
            @Valid @RequestBody AddressRequestDto request
    ) {
        return ApiResponse.success(
                addressService.updateAddress(
                        memberId,
                        addressId,
                        request
                ),
                "配送先を修正しました。"
        );
    }

    /**
     * Marks an address as the authenticated member's default address.
     */
    @PatchMapping("/{addressId}/default")
    public ApiResponse<AddressResponseDto> setDefaultAddress(
            @CurrentMemberId Long memberId,
            @PathVariable("addressId") Long addressId
    ) {
        return ApiResponse.success(
                addressService.setDefaultAddress(
                        memberId,
                        addressId
                ),
                "基本配送先を変更しました。"
        );
    }

    /**
     * Deletes an address owned by the authenticated member.
     */
    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> deleteAddress(
            @CurrentMemberId Long memberId,
            @PathVariable("addressId") Long addressId
    ) {
        addressService.deleteAddress(memberId, addressId);

        return ApiResponse.success(
                null,
                "配送先を削除しました。"
        );
    }
}