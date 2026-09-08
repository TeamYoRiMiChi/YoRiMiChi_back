package com.yorimichi.yorimichi.domain.wishlist.dto;

import lombok.Getter;

import com.yorimichi.yorimichi.domain.wishlist.entity.ProductWishlist;

import java.math.BigDecimal;

/**
 * 찜한 상품 하나
 *
 * 장바구니 드로어의 찜 탭에서 사용합니다.
 */
@Getter
public class WishlistItemResponseDto {

    private final Long wishlistId;
    private final Long productId;
    private final String brand;
    private final String productName;
    private final BigDecimal priceJpy;
    private final BigDecimal originalPriceJpy;
    private final String thumbnailUrl;
    private final Integer stock;
    private final boolean available;

    public WishlistItemResponseDto(ProductWishlist w) {
        this.wishlistId = w.getWishlistId();
        this.productId = w.getProductId();
        this.brand = w.getBrand();
        this.productName = w.getProductName();
        this.priceJpy = w.getPriceJpy();
        this.originalPriceJpy = w.getOriginalPriceJpy();
        this.thumbnailUrl = w.getThumbnailUrl();
        this.stock = w.getStock();
        this.available = w.isAvailable();
    }
}
