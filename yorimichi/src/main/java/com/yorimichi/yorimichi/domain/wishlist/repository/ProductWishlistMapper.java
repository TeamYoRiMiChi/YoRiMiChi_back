package com.yorimichi.yorimichi.domain.wishlist.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.wishlist.entity.ProductWishlist;

import java.util.List;

/**
 * 상품 화면용 찜 매퍼
 *
 * 마이페이지의 WishlistMapper와 이름이 겹치지 않도록 구분했습니다.
 */
@Mapper
public interface ProductWishlistMapper {

    /** 내 찜 목록 (상품 정보 조인) */
    List<ProductWishlist> findByMemberId(@Param("memberId") Long memberId);

    /** 찜한 상품 id만 (화면에서 하트 표시용) */
    List<Long> findProductIdsByMemberId(@Param("memberId") Long memberId);

    /** 이미 찜했는지 확인 */
    boolean exists(@Param("memberId") Long memberId,
                   @Param("productId") Long productId);

    void insert(@Param("memberId") Long memberId,
                @Param("productId") Long productId);

    void delete(@Param("memberId") Long memberId,
                @Param("productId") Long productId);
}
