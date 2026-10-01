package com.yorimichi.yorimichi.domain.admin.product.dto;

import com.yorimichi.yorimichi.global.storage.ImageUrlResolver;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 상품 이미지 한 장 (PRODUCT_IMAGE 한 행)
 *
 * imageUrl은 DB 값(저장소 키 또는 전체 주소)을 화면에서 바로 쓸 수 있는 URL로 바꿔서 내려줍니다.
 * thumbnail이 true인 행이 목록·장바구니·주문에 보이는 대표 이미지입니다.
 */
@Getter
@NoArgsConstructor
public class AdminProductImageResponse {

    private Long imageId;
    private Long productId;
    private String imageUrl;
    private Integer imageOrder;
    private Boolean thumbnail;

    public String getImageUrl() {
        return ImageUrlResolver.resolve(imageUrl);
    }
}
