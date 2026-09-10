package com.yorimichi.yorimichi.domain.mypage.repository;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.mypage.entity.OrderDetail;
import com.yorimichi.yorimichi.domain.mypage.entity.OrderPayment;
import com.yorimichi.yorimichi.domain.mypage.entity.OrderShipping;

@Mapper
public interface OrderDetailMapper {

    Optional<OrderDetail> findOrderDetailByMemberIdAndOrderId(
            @Param("memberId") Long memberId,
            @Param("orderId") Long orderId
    );

    Optional<OrderPayment> findPaymentByOrderId(
            @Param("orderId") Long orderId
    );

    Optional<OrderShipping> findShippingByOrderId(
            @Param("orderId") Long orderId
    );
}