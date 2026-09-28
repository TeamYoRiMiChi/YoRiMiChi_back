package com.yorimichi.yorimichi.domain.mypage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.WishlistResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.WishlistService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * 마이페이지 찜 목록 조회 API입니다.
 *
 * GET /api/wishlist 마이페이지 찜 목록 조회
 */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * Returns wishlist items displayed on the authenticated member's page.
     */
    @GetMapping
    public ApiResponse<List<WishlistResponseDto>> getWishlist(
            @CurrentMemberId Long memberId) {
        return ApiResponse.success(wishlistService.getWishlist(memberId));
    }
}
