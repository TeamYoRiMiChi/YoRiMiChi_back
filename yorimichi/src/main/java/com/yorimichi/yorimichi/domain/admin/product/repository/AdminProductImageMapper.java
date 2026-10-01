package com.yorimichi.yorimichi.domain.admin.product.repository;

import com.yorimichi.yorimichi.domain.admin.product.dto.AdminProductImageResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 관리자 상품 이미지(PRODUCT_IMAGE) 조회·저장·삭제
 *
 * SQL은 resources/mapper/admin/AdminProductImageMapper.xml에 있습니다.
 */
@Mapper
public interface AdminProductImageMapper {

    /** 한 상품의 이미지 전체 (대표 이미지 → image_order 순) */
    List<AdminProductImageResponse> findByProductId(@Param("productId") Long productId);

    /** DB에 저장된 원래 값(저장소 키 또는 전체 주소). 없으면 null */
    String findRawImageUrl(
            @Param("imageId") Long imageId,
            @Param("productId") Long productId
    );

    int countByProductId(@Param("productId") Long productId);

    int countThumbnail(@Param("productId") Long productId);

    /** 지금까지 쓴 image_order 중 가장 큰 값 (이미지가 없으면 -1) */
    int findMaxOrder(@Param("productId") Long productId);

    /** image_order가 가장 앞선 이미지 id (없으면 null) */
    Long findFirstImageId(@Param("productId") Long productId);

    int insert(
            @Param("productId") Long productId,
            @Param("imageUrl") String imageUrl,
            @Param("imageOrder") int imageOrder,
            @Param("thumbnail") boolean thumbnail
    );

    int delete(
            @Param("imageId") Long imageId,
            @Param("productId") Long productId
    );

    int clearThumbnail(@Param("productId") Long productId);

    int setThumbnail(
            @Param("imageId") Long imageId,
            @Param("productId") Long productId
    );
}
