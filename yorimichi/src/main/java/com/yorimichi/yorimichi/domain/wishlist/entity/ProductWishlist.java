package com.yorimichi.yorimichi.domain.wishlist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 찜 (WISHLIST 테이블) — 상품 화면용
 *
 * 마이페이지의 Wishlist 엔티티와 이름이 겹치지 않도록 구분했습니다.
 *
 * 한 회원이 같은 상품을 두 번 찜할 수 없습니다.
 * (UNIQUE (member_id, product_id))
 *
 * 찜 목록에 상품명·가격·이미지를 함께 보여줘야 하므로
 * 조회 시 PRODUCT를 조인한 값도 담습니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWishlist {

    private Long wishlistId;
    private Long memberId;
    private Long productId;
    private LocalDateTime createdAt;

    /* ===== PRODUCT 조인 값 ===== */
    private String brand;
    private String productName;
    private BigDecimal priceJpy;
    private BigDecimal originalPriceJpy;
    private String thumbnailUrl;
    private Integer stock;
    private String productStatus;

    /** 지금도 구매할 수 있는 상품인지 */
    public boolean isAvailable() {
        return "ACTIVE".equals(productStatus)
                && stock != null
                && stock > 0;
    }
}
