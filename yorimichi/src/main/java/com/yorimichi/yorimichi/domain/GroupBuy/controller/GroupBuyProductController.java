package com.yorimichi.yorimichi.domain.GroupBuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.GroupBuy.service.GroupBuyProductService;
import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

/**
 * 공동구매 상품 API
 *
 * GET /api/group-buys/products?categoryId=3&sort=popular&page=1&size=12
 * GET /api/group-buys/products/{productId}
 *
 * saleType은 서버가 GROUP_BUY로 고정합니다.
 *
 * 모집 정보(진행률·마감일)는 GroupBuyController가 담당합니다.
 * 여기서는 상품 자체의 정보만 다룹니다.
 */
@RestController
@RequestMapping("/api/group-buys/products")
@RequiredArgsConstructor
public class GroupBuyProductController {

    private final GroupBuyProductService groupBuyProductService;

    @GetMapping
    public ApiResponse<PageResponse<ProductResponseDto>> getProducts(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false, defaultValue = "recommend") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "12") int size) {

        return ApiResponse.success(
                groupBuyProductService.getProducts(
                        status,
                        categoryId,
                        keyword,
                        sort,
                        page,
                        size
                )
        );
    }
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDto> getProduct(
            @PathVariable("productId") Long productId) {

        return ApiResponse.success(groupBuyProductService.getProduct(productId));
    }
}
