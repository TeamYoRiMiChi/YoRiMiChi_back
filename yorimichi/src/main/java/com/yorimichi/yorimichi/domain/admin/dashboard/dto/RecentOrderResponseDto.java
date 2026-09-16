package com.yorimichi.yorimichi.domain.admin.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecentOrderResponseDto {

    private Long orderId;
    private String orderNumber;
    private String memberName;
    private String firstProductName;
    private int itemCount;
    private BigDecimal totalAmount;
    private String orderStatus;
    private LocalDateTime orderedAt;
}