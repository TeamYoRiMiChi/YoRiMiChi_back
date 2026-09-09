package com.yorimichi.yorimichi.domain.order.dto;

import lombok.Getter;

import com.yorimichi.yorimichi.domain.order.entity.OrderAddress;

/** 주문서에 표시할 배송지 */
@Getter
public class OrderAddressDto {

    private final Long addressId;
    private final String receiverName;
    private final String receiverPhone;
    private final String postalCode;
    private final String address;
    private final String addressDetail;

    public OrderAddressDto(OrderAddress a) {
        this.addressId = a.getAddressId();
        this.receiverName = a.getReceiverName();
        this.receiverPhone = a.getReceiverPhone();
        this.postalCode = a.getPostalCode();
        this.address = a.getAddress();
        this.addressDetail = a.getAddressDetail();
    }
}
