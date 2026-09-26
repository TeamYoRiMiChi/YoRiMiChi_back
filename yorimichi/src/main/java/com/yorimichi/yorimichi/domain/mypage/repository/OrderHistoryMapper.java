package com.yorimichi.yorimichi.domain.mypage.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.mypage.dto.OrderDetailItemResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.OrderDetailResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.OrderHistoryResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.OrderItemResponseDto;

@Mapper
public interface OrderHistoryMapper {

	List<OrderHistoryResponseDto> findOrderHistoriesByMemberId(
			@Param("memberId") Long memberId,
			@Param("offset") int offset,
			@Param("size") int size
			);

	List<OrderItemResponseDto> findItemsByOrderIds(
			@Param("orderIds") List<Long> orderIds
			);

	long countOrderHistoriesByMemberId(@Param("memberId") Long memberId);

	Optional<OrderDetailResponseDto> findOrderDetailByMemberIdAndOrderId(
			@Param("memberId") Long memberId,
			@Param("orderId") Long orderId
			);

	List<OrderDetailItemResponseDto> findOrderDetailItemsByOrderId(
			@Param("orderId") Long orderId
			);

	String findOrderStatusByMemberIdAndOrderId(
			@Param("memberId") Long memberId,
			@Param("orderId") Long orderId
			);

	int cancelOrder(
			@Param("memberId") Long memberId,
			@Param("orderId") Long orderId
			);

	int cancelPayment(@Param("orderId") Long orderId);

	int cancelShipping(@Param("orderId") Long orderId);
}