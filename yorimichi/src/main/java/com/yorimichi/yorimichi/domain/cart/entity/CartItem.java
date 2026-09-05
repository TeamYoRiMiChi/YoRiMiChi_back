package com.yorimichi.yorimichi.domain.cart.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 장바구니에 담긴 상품 (CART_ITEM 테이블)
 *
 * 화면에 상품명·가격·이미지를 함께 보여줘야 하므로,
 * 조회 시 PRODUCT를 조인한 값도 같이 담습니다.
 * (아래 product~ 필드가 조인으로 채워지는 부분입니다)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private Long cartItemId;
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /* ===== PRODUCT 조인 값 ===== */
    private String brand;
    private String productName;
    private BigDecimal priceJpy;
    private BigDecimal originalPriceJpy;
    private String thumbnailUrl;
    private Integer stock;
    private String productStatus;

    /** 이 항목의 소계 (단가 × 수량) */
    public BigDecimal getSubtotal() {
        if (priceJpy == null || quantity == null) return BigDecimal.ZERO;
        return priceJpy.multiply(BigDecimal.valueOf(quantity));
    }

    /** 담은 뒤 재고가 줄어들어 주문할 수 없게 된 상태인지 */
    public boolean isAvailable() {
        return "ACTIVE".equals(productStatus)
                && stock != null
                && quantity != null
                && stock >= quantity;
    }
}
