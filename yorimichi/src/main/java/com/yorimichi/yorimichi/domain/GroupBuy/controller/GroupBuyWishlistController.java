package com.yorimichi.yorimichi.domain.GroupBuy.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyWishlistResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.service.GroupBuyWishlistService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/group-buys")
@RequiredArgsConstructor
public class GroupBuyWishlistController {

    private final GroupBuyWishlistService groupBuyWishlistService;

    @PostMapping("/{productId}/wishlist")
    public ApiResponse<GroupBuyWishlistResponseDto> toggleWishlist(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("productId") Long productId) {

        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        boolean wishlisted =
                groupBuyWishlistService.toggleWishlist(memberId, productId);

        return ApiResponse.success(
                new GroupBuyWishlistResponseDto(productId, wishlisted)
        );
    }
}
