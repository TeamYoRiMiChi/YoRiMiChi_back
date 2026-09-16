package com.yorimichi.yorimichi.domain.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusCountResponseDto {

    private String status;
    private long count;
}