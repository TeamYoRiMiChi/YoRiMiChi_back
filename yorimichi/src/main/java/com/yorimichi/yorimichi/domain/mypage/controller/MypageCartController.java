package com.yorimichi.yorimichi.domain.mypage.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.MypageCartResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.MypageCartService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mypage/cart")
@RequiredArgsConstructor
public class MypageCartController {
	
	private final MypageCartService mypageCartService;
	
	//로그인 회원의 마이페이지 장바구니 조회
	@GetMapping
	public ApiResponse<List<MypageCartResponseDto>> 
		getMypageCart(@AuthenticationPrincipal Long memberId){
			return ApiResponse.success(mypageCartService.getMypageCart(memberId));
	}
}
