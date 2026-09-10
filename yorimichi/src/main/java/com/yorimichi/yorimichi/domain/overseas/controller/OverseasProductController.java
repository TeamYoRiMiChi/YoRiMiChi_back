package com.yorimichi.yorimichi.domain.overseas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.overseas.service.OverseasProductService;
import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

/**
 * 해외직구 상품 API
 *
 * GET /api/overseas/products?categoryId=3&keyword=クリーム&sort=popular&page=1&size=8
 * GET /api/overseas/products/{productId}
 *
 * saleType은 서버가 OVERSEAS로 고정합니다.
 * 프론트가 파라미터로 바꿀 수 없어 공동구매 상품이 섞일 일이 없습니다.
 *
 * 로그인 없이도 조회할 수 있습니다 (SecurityConfig에서 permitAll).
 */
@RestController
@RequestMapping("/api/overseas/products")
@RequiredArgsConstructor
public class OverseasProductController {

    private final OverseasProductService overseasProductService;

    @GetMapping
    public ApiResponse<PageResponse<ProductResponseDto>> getProducts(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "recommend") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "8") int size) {

        return ApiResponse.success(
                overseasProductService.getProducts(categoryId, keyword, sort, page, size)
        );
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDto> getProduct(
            @PathVariable("productId") Long productId) {

        return ApiResponse.success(overseasProductService.getProduct(productId));
    }
}
