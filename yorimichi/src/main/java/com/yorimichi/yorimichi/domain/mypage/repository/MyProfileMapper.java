package com.yorimichi.yorimichi.domain.mypage.repository;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.user.entity.User;

@Mapper
public interface MyProfileMapper {
	Optional<User> findMyProfileByMemberId(@Param("memberId") Long memberId);
	
	int updateMyProfile(
			@Param("memberId") Long memberId,
			@Param("name") String name,
			@Param("phone") String phone,
			@Param("encodedPassword") String encodedPassword
	);
}
