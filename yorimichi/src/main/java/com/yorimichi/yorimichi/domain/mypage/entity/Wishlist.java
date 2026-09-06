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
public class Wishlist {

    private Long wishlistId;
    private Long memberId;
    private Long productId;
    private String name;
    private BigDecimal price;
    private Boolean soldOut;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
