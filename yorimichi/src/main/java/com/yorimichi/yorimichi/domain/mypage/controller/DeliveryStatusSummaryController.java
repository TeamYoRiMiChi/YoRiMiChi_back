package com.yorimichi.yorimichi.domain.mypage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.DeliveryStatusSummaryResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.DeliveryStatusSummaryService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/delivery-status-summary")
@RequiredArgsConstructor
public class DeliveryStatusSummaryController {

	private final DeliveryStatusSummaryService deliveryStatusSummaryService;

	@GetMapping
	public ApiResponse<DeliveryStatusSummaryResponseDto> getDeliveryStatusSummary(
			@CurrentMemberId Long memberId
			) {
		return ApiResponse.success(
				deliveryStatusSummaryService.getDeliveryStatusSummary(
						memberId)
				);
	}
}
