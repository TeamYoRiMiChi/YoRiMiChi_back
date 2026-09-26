package com.yorimichi.yorimichi.domain.mypage.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.mypage.dto.OrderDetailItemResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.OrderDetailResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.OrderHistoryResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.OrderItemResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.OrderHistoryMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

	private final OrderHistoryMapper orderHistoryMapper;

	@Transactional(readOnly = true)
	public PageResponse<OrderHistoryResponseDto> getOrderHistories(
			long memberId,
			int page,
			int size
			) {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), 50);
		int offset = (safePage - 1) * safeSize;

		List<OrderHistoryResponseDto> orders =
				orderHistoryMapper.findOrderHistoriesByMemberId(
						memberId,
						offset,
						safeSize
						);

		if (!orders.isEmpty()) {
			List<Long> orderIds = orders.stream()
					.map(OrderHistoryResponseDto::getOrderId)
					.toList();

			Map<Long, List<OrderItemResponseDto>> itemsByOrderId =
					orderHistoryMapper.findItemsByOrderIds(orderIds)
					.stream()
					.collect(Collectors.groupingBy(
							OrderItemResponseDto::getOrderId
							));

			orders.forEach(order ->
			order.setItems(
					itemsByOrderId.getOrDefault(
							order.getOrderId(),
							List.of()
							)
					)
					);
		}

		long totalOrders =
				orderHistoryMapper.countOrderHistoriesByMemberId(memberId);

		return new PageResponse<>(
				orders,
				safePage,
				safeSize,
				totalOrders
				);
	}

	@Transactional(readOnly = true)
	public OrderDetailResponseDto getOrderDetail(
			long orderId,
			long memberId
			) {
		OrderDetailResponseDto orderDetail =
				orderHistoryMapper
				.findOrderDetailByMemberIdAndOrderId(
						memberId,
						orderId
						)
				.orElseThrow(
						() -> new CustomException(
								ErrorCode.ORDER_NOT_FOUND
								)
						);

		List<OrderDetailItemResponseDto> items =
				orderHistoryMapper.findOrderDetailItemsByOrderId(orderId);

		orderDetail.setItems(items);

		return orderDetail;
	}

	@Transactional
	public void cancelOrder(
			long orderId,
			long memberId
			) {
		String orderStatus =
				orderHistoryMapper.findOrderStatusByMemberIdAndOrderId(
						memberId,
						orderId
						);

		if (orderStatus == null) {
			throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
		}

		if ("CANCELLED".equals(orderStatus)) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_CANCELLED);
		}

		if ("REFUNDED".equals(orderStatus)) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_REFUNDED);
		}

		if (!"PAID".equals(orderStatus)
				&& !"PREPARING".equals(orderStatus)
				&& !"SHIPPING".equals(orderStatus)
				&& !"DELIVERED".equals(orderStatus)) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}

		int updatedOrderCount = orderHistoryMapper.cancelOrder(memberId, orderId);

		if (updatedOrderCount != 1) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}

		int updatedPaymentCount = orderHistoryMapper.cancelPayment(orderId);

		if (updatedPaymentCount != 1) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		int updatedShippingCount = orderHistoryMapper.cancelShipping(orderId);

		if (updatedShippingCount != 1) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

}