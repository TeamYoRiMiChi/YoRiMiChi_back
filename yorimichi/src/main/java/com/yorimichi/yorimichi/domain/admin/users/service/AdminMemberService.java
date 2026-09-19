package com.yorimichi.yorimichi.domain.admin.users.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.admin.users.dto.AdminMemberResponseDto;
import com.yorimichi.yorimichi.domain.admin.users.repository.AdminMemberMapper;
import com.yorimichi.yorimichi.domain.user.entity.User;
import com.yorimichi.yorimichi.domain.user.repository.UserMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMemberService {
	private final UserMapper userMapper;
	private final AdminMemberMapper adminMemberMapper;
	
	@Transactional(readOnly = true)
	public List<AdminMemberResponseDto> getMembers(Long adminMemberId) {
		if (adminMemberId == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}
		User admin = userMapper.findById(adminMemberId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		
		if (!"ADMIN".equals(admin.getRole()) || !"ACTIVE".equals(admin.getStatus())) {
			throw new CustomException(ErrorCode.ADMIN_ACCESS_DENIED);
		}
		
		return adminMemberMapper.findAllMembers();
	}
	
	@Transactional
	public void updateMemberStatus(Long adminMemberId, Long targetMemberId, String status) {
		if(adminMemberId == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}
		User admin = userMapper.findById(adminMemberId).orElseThrow(() -> new
				CustomException(ErrorCode.USER_NOT_FOUND));
		if(!"ADMIN".equals(admin.getRole()) || !"ACTIVE".equals(admin.getStatus())) {
			throw new CustomException(ErrorCode.ADMIN_ACCESS_DENIED);
		}
		if(!"ACTIVE".equals(status) && !"INACTIVE".equals(status)) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}
		
		userMapper.findById(targetMemberId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		
		adminMemberMapper.updateMemberStatus(targetMemberId, status);
	}
}
