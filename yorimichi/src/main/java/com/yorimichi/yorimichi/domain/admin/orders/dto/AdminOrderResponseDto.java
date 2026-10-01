package com.yorimichi.yorimichi.domain.admin.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.yorimichi.yorimichi.global.storage.ImageUrlResolver;

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

    // DB에는 저장소 키(예: 2026-10-01/uuid.jpg)가 들어 있을 수 있어서, 응답할 때 화면에서 쓸 수 있는 URL로 바꿔줍니다.
    public String getFirstThumbnailUrl() {
        return ImageUrlResolver.resolve(firstThumbnailUrl);
    }
}