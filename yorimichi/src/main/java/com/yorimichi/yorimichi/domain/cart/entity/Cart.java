package com.yorimichi.yorimichi.domain.cart.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 장바구니 (CART 테이블)
 *
 * 회원 한 명당 하나만 존재합니다 (MEMBER와 1:1).
 * 실제 담긴 상품은 CART_ITEM에 들어갑니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    private Long cartId;
    private Long memberId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
