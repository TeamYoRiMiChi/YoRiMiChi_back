package com.yorimichi.yorimichi.domain.admin.dashboard.repository;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.admin.dashboard.dto.DashboardSummaryResponseDto;

@Mapper
public interface AdminDashboardMapper {

    DashboardSummaryResponseDto findSummary(
            @Param("todayStartUtc") LocalDateTime todayStartUtc,
            @Param("tomorrowStartUtc") LocalDateTime tomorrowStartUtc
    );
}