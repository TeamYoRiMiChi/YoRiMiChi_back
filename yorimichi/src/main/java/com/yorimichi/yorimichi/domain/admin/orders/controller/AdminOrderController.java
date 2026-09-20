package com.yorimichi.yorimichi.domain.admin.orders.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderDetailResponseDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderResponseDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderStatusUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderSummaryResponseDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminPaymentStatusUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminShippingInfoUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.orders.service.AdminOrderService;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    // 주문 조회
    @GetMapping
    public ApiResponse<PageResponse<AdminOrderResponseDto>> getOrders(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "orderType", required = false) String orderType,
            @RequestParam(name = "orderStatus", required = false) String orderStatus,
            @RequestParam(name = "shippingStatus", required = false) String shippingStatus,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ApiResponse.success(
                adminOrderService.getOrders(
                        keyword,
                        orderType,
                        orderStatus,
                        shippingStatus,
                        page,
                        size
                )
        );
    }

    // 주문 종류 요약
    @GetMapping("/summary")
    public ApiResponse<AdminOrderSummaryResponseDto> getOrderSummary() {
        return ApiResponse.success(adminOrderService.getOrderSummary());
    }

    // 주문 상태 변경
    @PatchMapping("/{orderId}/status")
    public ApiResponse<Void> updateOrderStatus(
            @PathVariable("orderId") Long orderId,
            @RequestBody AdminOrderStatusUpdateRequestDto request
    ) {
        adminOrderService.updateOrderStatus(orderId, request);

        return ApiResponse.success(null, "注文ステータスを変更しました。");
    }

    // 결제 상태 변경
    @PatchMapping("/{orderId}/payment-status")
    public ApiResponse<Void> updatePaymentStatus(
            @PathVariable("orderId") Long orderId,
            @RequestBody AdminPaymentStatusUpdateRequestDto request
    ) {
        adminOrderService.updatePaymentStatus(orderId, request);

        return ApiResponse.success(null, "決済ステータスを変更しました。");
    }

    // 배송 상태 변경
    @PatchMapping("/{orderId}/shipping-info")
    public ApiResponse<Void> updateShippingInfo(
            @PathVariable("orderId") Long orderId,
            @RequestBody AdminShippingInfoUpdateRequestDto request
    ) {
        adminOrderService.updateShippingInfo(orderId, request);

        return ApiResponse.success(null, "配送情報を登録しました。");
    }
    
    @GetMapping("/{orderId}")
    public ApiResponse<AdminOrderDetailResponseDto> getOrderDetail(
            @PathVariable("orderId") Long orderId
    ) {
        return ApiResponse.success(
                adminOrderService.getOrderDetail(orderId)
        );
    }
}