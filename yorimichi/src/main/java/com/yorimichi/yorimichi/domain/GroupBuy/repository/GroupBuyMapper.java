package com.yorimichi.yorimichi.domain.GroupBuy.repository;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.entity.GroupBuyParticipant;


@Mapper
public interface GroupBuyMapper {

    Optional<GroupBuyResponseDto> findByProductId(@Param("productId") Long productId);

    Optional<GroupBuyResponseDto> findByProductIdForUpdate(@Param("productId") Long productId);

    Optional<GroupBuyParticipant> findParticipation(@Param("groupBuyId") Long groupBuyId,
                                                    @Param("memberId") Long memberId);

    void insertParticipation(GroupBuyParticipant participant);

    void rejoinParticipation(@Param("participantId") Long participantId,
                             @Param("quantity") Integer quantity);

    void increaseParticipationQuantity(@Param("participantId") Long participantId,
                                       @Param("quantity") Integer quantity);

    void increaseCurrentQuantity(@Param("groupBuyId") Long groupBuyId,
                                 @Param("quantity") Integer quantity);

    void completeGroupBuy(@Param("groupBuyId") Long groupBuyId);
}
