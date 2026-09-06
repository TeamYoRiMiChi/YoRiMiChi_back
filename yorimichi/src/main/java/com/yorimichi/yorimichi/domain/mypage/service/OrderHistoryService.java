package com.yorimichi.yorimichi.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.yorimichi.yorimichi.domain.mypage.dto.OrderHistoryResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.OrderItemResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.OrderHistoryMapper;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

	private final OrderHistoryMapper orderHistoryMapper;

	public PageResponse<OrderHistoryResponseDto> getOrderHistories(long memberId, int page, int size) {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), 50);
		int offset = (safePage - 1) * safeSize;
		
		List<OrderHistoryResponseDto> content = orderHistoryMapper.findOrderHistoriesByMemberId(memberId, offset, safeSize)
				.stream()
				.map((orderHistory) -> {
					List<OrderItemResponseDto> items = orderHistoryMapper.findItemsByOrderId(orderHistory.getOrderId())
							.stream()
							.map(OrderItemResponseDto::new)
							.toList();
					
					return new OrderHistoryResponseDto(orderHistory, items);
				})
				.toList();
		
		long totalOrders = orderHistoryMapper.countOrderHistoriesByMemberId(memberId);
		
		return new PageResponse<>(
			content,
			safePage,
			safeSize,
			totalOrders
		);
				
	}
}
