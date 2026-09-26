package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderHistoryResponseDto {

    private Long orderId;
    private String orderNumber;
    private LocalDateTime orderedAt;
    private String orderStatus;
    private List<OrderItemResponseDto> items;
    private BigDecimal totalAmount;
}