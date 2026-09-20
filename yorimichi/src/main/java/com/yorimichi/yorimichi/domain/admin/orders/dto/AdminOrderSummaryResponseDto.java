package com.yorimichi.yorimichi.domain.admin.orders.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminOrderSummaryResponseDto {

    private Long totalCount;
    private Long paidCount;
    private Long preparingCount;
    private Long shippingCount;
    private Long deliveredCount;
    private Long cancelledCount;
    private Long refundedCount;
}