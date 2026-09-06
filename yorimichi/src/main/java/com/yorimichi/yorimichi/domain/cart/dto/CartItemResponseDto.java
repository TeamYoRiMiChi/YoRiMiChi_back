package com.yorimichi.yorimichi.domain.cart.dto;

import lombok.Getter;

import com.yorimichi.yorimichi.domain.cart.entity.CartItem;

import java.math.BigDecimal;

/** 장바구니에 담긴 상품 하나 */
@Getter
public class CartItemResponseDto {

    private final Long cartItemId;
    private final Long productId;
    private final String brand;
    private final String productName;
    private final BigDecimal priceJpy;
    private final BigDecimal originalPriceJpy;
    private final String thumbnailUrl;
    private final Integer quantity;
    private final BigDecimal subtotal;
    private final Integer stock;
    private final boolean available;

    public CartItemResponseDto(CartItem item) {
        this.cartItemId = item.getCartItemId();
        this.productId = item.getProductId();
        this.brand = item.getBrand();
        this.productName = item.getProductName();
        this.priceJpy = item.getPriceJpy();
        this.originalPriceJpy = item.getOriginalPriceJpy();
        this.thumbnailUrl = item.getThumbnailUrl();
        this.quantity = item.getQuantity();
        this.subtotal = item.getSubtotal();
        this.stock = item.getStock();
        this.available = item.isAvailable();
    }
}
