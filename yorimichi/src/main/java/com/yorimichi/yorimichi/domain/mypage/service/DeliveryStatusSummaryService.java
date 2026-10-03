package com.yorimichi.yorimichi.domain.mypage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.mypage.dto.DeliveryStatusSummaryResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.DeliveryStatusSummaryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryStatusSummaryService {

	private final DeliveryStatusSummaryMapper deliveryStatusSummaryMapper;

	@Transactional(readOnly = true)
	public DeliveryStatusSummaryResponseDto getDeliveryStatusSummary(
			Long memberId
			) {
		return deliveryStatusSummaryMapper.countByMemberId(memberId);
	}
}
