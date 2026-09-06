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
public class OrderItem {

    private Long orderItemId;
    private Long orderId;
    private Long productId;

    private String productName;

    private BigDecimal priceJpy;
    private BigDecimal priceKrw;
    private Integer quantity;
    private BigDecimal itemTotal;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
