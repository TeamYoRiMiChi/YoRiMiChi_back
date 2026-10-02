package com.yorimichi.yorimichi.domain.mypage.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.mypage.dto.DeliveryTrackingResponseDto;

@Mapper
public interface DeliveryTrackingMapper {

	List<DeliveryTrackingResponseDto> findDeliveryTrackingsByMemberId(
			@Param("memberId") Long memberId, 
			@Param("offset") int offset,
			@Param("size") int size
		);
	
	long countDeliveryTrackingsByMemberId(
		@Param("memberId") Long memberId	
		);
}
