package com.yorimichi.yorimichi.domain.mypage.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyReviewResponseDto {

    private Long reviewId;
    private String productName;
    private String thumbnailUrl;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}