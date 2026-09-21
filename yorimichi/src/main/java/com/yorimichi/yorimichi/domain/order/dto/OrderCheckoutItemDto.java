package com.yorimichi.yorimichi.domain.order.dto;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * 주문서에 표시할 상품 하나
 *
 * priceJpy가 실제 판매 단가(엔화)입니다. priceKrw는 예전에 원화로 환산하던
 * 이름이 남은 것으로, 지금은 별도 환산 없이 priceJpy와 같은 값이 들어갑니다
 * (엔화로 통일 — OrderCalculator.toKrw 참고).
 */
@Getter
public class OrderCheckoutItemDto {

    private final Long productId;
    private final String saleType;
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
                                String saleType,
                                String brand,
                                String productName,
                                String thumbnailUrl,
                                BigDecimal priceJpy,
                                BigDecimal priceKrw,
                                int quantity,
                                BigDecimal overseasShipping,
                                BigDecimal domesticShipping) {
        this.productId = productId;
        this.saleType = saleType;
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
