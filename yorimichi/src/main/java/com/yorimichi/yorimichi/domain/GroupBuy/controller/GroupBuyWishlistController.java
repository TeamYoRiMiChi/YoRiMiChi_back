package com.yorimichi.yorimichi.domain.GroupBuy.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyWishlistResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.service.GroupBuyWishlistService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * 공동구매 상품의 찜 상태를 변경하는 API입니다.
 *
 * POST /api/group-buys/{productId}/wishlist 공동구매 상품 찜 상태 변경
 */
@RestController
@RequestMapping("/api/group-buys")
@RequiredArgsConstructor
public class GroupBuyWishlistController {

    private final GroupBuyWishlistService groupBuyWishlistService;

    /**
     * Toggles the wishlist state for the authenticated member.
     */
    @PostMapping("/{productId}/wishlist")
    public ApiResponse<GroupBuyWishlistResponseDto> toggleWishlist(
            @CurrentMemberId Long memberId,
            @PathVariable("productId") Long productId) {
        boolean wishlisted =
                groupBuyWishlistService.toggleWishlist(memberId, productId);

        return ApiResponse.success(
                new GroupBuyWishlistResponseDto(productId, wishlisted)
        );
    }
}
