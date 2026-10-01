package com.yorimichi.yorimichi.domain.product.dto;

import lombok.Getter;

import com.yorimichi.yorimichi.domain.product.entity.Product;
import com.yorimichi.yorimichi.global.storage.ImageUrlResolver;

import java.math.BigDecimal;
import java.util.List;

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
    private final String saleType;
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
    /**
     * 상품 이미지 전체 (PRODUCT_IMAGE, 썸네일 → image_order 순)
     * 상세 조회에서만 채우고, 목록에서는 빈 배열입니다.
     */
    private final List<String> images;
    private final String status;

    public ProductResponseDto(Product p) {
        this(p, List.of());
    }

    public ProductResponseDto(Product p, List<String> images) {
        this.productId = p.getProductId();
        this.categoryId = p.getCategoryId();
        this.saleType = p.getSaleType();
        this.brand = p.getBrand();
        this.productName = p.getProductName();
        this.productNameJp = p.getProductNameJp();
        this.priceJpy = p.getPriceJpy();
        this.originalPriceJpy = p.getOriginalPriceJpy();
        this.discountRate = p.getDiscountRate();
        this.stock = p.getStock();
        this.inStock = p.isInStock();
        this.salesCount = p.getSalesCount();
        // DB에는 URL 또는 저장소 키가 들어있을 수 있어서, 화면용 전체 주소로 바꿔서 내려줍니다
        this.thumbnailUrl = ImageUrlResolver.resolve(p.getThumbnailUrl());
        this.images = images;
        this.status = p.getStatus();
    }
}
