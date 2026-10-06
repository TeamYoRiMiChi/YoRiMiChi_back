package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;

import com.yorimichi.yorimichi.domain.mypage.entity.Wishlist;
import com.yorimichi.yorimichi.global.storage.ImageUrlResolver;

import lombok.Getter;

@Getter
public class WishlistResponseDto {

    private final Long id;
    private final Long wishlistId;
    private final Long productId;
    private final String name;
    private final BigDecimal price;
    private final Boolean soldOut;
    private final String thumbnailUrl;

    public WishlistResponseDto(Wishlist wishlist) {
        this.id = wishlist.getProductId();
        this.wishlistId = wishlist.getWishlistId();
        this.productId = wishlist.getProductId();
        this.name = wishlist.getName();
        this.price = wishlist.getPrice();
        this.soldOut = wishlist.getSoldOut();
        this.thumbnailUrl = ImageUrlResolver.resolve(wishlist.getThumbnailUrl());
    }
}
