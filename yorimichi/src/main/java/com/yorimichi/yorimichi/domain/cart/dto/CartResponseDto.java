package com.yorimichi.yorimichi.domain.cart.dto;

import lombok.Getter;

import com.yorimichi.yorimichi.domain.cart.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * 장바구니 전체 응답
 *
 * 합계는 서버에서 계산해 보냅니다.
 * 프론트에서 더하면 화면마다 값이 어긋날 수 있기 때문입니다.
 */
@Getter
public class CartResponseDto {

    private final List<CartItemResponseDto> items;
    private final int totalCount;      // 담긴 종류 수
    private final int totalQuantity;   // 담긴 총 개수
    private final BigDecimal totalPrice;

    public CartResponseDto(List<CartItem> items) {
        this.items = items.stream()
                .map(CartItemResponseDto::new)
                .toList();

        this.totalCount = items.size();

        this.totalQuantity = items.stream()
                .mapToInt(i -> i.getQuantity() == null ? 0 : i.getQuantity())
                .sum();

        this.totalPrice = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
