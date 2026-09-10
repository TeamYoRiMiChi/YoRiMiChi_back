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
public class OrderPayment {

    private Long paymentId;
    private Long orderId;

    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal amount;

    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;

    private String transactionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}