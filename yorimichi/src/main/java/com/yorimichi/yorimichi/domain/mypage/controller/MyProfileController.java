package com.yorimichi.yorimichi.domain.mypage.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.ProfileResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.ProfileUpdateRequestDto;
import com.yorimichi.yorimichi.domain.mypage.service.MyProfileService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/myprofile")
@RequiredArgsConstructor
public class MyProfileController {

	private final MyProfileService myProfileService;

	@GetMapping
	public ApiResponse<ProfileResponseDto> getProfile(@AuthenticationPrincipal Long memberId) {
		if(memberId == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		return ApiResponse.success(myProfileService.getProfile(memberId));
	}
	
	@PatchMapping
	public ApiResponse<ProfileResponseDto> updateProfile(
		@AuthenticationPrincipal Long memberId,
		@Valid @RequestBody ProfileUpdateRequestDto request
	) {
		if(memberId == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}
		
		return ApiResponse.success(
			myProfileService.updateProfile(memberId, request), 
			"プロフィールを更新しました。"
		);
	}
	
}
