package com.yorimichi.yorimichi.domain.product.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * 상품 DB 접근
 *
 * 목록 조회는 조건이 여러 개(카테고리·검색어·정렬·페이지)라
 * 파라미터가 많습니다. @Param으로 이름을 붙여야 XML에서 참조할 수 있습니다.
 */
@Mapper
public interface ProductMapper {

    /**
     * 조건에 맞는 상품 목록 (페이지 단위)
     *
     * @param categoryId 카테고리 id. null이면 전체
     * @param keyword    상품명·브랜드 검색어. null이면 전체
     * @param sort       recommend | popular | newest
     * @param offset     건너뛸 개수
     * @param size       가져올 개수
     */
    List<Product> findAll(@Param("categoryId") Long categoryId,
                          @Param("keyword") String keyword,
                          @Param("sort") String sort,
                          @Param("offset") int offset,
                          @Param("size") int size);

    /** 같은 조건의 전체 개수 (페이지 수 계산용) */
    long countAll(@Param("categoryId") Long categoryId,
                  @Param("keyword") String keyword);

    /** 상품 단건 조회 */
    Optional<Product> findById(@Param("productId") Long productId);
}
