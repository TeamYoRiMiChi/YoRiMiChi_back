package com.yorimichi.yorimichi.domain.product.dto;

import lombok.Getter;

import com.yorimichi.yorimichi.domain.product.entity.Product;

import java.math.BigDecimal;

/**
 * 상품 응답
 *
 * 화면에 필요한 값만 담고, 할인율처럼 계산이 필요한 값은
 * 서버에서 미리 만들어 보냅니다. 프론트마다 다르게 계산하면
 * 값이 어긋날 수 있기 때문입니다.
 */
@Getter
public class ProductResponseDto {

    private final Long productId;
    private final Long categoryId;
    private final String brand;
    private final String productName;
    private final String productNameJp;
    private final BigDecimal priceJpy;
    private final BigDecimal originalPriceJpy;
    private final int discountRate;
    private final Integer stock;
    private final boolean inStock;
    private final Integer salesCount;
    private final String thumbnailUrl;
    private final String status;

    public ProductResponseDto(Product p) {
        this.productId = p.getProductId();
        this.categoryId = p.getCategoryId();
        this.brand = p.getBrand();
        this.productName = p.getProductName();
        this.productNameJp = p.getProductNameJp();
        this.priceJpy = p.getPriceJpy();
        this.originalPriceJpy = p.getOriginalPriceJpy();
        this.discountRate = p.getDiscountRate();
        this.stock = p.getStock();
        this.inStock = p.isInStock();
        this.salesCount = p.getSalesCount();
        this.thumbnailUrl = p.getThumbnailUrl();
        this.status = p.getStatus();
    }
}
