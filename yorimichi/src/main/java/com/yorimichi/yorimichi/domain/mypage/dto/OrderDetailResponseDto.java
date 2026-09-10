package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.yorimichi.yorimichi.domain.mypage.entity.OrderDetail;

import lombok.Getter;

@Getter
public class OrderDetailResponseDto {

    private final Long orderId;
    private final String orderNumber;
    private final String orderType;
    private final LocalDateTime orderedAt;
    private final String orderStatus;

    private final String receiverName;
    private final String receiverPhone;
    private final String postalCode;
    private final String address;
    private final String addressDetail;
    private final String personalCustomsCode;

    
    private final BigDecimal appliedExchangeRate;
    private final BigDecimal productAmount;
    private final BigDecimal shippingFee;
    private final BigDecimal customsDuty;
    private final BigDecimal totalAmount;

    private final List<OrderDetailItemResponseDto> items;
    private final PaymentResponseDto payment;
    private final ShippingResponseDto shipping;

    public OrderDetailResponseDto(
            OrderDetail order,
            List<OrderDetailItemResponseDto> items,
            PaymentResponseDto payment,
            ShippingResponseDto shipping
    ) {
        this.orderId = order.getOrderId();
        this.orderNumber = order.getOrderNumber();
        this.orderType = order.getOrderType();
        this.orderedAt = order.getOrderedAt();
        this.orderStatus = order.getOrderStatus();

        this.receiverName = order.getReceiverName();
        this.receiverPhone = order.getReceiverPhone();
        this.postalCode = order.getPostalCode();
        this.address = order.getAddress();
        this.addressDetail = order.getAddressDetail();
        this.personalCustomsCode = order.getPersonalCustomsCode();

        this.appliedExchangeRate = order.getAppliedExchangeRate();
        this.productAmount = order.getProductAmount();
        this.shippingFee = order.getShippingFee();
        this.customsDuty = order.getCustomsDuty();
        this.totalAmount = order.getTotalAmount();

        this.items = items;
        this.payment = payment;
        this.shipping = shipping;
    }
}