package com.yorimichi.yorimichi.domain.admin.controller;

import com.yorimichi.yorimichi.domain.admin.dto.AdminProductUpdateRequest;
import com.yorimichi.yorimichi.domain.admin.service.AdminProductService;
import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 관리자 상품관리 API
 *
 * GET   /api/admin/products
 * → 관리자 상품 전체 조회
 *
 * PATCH /api/admin/products/{productId}
 * → 카테고리, 재고, 판매 상태 수정
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    /**
     * 관리자 상품 전체 조회
     */
    @GetMapping
    public ApiResponse<List<ProductResponseDto>> getProducts() {
        return ApiResponse.success(
                adminProductService.getProducts()
        );
    }

    /**
     * 관리자 상품 수정
     */
    @PatchMapping("/{productId}")
    public ApiResponse<ProductResponseDto> updateProduct(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody AdminProductUpdateRequest request
    ) {
        return ApiResponse.success(
                adminProductService.updateProduct(
                        productId,
                        request
                )
        );
    }
}