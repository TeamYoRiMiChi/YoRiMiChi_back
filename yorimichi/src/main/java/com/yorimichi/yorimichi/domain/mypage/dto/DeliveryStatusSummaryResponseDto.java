package com.yorimichi.yorimichi.domain.mypage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryStatusSummaryResponseDto {

    private Long preparingCount;
    private Long shippingCount;
    private Long deliveredCount;
    private Long cancelledCount;
}