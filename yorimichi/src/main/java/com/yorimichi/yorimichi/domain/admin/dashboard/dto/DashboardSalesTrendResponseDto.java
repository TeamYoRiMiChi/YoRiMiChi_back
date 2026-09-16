package com.yorimichi.yorimichi.domain.admin.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardSalesTrendResponseDto {

    private BigDecimal totalRevenue;
    private List<DailySalesResponseDto> dailySales;
}