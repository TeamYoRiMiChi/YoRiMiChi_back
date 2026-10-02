package com.yorimichi.yorimichi.domain.review.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.review.dto.ReviewCreateRequestDto;
import com.yorimichi.yorimichi.domain.review.dto.ReviewListResponseDto;
import com.yorimichi.yorimichi.domain.review.service.ReviewService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 리뷰 API
 *
 * POST /api/reviews                          리뷰 등록 (로그인 필요)
 * GET  /api/reviews/orders/{orderId}         이 주문에서 내가 이미 리뷰를 쓴 주문 상품 ID (로그인 필요)
 * GET  /api/products/{productId}/reviews     상품 리뷰 목록 + 요약 (로그인 없이 조회)
 *
 * 마지막 경로는 SecurityConfig의 GET /api/products/** 규칙으로 이미 공개되어 있습니다.
 */
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/reviews")
    public ApiResponse<Long> create(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody ReviewCreateRequestDto request) {

        Long reviewId = reviewService.create(memberId, request);

        return ApiResponse.success(reviewId, "レビューを投稿しました。");
    }

    @GetMapping("/api/reviews/orders/{orderId}")
    public ApiResponse<List<Long>> getReviewedItemIds(
            @CurrentMemberId Long memberId,
            @PathVariable("orderId") Long orderId) {

        return ApiResponse.success(reviewService.getReviewedItemIds(memberId, orderId));
    }

    @GetMapping("/api/products/{productId}/reviews")
    public ApiResponse<ReviewListResponseDto> getProductReviews(
            @PathVariable("productId") Long productId,
            @RequestParam(value = "size", required = false) Integer size) {

        return ApiResponse.success(reviewService.getProductReviews(productId, size));
    }
}
