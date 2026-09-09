package com.yorimichi.yorimichi.domain.wishlist.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.yorimichi.yorimichi.domain.wishlist.dto.WishlistItemResponseDto;
import com.yorimichi.yorimichi.domain.wishlist.dto.WishlistRequestDto;
import com.yorimichi.yorimichi.domain.wishlist.service.ProductWishlistService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import java.util.List;

/**
 * 찜 API — 상품 화면에서 쓰는 토글용 (로그인 필요)
 *
 * GET    /api/wishlist/ids            찜한 상품 id 배열 (하트 표시용)
 * GET    /api/wishlist/products       찜 목록 (상품 정보 포함)
 * POST   /api/wishlist/products       찜 추가
 * DELETE /api/wishlist/products/{id}  찜 삭제
 *
 * 마이페이지 전용 조회는 domain/mypage 쪽 컨트롤러가 담당합니다.
 * 경로와 빈 이름이 겹치지 않도록 여기서는 접두사를 다르게 두었습니다.
 */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class ProductWishlistController {

    private final ProductWishlistService productWishlistService;

    /** 찜한 상품 id만 (프론트가 하트 상태를 표시할 때 사용) */
    @GetMapping("/ids")
    public ApiResponse<List<Long>> getWishlistIds(@AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(
                productWishlistService.getWishlistProductIds(requireLogin(memberId))
        );
    }

    /** 찜 목록 (상품 정보 포함) */
    @GetMapping("/products")
    public ApiResponse<List<WishlistItemResponseDto>> getWishlistProducts(
            @AuthenticationPrincipal Long memberId) {

        return ApiResponse.success(
                productWishlistService.getWishlistItems(requireLogin(memberId))
        );
    }

    @PostMapping("/products")
    public ApiResponse<Void> add(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody WishlistRequestDto request) {

        productWishlistService.add(requireLogin(memberId), request.getProductId());
        return ApiResponse.success(null, "お気に入りに追加しました。");
    }

    @DeleteMapping("/products/{productId}")
    public ApiResponse<Void> remove(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("productId") Long productId) {

        productWishlistService.remove(requireLogin(memberId), productId);
        return ApiResponse.success(null, "お気に入りから削除しました。");
    }


    private Long requireLogin(Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return memberId;
    }
}
