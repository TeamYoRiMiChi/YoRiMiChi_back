package com.yorimichi.yorimichi.domain.mypage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.ProfileResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.ProfileUpdateRequestDto;
import com.yorimichi.yorimichi.domain.mypage.service.MyProfileService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 로그인한 회원의 프로필을 관리하는 API입니다.
 *
 * GET   /api/my-profile            프로필 조회
 * PATCH /api/my-profile            이름 및 전화번호 수정
 * PATCH /api/my-profile/withdrawal 회원 탈퇴
 */
@RestController
@RequestMapping("/api/my-profile")
@RequiredArgsConstructor
public class MyProfileController {

	private final MyProfileService myProfileService;

	/**
	 * Returns the authenticated member's profile.
	 */
	@GetMapping
	public ApiResponse<ProfileResponseDto> getProfile(@CurrentMemberId Long memberId) {
		return ApiResponse.success(myProfileService.getProfile(memberId));
	}
	
	/**
	 * Updates the authenticated member's name and phone number.
	 */
	@PatchMapping
	public ApiResponse<ProfileResponseDto> updateProfile(
		@CurrentMemberId Long memberId,
		@Valid @RequestBody ProfileUpdateRequestDto request
	) {
		return ApiResponse.success(
			myProfileService.updateProfile(memberId, request), 
			"プロフィールを更新しました。"
		);
	}
	
	/**
	 * Marks the authenticated member as withdrawn.
	 */
	@PatchMapping("/withdrawal")
	public ApiResponse<Void> withdrawMember(
	        @CurrentMemberId Long memberId
	) {
	    myProfileService.withdrawMember(memberId);

	    return ApiResponse.success(
	            null,
	            "退会処理が完了しました。"
	    );
	}
}
