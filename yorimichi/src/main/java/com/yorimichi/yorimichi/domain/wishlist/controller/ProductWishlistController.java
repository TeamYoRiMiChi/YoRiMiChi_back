package com.yorimichi.yorimichi.domain.wishlist.controller;

import com.yorimichi.yorimichi.domain.wishlist.dto.WishlistItemResponseDto;
import com.yorimichi.yorimichi.domain.wishlist.dto.WishlistRequestDto;
import com.yorimichi.yorimichi.domain.wishlist.service.ProductWishlistService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 상품 화면의 찜 상태와 찜 목록을 관리하는 로그인 전용 API입니다.
 *
 * GET    /api/wishlist/ids            찜한 상품 ID 목록 조회
 * GET    /api/wishlist/products       상품 정보가 포함된 찜 목록 조회
 * POST   /api/wishlist/products       상품을 찜 목록에 추가
 * DELETE /api/wishlist/products/{id}  상품을 찜 목록에서 삭제
 *
 * 마이페이지 전용 찜 조회는 domain/mypage의 컨트롤러가 담당합니다.
 * 두 컨트롤러의 경로와 Spring Bean 이름이 겹치지 않도록 역할을 분리했습니다.
 */
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class ProductWishlistController {

    private final ProductWishlistService productWishlistService;

    /**
     * Returns product IDs needed to render the member's wishlist state.
     */
    @GetMapping("/ids")
    public ApiResponse<List<Long>> getWishlistIds(
            @CurrentMemberId Long memberId
    ) {
        return ApiResponse.success(
                productWishlistService.getWishlistProductIds(memberId)
        );
    }

    /**
     * Returns the member's wishlist with product information.
     */
    @GetMapping("/products")
    public ApiResponse<List<WishlistItemResponseDto>> getWishlistProducts(
            @CurrentMemberId Long memberId
    ) {
        return ApiResponse.success(
                productWishlistService.getWishlistItems(memberId)
        );
    }

    /**
     * Adds a product to the member's wishlist.
     */
    @PostMapping("/products")
    public ApiResponse<Void> add(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody WishlistRequestDto request
    ) {
        productWishlistService.add(memberId, request.getProductId());

        return ApiResponse.success(
                null,
                "お気に入りに追加しました。"
        );
    }

    /**
     * Removes a product from the member's wishlist.
     */
    @DeleteMapping("/products/{productId}")
    public ApiResponse<Void> remove(
            @CurrentMemberId Long memberId,
            @PathVariable("productId") Long productId
    ) {
        productWishlistService.remove(memberId, productId);

        return ApiResponse.success(
                null,
                "お気に入りから削除しました。"
        );
    }
}
