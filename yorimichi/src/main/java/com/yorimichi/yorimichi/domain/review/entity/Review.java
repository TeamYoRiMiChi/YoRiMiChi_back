package com.yorimichi.yorimichi.domain.review.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰 (REVIEW 테이블)
 *
 * - saleType    : OVERSEAS(해외직구), GROUP_BUY(공동구매) — 주문 상품의 판매 방식을 그대로 복사
 * - orderItemId : 주문 상품 하나당 리뷰 한 개 (UNIQUE)
 * - rating      : 1~5
 * - memberName  : 목록 조회 시 MEMBER를 조인해서 채우는 표시용 값
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private Long reviewId;
    private Long memberId;
    private Long productId;
    private String saleType;
    private Long orderItemId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /* 화면 표시용 (MEMBER 조인) */
    private String memberName;
}
