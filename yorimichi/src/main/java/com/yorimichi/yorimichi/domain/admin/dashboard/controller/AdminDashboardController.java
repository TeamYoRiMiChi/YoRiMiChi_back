package com.yorimichi.yorimichi.domain.admin.dashboard.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.admin.dashboard.dto.RecentOrderResponseDto;  
import com.yorimichi.yorimichi.domain.admin.dashboard.dto.DashboardOrderStatusResponseDto;
import com.yorimichi.yorimichi.domain.admin.dashboard.dto.DashboardSalesTrendResponseDto;
import com.yorimichi.yorimichi.domain.admin.dashboard.dto.DashboardSummaryResponseDto;
import com.yorimichi.yorimichi.domain.admin.dashboard.service.AdminDashboardService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponseDto> getSummary(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(
                adminDashboardService.getSummary(memberId)
        );
    }

    @GetMapping("/sales-trend")
    public ApiResponse<DashboardSalesTrendResponseDto> getSalesTrend(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(
                adminDashboardService.getSalesTrend(memberId)
        );
    }

    @GetMapping("/order-status")
    public ApiResponse<DashboardOrderStatusResponseDto> getOrderStatus(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(
                adminDashboardService.getOrderStatus(memberId)
        );
    }

    @GetMapping("/recent-orders")
    public ApiResponse<List<RecentOrderResponseDto>> getRecentOrders(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.success(
                adminDashboardService.getRecentOrders(memberId)
        );
    }
}