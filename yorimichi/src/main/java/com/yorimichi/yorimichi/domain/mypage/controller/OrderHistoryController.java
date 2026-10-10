package com.yorimichi.yorimichi.domain.mypage.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.OrderDetailResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.OrderHistoryResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.OrderHistoryService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

/**
 * 마이페이지 주문 내역을 조회하는 API입니다.
 *
 * GET /api/order-history           주문 내역 조회
 * GET /api/order-history/{orderId} 주문 상세 조회
 */
@RestController
@RequestMapping("/api/order-history")
@RequiredArgsConstructor
public class OrderHistoryController {
	private final OrderHistoryService orderHistoryService;

	/**
	 * Returns a paginated order history for the authenticated member.
	 */
	@GetMapping
	public ApiResponse<PageResponse<OrderHistoryResponseDto>> getOrderHistories(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size,
			@CurrentMemberId Long memberId
			) {
		return ApiResponse.success(
				orderHistoryService.getOrderHistories(memberId, page, size)
				);
	}

	/**
	 * Returns an order detail owned by the authenticated member.
	 */
	@GetMapping("/{orderId}")
	public ApiResponse<OrderDetailResponseDto> getOrderDetail(
			@PathVariable("orderId") long orderId,
			@CurrentMemberId long memberId			
			) {

		return ApiResponse.success(orderHistoryService.getOrderDetail(
				orderId, memberId
				));
	}

	@PatchMapping("/{orderId}/cancel")
	public ApiResponse<Void> cancelOrder(
			@PathVariable("orderId") long orderId,
			@CurrentMemberId long memberId
			) {
		orderHistoryService.cancelOrder(orderId, memberId);

		return ApiResponse.success(null, "注文をキャンセルしました。");
	}
}
