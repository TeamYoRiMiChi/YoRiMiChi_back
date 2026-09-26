package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailItemResponseDto {

    private Long orderItemId;
    private Long productId;
    private String saleType;
    private String productName;
    private String thumbnailUrl;
    private BigDecimal priceJpy;
    private Integer quantity;
    private BigDecimal itemTotal;
}