package com.yorimichi.yorimichi.domain.admin.repository;

import com.yorimichi.yorimichi.domain.admin.dto.AdminProductUpdateRequest;
import com.yorimichi.yorimichi.domain.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminProductMapper {

    /*
     * 관리자 상품관리 화면에 표시할 전체 상품을 조회한다.
     *
     * 실제 SELECT SQL은 AdminProductMapper.xml에 작성한다.
     * PRODUCT 테이블의 여러 행을 조회하므로 List<Product>로 받는다.
     */
    List<Product> findAllProducts();

    /*
     * 관리자가 변경한 상품 정보를 수정한다.
     *
     * productId: 수정할 상품 번호
     * request: 변경할 카테고리, 재고, 상품 상태
     * 반환값: 실제로 수정된 행의 개수
     */
    int updateProduct(
            @Param("productId") Long productId,
            @Param("request") AdminProductUpdateRequest request
    );
}