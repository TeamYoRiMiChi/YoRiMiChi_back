package com.yorimichi.yorimichi.domain.admin.orders.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderDetailItemResponseDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderDetailResponseDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderResponseDto;
import com.yorimichi.yorimichi.domain.admin.orders.dto.AdminOrderSummaryResponseDto;

@Mapper
public interface AdminOrderMapper {

	// 주문 조회
    List<AdminOrderResponseDto> findOrders(
            @Param("keyword") String keyword,
            @Param("orderType") String orderType,
            @Param("orderStatus") String orderStatus,
            @Param("shippingStatus") String shippingStatus,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countOrders(
            @Param("keyword") String keyword,
            @Param("orderType") String orderType,
            @Param("orderStatus") String orderStatus,
            @Param("shippingStatus") String shippingStatus
    );

    // 주문 요약
    AdminOrderSummaryResponseDto findOrderSummary();
    
    String findOrderStatus(@Param("orderId") Long orderId);

    String findPaymentStatus(@Param("orderId") Long orderId);
    
    int countShippingInfo(@Param("orderId") Long orderId);

    // 주문 상태 수정
    int updateOrderStatus(
            @Param("orderId") Long orderId,
            @Param("orderStatus") String orderStatus
    );

    // 결제 상태 수정
    int updatePaymentStatus(
            @Param("orderId") Long orderId,
            @Param("paymentStatus") String paymentStatus
    );

    // 배송 상태 수정
    int updateShippingStatus(
            @Param("orderId") Long orderId,
            @Param("shippingStatus") String shippingStatus
    );

    // 배송 정보 수정
    int updateShippingInfo(
            @Param("orderId") Long orderId,
            @Param("carrier") String carrier,
            @Param("trackingNumber") String trackingNumber
    );
    
    // 주문 상세
    AdminOrderDetailResponseDto findOrderDetail(
            @Param("orderId") Long orderId
    );
    
    // 주문 상세 내 상품 상세
    List<AdminOrderDetailItemResponseDto> findOrderDetailItems(
            @Param("orderId") Long orderId
    );
}