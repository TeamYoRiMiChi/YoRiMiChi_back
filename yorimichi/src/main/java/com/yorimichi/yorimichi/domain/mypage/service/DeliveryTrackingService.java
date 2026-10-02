package com.yorimichi.yorimichi.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.mypage.dto.DeliveryTrackingResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.DeliveryTrackingMapper;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryTrackingService {

	private static final int MAX_PAGE_SIZE = 50;

	private final DeliveryTrackingMapper deliveryTrackingMapper;

	@Transactional(readOnly = true)
	public PageResponse<DeliveryTrackingResponseDto> getDeliveryTrackings(
			Long memberId, int page, int size
			) {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
		int offset = (safePage - 1) * safeSize;

		List<DeliveryTrackingResponseDto> deliveryTrackings = 
				deliveryTrackingMapper.findDeliveryTrackingsByMemberId(
						memberId, offset, safeSize
						);

		long totalDeliveryTrackings = 
				deliveryTrackingMapper.countDeliveryTrackingsByMemberId(memberId);

		return new PageResponse<>(
				deliveryTrackings, safePage, safeSize, totalDeliveryTrackings
				);
	}

}
