package com.yorimichi.yorimichi.domain.mypage.repository;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.mypage.entity.OrderHistory;
import com.yorimichi.yorimichi.domain.mypage.entity.OrderItem;

@Mapper
public interface OrderHistoryMapper {

	List<OrderHistory> findOrderHistoriesByMemberId(@Param("memberId") Long memberId);
	List<OrderItem> findItemsByOrderId(@Param("orderId") Long orderId);
}
