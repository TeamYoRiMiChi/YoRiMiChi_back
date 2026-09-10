package com.yorimichi.yorimichi.domain.mypage.dto;

import java.time.LocalDateTime;

import com.yorimichi.yorimichi.domain.mypage.entity.OrderShipping;

import lombok.Getter;

@Getter
public class ShippingResponseDto {

    private final String trackingNumber;
    private final String carrier;
    private final String shippingStatus;
    private final LocalDateTime shippedAt;
    private final LocalDateTime deliveredAt;

    public ShippingResponseDto(OrderShipping shipping) {
        this.trackingNumber = shipping.getTrackingNumber();
        this.carrier = shipping.getCarrier();
        this.shippingStatus = shipping.getShippingStatus();
        this.shippedAt = shipping.getShippedAt();
        this.deliveredAt = shipping.getDeliveredAt();
    }
}