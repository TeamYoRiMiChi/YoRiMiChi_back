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
 * 상품 공용 API
 *
 * GET /api/products/{productId}   판매 방식과 무관한 단건 조회
 * GET /api/products/search        헤더 통합검색 (해외직구+공동구매 섞어서 조회)
 *
 * 목록 조회(카테고리 화면)는 각 도메인 API를 쓰세요.
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

    /**
     * 헤더 검색창 → 검색 결과 페이지
     *
     * GET /api/products/search?keyword=クリーム&sort=recommend&page=1&size=8
     */
    @GetMapping("/search")
    public ApiResponse<PageResponse<ProductResponseDto>> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "sort", required = false, defaultValue = "recommend") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "8") int size) {

        return ApiResponse.success(
                productService.search(keyword, categoryId, sort, page, size)
        );
    }
}
