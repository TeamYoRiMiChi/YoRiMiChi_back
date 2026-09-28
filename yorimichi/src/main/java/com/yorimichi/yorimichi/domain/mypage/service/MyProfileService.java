package com.yorimichi.yorimichi.domain.mypage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.mypage.dto.ProfileResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.ProfileUpdateRequestDto;
import com.yorimichi.yorimichi.domain.mypage.repository.MyProfileMapper;
import com.yorimichi.yorimichi.domain.user.entity.User;
import com.yorimichi.yorimichi.domain.user.repository.UserMapper;
import com.yorimichi.yorimichi.global.auth.CognitoAccountService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyProfileService {

	private final MyProfileMapper myProfileMapper;
	private final UserMapper userMapper;
	private final CognitoAccountService cognitoAccountService;

	public ProfileResponseDto getProfile(Long memberId) {

		User user = myProfileMapper.findMyProfileByMemberId(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		return new ProfileResponseDto(user);
	}


	public ProfileResponseDto updateProfile(
			Long memberId, ProfileUpdateRequestDto request
	) {
		int updatedCount = myProfileMapper.updateMyProfile(
				memberId, 
				request.getName().trim(), 
				request.getPhone().trim()
		);
		
		if(updatedCount == 0) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
		
		User updatedUser = myProfileMapper.findMyProfileByMemberId(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		return new ProfileResponseDto(updatedUser);
	}
	
	@Transactional
	public void withdrawMember(Long memberId) {
		User user = userMapper.findById(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		int updatedCount = myProfileMapper.withdrawMember(memberId);
		if (updatedCount == 0) {
			throw new CustomException(ErrorCode.WITHDRAWN_MEMBER);
		}

		// Keep the Cognito account and sub, but prevent future sign-ins.
		cognitoAccountService.disableUser(user.getEmail());
	}
	
}
