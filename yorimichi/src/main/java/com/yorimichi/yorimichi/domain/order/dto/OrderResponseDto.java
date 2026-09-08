package com.yorimichi.yorimichi.domain.order.dto;

import lombok.Getter;

import com.yorimichi.yorimichi.domain.order.entity.Order;
import com.yorimichi.yorimichi.domain.order.entity.OrderLine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 결과
 *
 * 주문 완료 화면과 주문 상세에서 사용합니다.
 */
@Getter
public class OrderResponseDto {

    private final Long orderId;
    private final String orderNumber;
    private final String orderStatus;
    private final String orderType;

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

    private final LocalDateTime orderedAt;
    private final List<OrderLineDto> items;

    public OrderResponseDto(Order o, List<OrderLine> lines) {
        this.orderId = o.getOrderId();
        this.orderNumber = o.getOrderNumber();
        this.orderStatus = o.getOrderStatus();
        this.orderType = o.getOrderType();

        this.receiverName = o.getReceiverName();
        this.receiverPhone = o.getReceiverPhone();
        this.postalCode = o.getPostalCode();
        this.address = o.getAddress();
        this.addressDetail = o.getAddressDetail();
        this.personalCustomsCode = o.getPersonalCustomsCode();

        this.appliedExchangeRate = o.getAppliedExchangeRate();
        this.productAmount = o.getProductAmount();
        this.shippingFee = o.getShippingFee();
        this.customsDuty = o.getCustomsDuty();
        this.totalAmount = o.getTotalAmount();

        this.orderedAt = o.getOrderedAt();
        this.items = lines.stream().map(OrderLineDto::new).toList();
    }


    /** 주문 상품 하나 */
    @Getter
    public static class OrderLineDto {
        private final Long orderItemId;
        private final Long productId;
        private final String brand;
        private final String productName;
        private final String thumbnailUrl;
        private final BigDecimal priceJpy;
        private final BigDecimal priceKrw;
        private final Integer quantity;
        private final BigDecimal itemTotal;

        public OrderLineDto(OrderLine l) {
            this.orderItemId = l.getOrderItemId();
            this.productId = l.getProductId();
            this.brand = l.getBrand();
            this.productName = l.getProductName();
            this.thumbnailUrl = l.getThumbnailUrl();
            this.priceJpy = l.getPriceJpy();
            this.priceKrw = l.getPriceKrw();
            this.quantity = l.getQuantity();
            this.itemTotal = l.getItemTotal();
        }
    }
}
