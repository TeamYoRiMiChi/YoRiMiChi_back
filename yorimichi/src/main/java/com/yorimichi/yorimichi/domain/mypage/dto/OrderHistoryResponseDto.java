package com.yorimichi.yorimichi.domain.mypage.dto;


import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.yorimichi.yorimichi.domain.mypage.entity.OrderHistory;

import lombok.Getter;

@Getter
public class OrderHistoryResponseDto {
	private final Long id;
	private final String orderNumber;
	private final String date;
	private final String status;
	private final String statusType;
	private final List<OrderItemResponseDto> items;
	private final BigDecimal total;

	public OrderHistoryResponseDto(OrderHistory orderHistory, List<OrderItemResponseDto> items) {
		this.id = orderHistory.getOrderId();
		this.orderNumber = orderHistory.getOrderNumber();
		this.date = orderHistory.getOrderedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
		this.status = orderHistory.getOrderStatus();
		this.statusType = orderHistory.getOrderStatus().toLowerCase();
		
		this.items = items;
		this.total = orderHistory.getTotalAmount();
	}
}
