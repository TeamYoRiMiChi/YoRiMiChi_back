package com.yorimichi.yorimichi.domain.inquiry.dto;

import java.time.LocalDateTime;

import com.yorimichi.yorimichi.domain.inquiry.entity.Inquiry;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InquiryResponseDto {

    private Long inquiryId;
    private Long memberId;
    private String memberName;
    private String email;
    private String category;
    private String title;
    private String content;
    private String status;
    private String answer;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InquiryResponseDto from(Inquiry inquiry) {
        return InquiryResponseDto.builder()
                .inquiryId(inquiry.getInquiryId())
                .memberId(inquiry.getMemberId())
                .memberName(inquiry.getMemberName())
                .email(inquiry.getEmail())
                .category(inquiry.getCategory())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .status(inquiry.getStatus())
                .answer(inquiry.getAnswer())
                .answeredAt(inquiry.getAnsweredAt())
                .createdAt(inquiry.getCreatedAt())
                .updatedAt(inquiry.getUpdatedAt())
                .build();
    }
}
