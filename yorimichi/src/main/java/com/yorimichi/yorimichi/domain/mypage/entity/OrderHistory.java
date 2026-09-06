package com.yorimichi.yorimichi.domain.mypage.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistory {

    private Long orderId;
    private Long memberId;
    private Long groupBuyId;

    private String orderNumber;
    private String orderType;

    private BigDecimal appliedExchangeRate;
    private BigDecimal productAmount;
    private BigDecimal shippingFee;
    private BigDecimal customsDuty;
    private BigDecimal totalAmount;

    private String orderStatus;
    private LocalDateTime orderedAt;
}
