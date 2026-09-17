package com.yorimichi.yorimichi.domain.admin.controller;

import com.yorimichi.yorimichi.domain.admin.dto.AdminProductUpdateRequest;
import com.yorimichi.yorimichi.domain.admin.service.AdminProductService;
import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 상품 관리 API
 *
 * PATCH /api/admin/products/{productId}
 * 카테고리, 재고, 판매 상태 수정
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

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