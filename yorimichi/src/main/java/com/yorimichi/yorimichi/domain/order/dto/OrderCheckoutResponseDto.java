package com.yorimichi.yorimichi.domain.order.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 주문서 화면에 필요한 데이터 묶음
 *
 * 배송지, 통관부호, 주문할 상품, 금액을 한 번에 내려줍니다.
 * 화면 하나를 그리려고 API를 네 번 부를 이유가 없기 때문입니다.
 */
@Getter
public class OrderCheckoutResponseDto {

    private final OrderAddressDto address;
    private final String personalCustomsCode;
    private final List<OrderCheckoutItemDto> items;

    private final BigDecimal exchangeRate;
    private final BigDecimal productAmount;
    private final BigDecimal overseasShipping;
    private final BigDecimal domesticShipping;
    private final BigDecimal customsDuty;
    private final BigDecimal totalAmount;

    public OrderCheckoutResponseDto(OrderAddressDto address,
                                    String personalCustomsCode,
                                    List<OrderCheckoutItemDto> items,
                                    BigDecimal exchangeRate,
                                    BigDecimal productAmount,
                                    BigDecimal overseasShipping,
                                    BigDecimal domesticShipping,
                                    BigDecimal customsDuty,
                                    BigDecimal totalAmount) {
        this.address = address;
        this.personalCustomsCode = personalCustomsCode;
        this.items = items;
        this.exchangeRate = exchangeRate;
        this.productAmount = productAmount;
        this.overseasShipping = overseasShipping;
        this.domesticShipping = domesticShipping;
        this.customsDuty = customsDuty;
        this.totalAmount = totalAmount;
    }
}
