package com.yorimichi.yorimichi.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.domain.product.entity.Product;
import com.yorimichi.yorimichi.domain.product.service.ProductService;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

/**
 * 상품 API
 *
 * GET /api/products?saleType=OVERSEAS&categoryId=3&keyword=クリーム&sort=popular&page=1&size=8
 * GET /api/products/{productId}
 *
 * saleType을 지정하지 않으면 해외직구 상품만 내려줍니다.
 * 해외직구 페이지에서 공동구매 전용 상품이 섞여 보이면 안 되기 때문입니다.
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
            @RequestParam(value = "saleType", required = false,
                          defaultValue = Product.SALE_TYPE_OVERSEAS) String saleType,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "recommend") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "8") int size) {

        return ApiResponse.success(
                productService.getProducts(saleType, categoryId, keyword, sort, page, size)
        );
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDto> getProduct(
            @PathVariable("productId") Long productId) {

        return ApiResponse.success(productService.getProduct(productId));
    }
}
