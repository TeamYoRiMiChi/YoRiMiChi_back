package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;

import com.yorimichi.yorimichi.domain.mypage.entity.OrderItem;

import lombok.Getter;

@Getter
public class OrderDetailItemResponseDto {

    private final Long orderItemId;
    private final Long productId;
    private final String productName;
    private final BigDecimal priceJpy;
    private final BigDecimal priceKrw;
    private final Integer quantity;
    private final BigDecimal itemTotal;

    public OrderDetailItemResponseDto(OrderItem orderItem) {
        this.orderItemId = orderItem.getOrderItemId();
        this.productId = orderItem.getProductId();
        this.productName = orderItem.getProductName();
        this.priceJpy = orderItem.getPriceJpy();
        this.priceKrw = orderItem.getPriceKrw();
        this.quantity = orderItem.getQuantity();
        this.itemTotal = orderItem.getItemTotal();
    }
}