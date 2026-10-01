package com.yorimichi.yorimichi.domain.product.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 상품 이미지(PRODUCT_IMAGE) DB 접근
 */
@Mapper
public interface ProductImageMapper {

    /**
     * 상품의 이미지 값 목록 — 대표 이미지(is_thumbnail=1) 먼저, 그다음 image_order 순
     * (DB에 저장된 값 그대로이며, 화면용 주소 변환은 ProductImageService가 합니다)
     */
    List<String> findImageUrlsByProductId(@Param("productId") Long productId);
}
