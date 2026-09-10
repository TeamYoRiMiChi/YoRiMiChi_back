package com.yorimichi.yorimichi.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.yorimichi.yorimichi.domain.mypage.dto.OrderDetailItemResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.OrderDetailResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.PaymentResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.ShippingResponseDto;
import com.yorimichi.yorimichi.domain.mypage.entity.OrderDetail;
import com.yorimichi.yorimichi.domain.mypage.entity.OrderPayment;
import com.yorimichi.yorimichi.domain.mypage.entity.OrderShipping;
import com.yorimichi.yorimichi.domain.mypage.repository.OrderDetailMapper;
import com.yorimichi.yorimichi.domain.mypage.repository.OrderHistoryMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

	private final OrderHistoryMapper orderHistoryMapper;
    private final OrderDetailMapper orderDetailMapper;

    public OrderDetailResponseDto getOrderDetail(
            long orderId,
            long memberId
    ) {
        OrderDetail order =
                orderDetailMapper
                        .findOrderDetailByMemberIdAndOrderId(memberId, orderId)
                        .orElseThrow(
                            () -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
                        );

        List<OrderDetailItemResponseDto> items =
                orderHistoryMapper.findItemsByOrderId(orderId)
                        .stream()
                        .map(OrderDetailItemResponseDto::new)
                        .toList();

        OrderPayment payment =
                orderDetailMapper.findPaymentByOrderId(orderId)
                        .orElse(null);

        OrderShipping shipping =
                orderDetailMapper.findShippingByOrderId(orderId)
                        .orElse(null);

        PaymentResponseDto paymentDto =
                payment == null
                        ? null
                        : new PaymentResponseDto(payment);

        ShippingResponseDto shippingDto =
                shipping == null
                        ? null
                        : new ShippingResponseDto(shipping);

        return new OrderDetailResponseDto(
                order,
                items,
                paymentDto,
                shippingDto
        );
    }
}