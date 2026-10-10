package com.yorimichi.yorimichi.domain.mypage.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.MyReviewResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.ReviewUpdateRequestDto;
import com.yorimichi.yorimichi.domain.mypage.service.MyReviewService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/my-reviews")
@RequiredArgsConstructor
public class MyReviewController {

    private final MyReviewService myReviewService;

    @GetMapping
    public ApiResponse<PageResponse<MyReviewResponseDto>> getMyReviews(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @CurrentMemberId Long memberId
    ) {
        return ApiResponse.success(
                myReviewService.getMyReviews(memberId, page, size)
        );
    }

    @PatchMapping("/{reviewId}")
    public ApiResponse<Void> updateMyReview(
            @PathVariable("reviewId") long reviewId,
            @CurrentMemberId Long memberId,
            @Valid @RequestBody ReviewUpdateRequestDto request
    ) {
        myReviewService.updateMyReview(memberId, reviewId, request);

        return ApiResponse.success(null, "レビューを修正しました。");
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteMyReview(
            @PathVariable("reviewId") long reviewId,
            @CurrentMemberId Long memberId
    ) {
        myReviewService.deleteMyReview(memberId, reviewId);

        return ApiResponse.success(null, "レビューを削除しました。");
    }
}