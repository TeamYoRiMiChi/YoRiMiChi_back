package com.yorimichi.yorimichi.domain.order.dto;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * 주문서에 표시할 상품 하나
 *
 * 엔화 가격과 원화 환산가를 함께 내려줍니다.
 * 환산은 서버에서 한 번만 계산해 화면마다 값이 어긋나지 않게 합니다.
 */
@Getter
public class OrderCheckoutItemDto {

    private final Long productId;
    private final String brand;
    private final String productName;
    private final String thumbnailUrl;

    private final BigDecimal priceJpy;
    private final BigDecimal priceKrw;
    private final int quantity;
    private final BigDecimal itemTotal;

    private final BigDecimal overseasShipping;
    private final BigDecimal domesticShipping;

    public OrderCheckoutItemDto(Long productId,
                                String brand,
                                String productName,
                                String thumbnailUrl,
                                BigDecimal priceJpy,
                                BigDecimal priceKrw,
                                int quantity,
                                BigDecimal overseasShipping,
                                BigDecimal domesticShipping) {
        this.productId = productId;
        this.brand = brand;
        this.productName = productName;
        this.thumbnailUrl = thumbnailUrl;
        this.priceJpy = priceJpy;
        this.priceKrw = priceKrw;
        this.quantity = quantity;
        this.itemTotal = priceKrw.multiply(BigDecimal.valueOf(quantity));
        this.overseasShipping = overseasShipping;
        this.domesticShipping = domesticShipping;
    }
}
