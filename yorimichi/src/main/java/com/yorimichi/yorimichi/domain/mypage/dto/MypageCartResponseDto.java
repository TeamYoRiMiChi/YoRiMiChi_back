package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;

import com.yorimichi.yorimichi.domain.mypage.entity.MypageCart;

import lombok.Getter;

@Getter
public class MypageCartResponseDto {
	
	private final Long id;
	private final Long productId;
	private final String name;
	private final BigDecimal price;
	private final Integer qty;
	private final String thumbnailUrl;
	private final boolean soldOut;
	public MypageCartResponseDto(MypageCart cart) {
		this.id = cart.getCartItemId();
		this.productId = cart.getProductId();
		this.name = cart.getProductName();
		this.price = cart.getPriceJpy();
		this.qty = cart.getQuantity();
		this.thumbnailUrl = cart.getThumbnailUrl();
		this.soldOut = 
				cart.getStock() == null
				|| cart.getStock() <= 0
				|| !"ACTIVE".equals(cart.getProductStatus());
	}
	
	
	
}
