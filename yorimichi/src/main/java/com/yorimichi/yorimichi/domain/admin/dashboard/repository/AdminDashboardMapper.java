package com.yorimichi.yorimichi.domain.admin.dashboard.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.admin.dashboard.dto.DashboardSummaryResponseDto;
import com.yorimichi.yorimichi.domain.admin.dashboard.dto.DailySalesResponseDto;
import com.yorimichi.yorimichi.domain.admin.dashboard.dto.OrderStatusCountResponseDto;

@Mapper
public interface AdminDashboardMapper {

    DashboardSummaryResponseDto findSummary(
            @Param("todayStartUtc") LocalDateTime todayStartUtc,
            @Param("tomorrowStartUtc") LocalDateTime tomorrowStartUtc
    );

    List<DailySalesResponseDto> findDailySales(
        @Param("startUtc") LocalDateTime startUtc,
        @Param("endUtc") LocalDateTime endUtc
    );

     List<OrderStatusCountResponseDto> findOrderStatusCounts(
            @Param("startUtc") LocalDateTime startUtc,
            @Param("endUtc") LocalDateTime endUtc
    );
}
