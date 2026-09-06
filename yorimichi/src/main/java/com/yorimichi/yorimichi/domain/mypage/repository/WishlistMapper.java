package com.yorimichi.yorimichi.domain.mypage.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.mypage.entity.Wishlist;

@Mapper
public interface WishlistMapper {

    /** 로그인 회원의 찜 상품 목록 조회 */
    List<Wishlist> findAllByMemberId(@Param("memberId") Long memberId);
}
