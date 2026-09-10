package com.yorimichi.yorimichi.domain.mypage.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderShipping {

    private Long shippingId;
    private Long orderId;

    private String trackingNumber;
    private String carrier;
    private String shippingStatus;

    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}