package com.yorimichi.yorimichi.domain.mypage.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.mypage.dto.DeliveryStatusSummaryResponseDto;

@Mapper
public interface DeliveryStatusSummaryMapper {

	DeliveryStatusSummaryResponseDto countByMemberId(
			@Param("memberId") Long memberId
			);
}
