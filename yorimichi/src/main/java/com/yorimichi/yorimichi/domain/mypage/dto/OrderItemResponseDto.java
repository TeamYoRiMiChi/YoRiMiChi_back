package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;

import com.yorimichi.yorimichi.domain.mypage.entity.OrderItem;

import lombok.Getter;

@Getter
public class OrderItemResponseDto {

    private final String name;
    private final Integer qty;
    private final BigDecimal price;

    public OrderItemResponseDto(OrderItem orderItem) {
        this.name = orderItem.getProductName();
        this.qty = orderItem.getQuantity();
        this.price = orderItem.getPriceKrw();
    }
}