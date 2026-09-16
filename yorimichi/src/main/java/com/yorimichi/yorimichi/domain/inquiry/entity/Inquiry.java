package com.yorimichi.yorimichi.domain.inquiry.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {

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
}
