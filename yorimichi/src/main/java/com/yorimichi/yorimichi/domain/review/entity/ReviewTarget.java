package com.yorimichi.yorimichi.domain.review.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰를 쓰려는 주문 상품의 정보
 *
 * 클라이언트가 보내는 값은 orderItemId 하나뿐이고,
 * 상품 ID·판매 방식은 서버가 ORDER_ITEM에서 직접 읽습니다.
 * (다른 사람의 주문 상품이나 엉뚱한 상품에 리뷰를 다는 것을 막기 위해서입니다.)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewTarget {

    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private String saleType;
    private String orderStatus;
}
