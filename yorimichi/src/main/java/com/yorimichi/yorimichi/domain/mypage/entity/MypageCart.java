package com.yorimichi.yorimichi.domain.mypage.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MypageCart {
	
	// CART_ITEM 테이블 정보
	private Long cartItemId;
	private Long cartId;
	private Long productId;
	private Integer quantity;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	// CART 테이블에서 가져올 회원 번호
	private Long memberId;
	
	// PRODUCT 테이블에서 가져올 상품 정보
	private String productName;
	private BigDecimal priceJpy;
	private String thumbnailUrl;
	private Integer stock;
	private String productStatus;
	
}
