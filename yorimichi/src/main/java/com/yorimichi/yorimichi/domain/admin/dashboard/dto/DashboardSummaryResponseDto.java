package com.yorimichi.yorimichi.domain.admin.dashboard.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//is for mybatis
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponseDto {

    private long todayOrderCount;
    private BigDecimal todayRevenue;
    private long newMemberCount;
    private long pendingOrderCount;
}