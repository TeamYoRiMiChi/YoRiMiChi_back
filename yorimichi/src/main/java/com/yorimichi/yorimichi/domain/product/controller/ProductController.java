package com.yorimichi.yorimichi.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.domain.product.service.ProductService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

/**
 * 상품 공용 API
 *
 * GET /api/products/{productId}   판매 방식과 무관한 단건 조회
 *
 * 목록 조회는 각 도메인 API를 쓰세요.
 *   해외직구 → GET /api/overseas/products
 *   공동구매 → GET /api/group-buys/products
 *
 * 로그인 없이도 조회할 수 있습니다 (SecurityConfig에서 permitAll).
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDto> getProduct(
            @PathVariable("productId") Long productId) {

        return ApiResponse.success(productService.getProduct(productId));
    }
}
