package com.yorimichi.yorimichi.domain.admin.dashboard.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardOrderStatusResponseDto {

    private final long totalCount;
    private final List<OrderStatusCountResponseDto> statuses;
}