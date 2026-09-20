package com.yorimichi.yorimichi.domain.admin.orders.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderDetailItemResponseDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderDetailResponseDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderResponseDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderStatusUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderSummaryResponseDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminPaymentStatusUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminShippingInfoUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.orders.repository.AdminOrderMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

	private static final int MAX_PAGE_SIZE = 10;

	private final AdminOrderMapper adminOrderMapper;

	// 주문 목록
	@Transactional(readOnly = true)
	public PageResponse<AdminOrderResponseDto> getOrders(
			String keyword,
			String orderType,
			String orderStatus,
			String shippingStatus,
			int page,
			int size
			) {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
		int offset = (safePage - 1) * safeSize;

		String searchKeyword = keyword == null || keyword.isBlank()
				? null : keyword.trim();

		List<AdminOrderResponseDto> content = adminOrderMapper.findOrders(
				searchKeyword,
				orderType,
				orderStatus,
				shippingStatus,
				offset,
				safeSize
				);

		long totalOrders = adminOrderMapper.countOrders(
				searchKeyword,
				orderType,
				orderStatus,
				shippingStatus
				);

		return new PageResponse<>(
				content,
				safePage,
				safeSize,
				totalOrders
				);
	}

	// 주문 종류 수 요약
	@Transactional(readOnly = true)
	public AdminOrderSummaryResponseDto getOrderSummary() {
		return adminOrderMapper.findOrderSummary();
	}

	// 주문 상태 변경
	@Transactional
	public void updateOrderStatus(
			Long orderId,
			AdminOrderStatusUpdateRequestDto request
			) {
		String currentStatus = adminOrderMapper.findOrderStatus(orderId);

		if (currentStatus == null) {
			throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
		}

		String nextStatus = request.getOrderStatus();

		if (!isAllowedOrderStatusChange(currentStatus, nextStatus)) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}
		
		if ("PREPARING".equals(currentStatus)
		        && "SHIPPING".equals(nextStatus)
		        && adminOrderMapper.countShippingInfo(orderId) == 0) {
		    throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}

		adminOrderMapper.updateOrderStatus(orderId, nextStatus);

		if ("SHIPPING".equals(nextStatus)) {
			adminOrderMapper.updateShippingStatus(orderId, "SHIPPING");
		}

		if ("DELIVERED".equals(nextStatus)) {
			adminOrderMapper.updateShippingStatus(orderId, "DELIVERED");
		}
	}

	// 결제 정보 변경
	@Transactional
	public void updatePaymentStatus(
			Long orderId,
			AdminPaymentStatusUpdateRequestDto request
			) {
		String currentOrderStatus = adminOrderMapper.findOrderStatus(orderId);

		if (currentOrderStatus == null) {
			throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
		}

		String currentPaymentStatus =
				adminOrderMapper.findPaymentStatus(orderId);

		if (!"CANCELLED".equals(currentOrderStatus)
				|| !"CANCELLED".equals(currentPaymentStatus)
				|| !"REFUNDED".equals(request.getPaymentStatus())) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}

		adminOrderMapper.updatePaymentStatus(orderId, "REFUNDED");
		adminOrderMapper.updateOrderStatus(orderId, "REFUNDED");
	}

	// 배송 정보 변경
	@Transactional
	public void updateShippingInfo(
			Long orderId,
			AdminShippingInfoUpdateRequestDto request
			) {
		String currentOrderStatus = adminOrderMapper.findOrderStatus(orderId);

		if (currentOrderStatus == null) {
			throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
		}

		if (!"PREPARING".equals(currentOrderStatus)
				|| isBlank(request.getCarrier())
				|| isBlank(request.getTrackingNumber())) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}

		adminOrderMapper.updateShippingInfo(
				orderId,
				request.getCarrier().trim(),
				request.getTrackingNumber().trim()
				);
	}

	// 허용된 요청인지 검사
	private boolean isAllowedOrderStatusChange(
			String currentStatus,
			String nextStatus
			) {
		if ("PAID".equals(currentStatus)) {
			return "PREPARING".equals(nextStatus);
		}

		if ("PREPARING".equals(currentStatus)) {
			return "SHIPPING".equals(nextStatus);
		}

		if ("SHIPPING".equals(currentStatus)) {
			return "DELIVERED".equals(nextStatus);
		}

		return false;
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
	
	// 주문 상세
	@Transactional(readOnly = true)
	public AdminOrderDetailResponseDto getOrderDetail(Long orderId) {
	    AdminOrderDetailResponseDto orderDetail =
	            adminOrderMapper.findOrderDetail(orderId);

	    if (orderDetail == null) {
	        throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
	    }

	    List<AdminOrderDetailItemResponseDto> items =
	            adminOrderMapper.findOrderDetailItems(orderId);

	    orderDetail.setItems(items);

	    return orderDetail;
	}
}