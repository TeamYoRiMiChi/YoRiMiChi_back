<<<<<<< HEAD
=======
package com.yorimichi.yorimichi.domain.mypage.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.WishlistResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.WishlistService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<List<WishlistResponseDto>> getWishlist(
            @AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(wishlistService.getWishlist(memberId));
    }
}
>>>>>>> branch 'master' of https://github.com/TeamYoRiMiChi/YoRiMiChi_back.git
