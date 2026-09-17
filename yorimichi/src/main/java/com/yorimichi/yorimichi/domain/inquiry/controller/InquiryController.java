package com.yorimichi.yorimichi.domain.inquiry.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryCreateRequestDto;
import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryAnswerRequestDto;
import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryResponseDto;
import com.yorimichi.yorimichi.domain.inquiry.service.InquiryService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ApiResponse<Long> create(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody InquiryCreateRequestDto request) {

        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long inquiryId = inquiryService.create(memberId, request);
        return ApiResponse.success(inquiryId, "お問い合わせを受け付けました。");
    }

    @GetMapping("/my")
    public ApiResponse<List<InquiryResponseDto>> getMyInquiries(
            @AuthenticationPrincipal Long memberId) {

        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.success(inquiryService.getMyInquiries(memberId));
    }

    @PatchMapping("/{inquiryId}")
    public ApiResponse<Void> updatePending(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inquiryId,
            @Valid @RequestBody InquiryCreateRequestDto request) {
        inquiryService.updatePending(memberId, inquiryId, request);
        return ApiResponse.success(null, "お問い合わせを修正しました。");
    }

    @GetMapping("/admin")
    public ApiResponse<List<InquiryResponseDto>> getAll(
            @AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(inquiryService.getAll(memberId));
    }

    @PatchMapping("/admin/{inquiryId}/answer")
    public ApiResponse<Void> answer(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inquiryId,
            @Valid @RequestBody InquiryAnswerRequestDto request) {
        inquiryService.answer(memberId, inquiryId, request.getAnswer());
        return ApiResponse.success(null, "답변이 저장되었습니다.");
    }
}
