package com.yorimichi.yorimichi.domain.admin.orders.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminPaymentStatusUpdateRequestDto {

    private String paymentStatus;
}