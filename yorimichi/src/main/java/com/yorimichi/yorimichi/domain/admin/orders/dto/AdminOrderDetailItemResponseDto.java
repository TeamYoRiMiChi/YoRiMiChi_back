package com.yorimichi.yorimichi.domain.admin.orders.dto;

import java.math.BigDecimal;

import com.yorimichi.yorimichi.global.storage.ImageUrlResolver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminOrderDetailItemResponseDto {

    private Long orderItemId;
    private Long productId;
    private String saleType;
    private String productName;
    private String thumbnailUrl;
    private BigDecimal priceJpy;
    private Integer quantity;
    private BigDecimal itemTotal;

    // 저장소 키로 저장돼 있어도 화면에서 바로 쓸 수 있는 URL로 바꿔서 응답합니다.
    public String getThumbnailUrl() {
        return ImageUrlResolver.resolve(thumbnailUrl);
    }
}