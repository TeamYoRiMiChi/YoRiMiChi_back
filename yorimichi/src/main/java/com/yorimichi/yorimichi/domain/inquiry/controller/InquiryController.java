package com.yorimichi.yorimichi.domain.inquiry.controller;

import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryAnswerRequestDto;
import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryCreateRequestDto;
import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryResponseDto;
import com.yorimichi.yorimichi.domain.inquiry.service.InquiryService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 회원 문의와 관리자 답변을 처리하는 API입니다.
 *
 * POST  /api/inquiries                          문의 등록
 * GET   /api/inquiries/my                       내 문의 목록 조회
 * PATCH /api/inquiries/{inquiryId}              답변 전 문의 수정
 * GET   /api/inquiries/admin                    관리자용 전체 문의 조회
 * PATCH /api/inquiries/admin/{inquiryId}/answer 관리자 문의 답변 등록
 */
@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    /**
     * Creates an inquiry for the authenticated member.
     */
    @PostMapping
    public ApiResponse<Long> create(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody InquiryCreateRequestDto request
    ) {
        Long inquiryId = inquiryService.create(memberId, request);

        return ApiResponse.success(
                inquiryId,
                "お問い合わせを受け付けました。"
        );
    }

    /**
     * Returns every inquiry created by the authenticated member.
     */
    @GetMapping("/my")
    public ApiResponse<List<InquiryResponseDto>> getMyInquiries(
            @CurrentMemberId Long memberId
    ) {
        return ApiResponse.success(
                inquiryService.getMyInquiries(memberId)
        );
    }

    /**
     * Updates an inquiry that has not been answered yet.
     */
    @PatchMapping("/{inquiryId}")
    public ApiResponse<Void> updatePending(
            @CurrentMemberId Long memberId,
            @PathVariable("inquiryId") Long inquiryId,
            @Valid @RequestBody InquiryCreateRequestDto request
    ) {
        inquiryService.updatePending(
                memberId,
                inquiryId,
                request
        );

        return ApiResponse.success(
                null,
                "お問い合わせを修正しました。"
        );
    }

    /**
     * Returns every inquiry after verifying administrator access.
     */
    @GetMapping("/admin")
    public ApiResponse<List<InquiryResponseDto>> getAll(
            @CurrentMemberId Long memberId
    ) {
        return ApiResponse.success(
                inquiryService.getAll(memberId)
        );
    }

    /**
     * Saves an administrator's answer to an inquiry.
     */
    @PatchMapping("/admin/{inquiryId}/answer")
    public ApiResponse<Void> answer(
            @CurrentMemberId Long memberId,
            @PathVariable("inquiryId") Long inquiryId,
            @Valid @RequestBody InquiryAnswerRequestDto request
    ) {
        inquiryService.answer(
                memberId,
                inquiryId,
                request.getAnswer()
        );

        return ApiResponse.success(
                null,
                "回答を登録しました。"
        );
    }
}