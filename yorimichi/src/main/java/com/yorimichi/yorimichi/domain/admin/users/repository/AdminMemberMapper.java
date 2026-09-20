package com.yorimichi.yorimichi.domain.admin.users.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.admin.users.dto.AdminMemberResponseDto;

@Mapper
public interface AdminMemberMapper {
	List<AdminMemberResponseDto> findAllMembers();
	
	int updateMemberStatus(
			@Param("memberId") Long memberId,
			@Param("status") String status
	);
	int demoteAdminToUser(@Param("memberId") Long memberId);
}
