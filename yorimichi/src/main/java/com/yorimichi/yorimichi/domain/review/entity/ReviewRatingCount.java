package com.yorimichi.yorimichi.domain.review.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 별점별 리뷰 개수 (상품 리뷰 요약용) */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRatingCount {

    private Integer rating;
    private Long count;
}
