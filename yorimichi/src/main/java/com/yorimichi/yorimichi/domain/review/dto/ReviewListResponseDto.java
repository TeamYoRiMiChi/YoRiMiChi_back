package com.yorimichi.yorimichi.domain.review.dto;

import java.util.ArrayList;
import java.util.List;

import com.yorimichi.yorimichi.domain.review.entity.Review;
import com.yorimichi.yorimichi.domain.review.entity.ReviewRatingCount;

import lombok.Getter;

/**
 * 상품 리뷰 목록 + 요약
 *
 * 해외직구 상세·공동구매 상세의 리뷰 탭에서 같이 씁니다.
 * - average      : 평균 별점 (소수 첫째 자리)
 * - total        : 전체 리뷰 개수 (reviews는 최근 N건만 담기므로 total과 다를 수 있음)
 * - distribution : 5점 → 1점 순서의 별점별 개수·비율
 */
@Getter
public class ReviewListResponseDto {

    private final double average;
    private final long total;
    private final List<Distribution> distribution;
    private final List<ReviewResponseDto> reviews;

    public ReviewListResponseDto(List<ReviewRatingCount> counts, List<Review> reviews) {
        long totalCount = 0;
        long ratingSum = 0;
        long[] byStar = new long[6];

        for (ReviewRatingCount row : counts) {
            int star = row.getRating() == null ? 0 : row.getRating();
            long count = row.getCount() == null ? 0 : row.getCount();

            if (star >= 1 && star <= 5) {
                byStar[star] += count;
                totalCount += count;
                ratingSum += (long) star * count;
            }
        }

        this.total = totalCount;
        this.average = totalCount == 0
                ? 0.0
                : Math.round(ratingSum * 10.0 / totalCount) / 10.0;

        List<Distribution> bars = new ArrayList<>();
        for (int star = 5; star >= 1; star--) {
            int percent = totalCount == 0
                    ? 0
                    : (int) Math.round(byStar[star] * 100.0 / totalCount);
            bars.add(new Distribution(star, byStar[star], percent));
        }
        this.distribution = bars;

        this.reviews = reviews.stream().map(ReviewResponseDto::new).toList();
    }

    @Getter
    public static class Distribution {
        private final int star;
        private final long count;
        private final int percent;

        public Distribution(int star, long count, int percent) {
            this.star = star;
            this.count = count;
            this.percent = percent;
        }
    }
}
