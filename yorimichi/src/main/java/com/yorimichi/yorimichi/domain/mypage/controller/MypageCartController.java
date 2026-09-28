package com.yorimichi.yorimichi.domain.mypage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.MypageCartResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.MypageCartService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * 마이페이지 장바구니 조회 API입니다.
 *
 * GET /api/mypage/cart 마이페이지 장바구니 조회
 */
@RestController
@RequestMapping("/api/mypage/cart")
@RequiredArgsConstructor
public class MypageCartController {
	
	private final MypageCartService mypageCartService;
	
	/**
	 * Returns cart items displayed on the authenticated member's page.
	 */
	@GetMapping
	public ApiResponse<List<MypageCartResponseDto>> 
		getMypageCart(@CurrentMemberId Long memberId){
			return ApiResponse.success(mypageCartService.getMypageCart(memberId));
	}
}
