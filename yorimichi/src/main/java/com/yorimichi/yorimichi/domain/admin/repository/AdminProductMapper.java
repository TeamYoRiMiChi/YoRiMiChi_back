package com.yorimichi.yorimichi.domain.admin.repository;

import com.yorimichi.yorimichi.domain.admin.dto.AdminProductCreateRequest;
import com.yorimichi.yorimichi.domain.admin.dto.AdminProductUpdateRequest;
import com.yorimichi.yorimichi.domain.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminProductMapper {

    /**
     * 관리자 상품 전체 조회
     */
    List<Product> findAllProducts();

    /**
     * 상품 등록
     */
    int insertProduct(Product product);

    /**
     * 공동구매 모집 정보 등록
     */
    int insertGroupBuy(
            @Param("productId")
            Long productId,

            @Param("creatorId")
            Long creatorId,

            @Param("request")
            AdminProductCreateRequest request
    );

    /**
     * 관리자 상품 수정
     */
    int updateProduct(
            @Param("productId")
            Long productId,

            @Param("request")
            AdminProductUpdateRequest request
    );

    /**
     * 공동구매 모집 정보 삭제
     */
    int deleteGroupBuyByProductId(
            @Param("productId")
            Long productId
    );

    /**
     * 관리자 상품 삭제
     */
    int deleteProduct(
            @Param("productId")
            Long productId
    );

    int countProductById(
            @Param("productId")
            Long productId
    );
}