package com.yorimichi.yorimichi.domain.admin.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminOrderResponseDto {

    private Long orderId;
    private String orderNumber;
    private String orderType;
    private String orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private LocalDateTime orderedAt;

    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String memberPhone;

    private Long firstOrderItemId;
    private String firstProductName;
    private String firstThumbnailUrl;
    private Integer firstItemQuantity;
    private Integer itemCount;

    private String paymentMethod;
    private String paymentStatus;

    private String shippingStatus;
    private String carrier;
    private String trackingNumber;
}