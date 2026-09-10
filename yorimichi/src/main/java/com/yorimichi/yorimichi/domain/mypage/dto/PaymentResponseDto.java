package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.yorimichi.yorimichi.domain.mypage.entity.OrderPayment;

import lombok.Getter;

@Getter
public class PaymentResponseDto {

    private final String paymentMethod;
    private final String paymentStatus;
    private final BigDecimal amount;
    private final LocalDateTime paidAt;
    private final LocalDateTime cancelledAt;

    public PaymentResponseDto(OrderPayment payment) {
        this.paymentMethod = payment.getPaymentMethod();
        this.paymentStatus = payment.getPaymentStatus();
        this.amount = payment.getAmount();
        this.paidAt = payment.getPaidAt();
        this.cancelledAt = payment.getCancelledAt();
    }
}