package com.yorimichi.yorimichi.domain.GroupBuy.repository;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyResponseDto;


@Mapper
public interface GroupBuyMapper {
  
    Optional<GroupBuyResponseDto> findById(@Param("groupBuyId") Long groupBuyId);
}
