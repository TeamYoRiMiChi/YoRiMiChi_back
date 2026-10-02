package com.yorimichi.yorimichi.domain.review.dto;

import java.time.LocalDateTime;

import com.yorimichi.yorimichi.domain.review.entity.Review;

import lombok.Getter;

/**
 * 리뷰 한 건 (목록 표시용)
 *
 * 작성자 이름은 그대로 노출하지 않고 첫 글자만 보여줍니다. (예: 山田 太郎 → 山＊＊)
 */
@Getter
public class ReviewResponseDto {

    private final Long reviewId;
    private final String memberName;
    private final Integer rating;
    private final String content;
    private final String saleType;
    private final LocalDateTime createdAt;

    public ReviewResponseDto(Review review) {
        this.reviewId = review.getReviewId();
        this.memberName = mask(review.getMemberName());
        this.rating = review.getRating();
        this.content = review.getContent();
        this.saleType = review.getSaleType();
        this.createdAt = review.getCreatedAt();
    }

    private static String mask(String name) {
        if (name == null || name.isBlank()) {
            return "匿名";
        }

        String trimmed = name.trim();
        int firstEnd = trimmed.offsetByCodePoints(0, 1);
        int restLength = trimmed.codePointCount(firstEnd, trimmed.length());

        return trimmed.substring(0, firstEnd) + "＊".repeat(Math.min(restLength, 2));
    }
}
