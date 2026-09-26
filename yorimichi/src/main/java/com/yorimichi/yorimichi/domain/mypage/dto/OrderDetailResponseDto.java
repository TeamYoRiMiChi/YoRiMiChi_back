package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailResponseDto {

    private Long orderId;
    private String orderNumber;
    private String orderType;
    private String orderStatus;
    private LocalDateTime orderedAt;

    private String receiverName;
    private String receiverPhone;
    private String postalCode;
    private String address;
    private String addressDetail;

    private BigDecimal productAmount;
    private BigDecimal shippingFee;
    private BigDecimal customsDuty;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;

    private String shippingStatus;
    private String carrier;
    private String trackingNumber;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    private List<OrderDetailItemResponseDto> items;
}