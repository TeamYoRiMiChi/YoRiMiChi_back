package com.yorimichi.yorimichi.domain.mypage.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.mypage.dto.MyPageProfileResponseDto;

@Mapper
public interface MyPageProfileMapper {

	MyPageProfileResponseDto findByMemberId(
			@Param("memberId") Long memberId
			);
}
