package com.yorimichi.yorimichi.domain.mypage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.MyPageProfileResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.MyPageProfileService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mypage-profile")
@RequiredArgsConstructor
public class MyPageProfileController {

	private final MyPageProfileService myPageProfileService;

	@GetMapping
	public ApiResponse<MyPageProfileResponseDto> getMyPageProfile(
			@CurrentMemberId Long memberId
			) {
		return ApiResponse.success(
				myPageProfileService.getMyPageProfile(memberId)
				);
	}
}
