package com.yorimichi.yorimichi.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.yorimichi.yorimichi.domain.mypage.dto.OrderHistoryResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.OrderItemResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.OrderHistoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

	private final OrderHistoryMapper orderHistoryMapper;

	public List<OrderHistoryResponseDto> getOrderHistories(long memberId) {
		return orderHistoryMapper.findOrderHistoriesByMemberId(memberId)
		        .stream()
		        .map((orderHistory) -> {
		        	List<OrderItemResponseDto> items = orderHistoryMapper.findItemsByOrderId(orderHistory.getOrderId())
		        			.stream()
		        			.map(OrderItemResponseDto::new)
		        			.toList();
		        	
		        	return new OrderHistoryResponseDto(orderHistory, items);
		        })
		        .toList();
	}
}
