package com.yorimichi.yorimichi.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.domain.product.service.ProductService;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

/**
 * 상품 API
 *
 * GET /api/products?categoryId=3&keyword=クリーム&sort=popular&page=1&size=8
 * GET /api/products/{productId}
 *
 * 로그인 없이도 조회할 수 있습니다 (SecurityConfig에서 permitAll).
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<PageResponse<ProductResponseDto>> getProducts(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "recommend") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "8") int size) {

        return ApiResponse.success(
                productService.getProducts(categoryId, keyword, sort, page, size)
        );
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDto> getProduct(
            @PathVariable("productId") Long productId) {

        return ApiResponse.success(productService.getProduct(productId));
    }
}
