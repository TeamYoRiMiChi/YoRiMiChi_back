package com.yorimichi.yorimichi.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문 상품 (ORDER_ITEM 테이블)
 *
 * 상품명과 가격을 여기에 그대로 저장합니다(스냅샷).
 * PRODUCT를 조인하면 될 것 같지만, 나중에 상품 가격이 바뀌거나
 * 상품이 사라지면 과거 주문 내역의 금액이 달라져 버립니다.
 * 영수증이 나중에 바뀌면 안 되는 것과 같은 이유입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLine {

    private Long orderItemId;
    private Long orderId;
    private Long productId;

    /* 주문 시점 스냅샷 */
    private String productName;
    private BigDecimal priceJpy;
    private BigDecimal priceKrw;
    private Integer quantity;
    private BigDecimal itemTotal;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /* 화면 표시용 (PRODUCT 조인) */
    private String brand;
    private String thumbnailUrl;
}
