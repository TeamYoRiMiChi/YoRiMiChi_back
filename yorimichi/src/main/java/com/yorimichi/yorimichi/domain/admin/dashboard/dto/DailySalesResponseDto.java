package com.yorimichi.yorimichi.domain.admin.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailySalesResponseDto {

    private LocalDate salesDate;
    private BigDecimal revenue;
}