package com.yorimichi.yorimichi.domain.mypage.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.OrderHistoryResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.OrderHistoryService;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/orderhistory")
@RequiredArgsConstructor
public class OrderHistoryController {
	private final OrderHistoryService orderHistoryService;
	
	@GetMapping
	public ApiResponse<PageResponse<OrderHistoryResponseDto>> getOrderHistories(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size,
		    @AuthenticationPrincipal Long memberId
		) {
		    return ApiResponse.success(
		        orderHistoryService.getOrderHistories(memberId, page, size)
		    );
		}
}
