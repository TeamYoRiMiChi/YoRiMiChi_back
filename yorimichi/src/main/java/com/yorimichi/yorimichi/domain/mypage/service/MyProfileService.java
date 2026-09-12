package com.yorimichi.yorimichi.domain.mypage.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yorimichi.yorimichi.domain.mypage.dto.ProfileResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.ProfileUpdateRequestDto;
import com.yorimichi.yorimichi.domain.mypage.repository.MyProfileMapper;
import com.yorimichi.yorimichi.domain.user.entity.User;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyProfileService {

	private final MyProfileMapper myProfileMapper;
	private final PasswordEncoder passwordEncoder;

	public ProfileResponseDto getProfile(Long memberId) {

		User user = myProfileMapper.findMyProfileByMemberId(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		return new ProfileResponseDto(user);
	}


	public ProfileResponseDto updateProfile(
			Long memberId, ProfileUpdateRequestDto request
	) {
		String encodedPassword = null;
		
		if(request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
			encodedPassword = passwordEncoder.encode(request.getNewPassword());
		}
		
		int updatedCount = myProfileMapper.updateMyProfile(
				memberId, 
				request.getName().trim(), 
				request.getPhone().trim(), 
				encodedPassword
		);
		
		if(updatedCount == 0) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
		
		User updatedUser = myProfileMapper.findMyProfileByMemberId(memberId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		return new ProfileResponseDto(updatedUser);
	}
}
