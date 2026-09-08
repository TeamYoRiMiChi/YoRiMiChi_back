package com.yorimichi.yorimichi.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문 (ORDERS 테이블)
 *
 * - orderType    : NORMAL(개인구매), GROUP(공동구매)
 * - orderStatus  : PENDING, PAID, PREPARING, SHIPPING, DELIVERED, CANCELLED
 * - 배송지·통관부호는 주문 시점 값을 그대로 복사해 둡니다.
 *   회원이 나중에 주소를 바꿔도 과거 주문 내역은 그대로여야 하기 때문입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long orderId;
    private Long memberId;
    private Long groupBuyId;
    private String orderNumber;
    private String orderType;

    /* 배송지 스냅샷 */
    private String receiverName;
    private String receiverPhone;
    private String postalCode;
    private String address;
    private String addressDetail;

    /* 통관·환율 스냅샷 */
    private String personalCustomsCode;
    private BigDecimal appliedExchangeRate;

    /* 금액 */
    private BigDecimal productAmount;
    private BigDecimal shippingFee;
    private BigDecimal customsDuty;
    private BigDecimal totalAmount;

    private String orderStatus;
    private LocalDateTime orderedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
