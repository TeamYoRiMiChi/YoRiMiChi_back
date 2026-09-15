package com.yorimichi.yorimichi.domain.inquiry.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryCreateRequestDto;
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
}
