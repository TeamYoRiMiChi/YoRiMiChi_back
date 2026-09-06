package com.yorimichi.yorimichi.domain.mypage.controller;


import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.OrderHistoryResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.OrderHistoryService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/orderhistory")
@RequiredArgsConstructor
public class OrderHistoryController {
	private final OrderHistoryService orderHistoryService;
	
	@GetMapping
	public ApiResponse<List<OrderHistoryResponseDto>> getOrderHistories(
		    @AuthenticationPrincipal Long memberId
		) {
		    return ApiResponse.success(
		        orderHistoryService.getOrderHistories(memberId)
		    );
		}
}
