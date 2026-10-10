package com.yorimichi.yorimichi.domain.mypage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.DeliveryTrackingResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.DeliveryTrackingService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/delivery-tracking")
@RequiredArgsConstructor
public class DeliveryTrackingController {

	private final DeliveryTrackingService deliveryTrackingService;

	@GetMapping
	public ApiResponse<PageResponse<DeliveryTrackingResponseDto>> getDeliveryTrackings(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size,
			@CurrentMemberId Long memberId
			) {
		return ApiResponse.success(
				deliveryTrackingService.getDeliveryTrackings(
						memberId, page, size
						)
				);
	}
}
