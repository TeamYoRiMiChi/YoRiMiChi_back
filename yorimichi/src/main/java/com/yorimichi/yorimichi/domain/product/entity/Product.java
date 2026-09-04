package com.yorimichi.yorimichi.domain.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 일본 상품 (PRODUCT 테이블)
 *
 * - originalPriceJpy: 정가. NULL이면 할인이 없는 상품
 * - salesCount: 누적 판매량 (인기순 정렬에 사용)
 * - status: ACTIVE, SOLD_OUT, HIDDEN
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long productId;
    private Long categoryId;
    private String brand;
    private String productName;
    private String productNameJp;
    private String description;
    private BigDecimal priceJpy;
    private BigDecimal originalPriceJpy;
    private Integer stock;
    private Integer salesCount;
    private BigDecimal weight;
    private String originalUrl;
    private String thumbnailUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 재고가 남아 있는지 */
    public boolean isInStock() {
        return stock != null && stock > 0;
    }

    /**
     * 할인율 (%)
     * 정가가 없거나 판매가보다 낮으면 0을 돌려줍니다.
     */
    public int getDiscountRate() {
        if (originalPriceJpy == null || priceJpy == null) return 0;
        if (originalPriceJpy.compareTo(priceJpy) <= 0) return 0;

        BigDecimal diff = originalPriceJpy.subtract(priceJpy);
        return diff.multiply(BigDecimal.valueOf(100))
                .divide(originalPriceJpy, 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }
}
