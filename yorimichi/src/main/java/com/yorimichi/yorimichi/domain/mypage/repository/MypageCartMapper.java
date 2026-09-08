package com.yorimichi.yorimichi.domain.mypage.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.mypage.entity.MypageCart;

@Mapper
public interface MypageCartMapper {
	
	//로그인 회원의 장바구니 상품 목록 조회
	List<MypageCart> findAllByMemberId(
			@Param("memberId") Long memberId
	);
}
