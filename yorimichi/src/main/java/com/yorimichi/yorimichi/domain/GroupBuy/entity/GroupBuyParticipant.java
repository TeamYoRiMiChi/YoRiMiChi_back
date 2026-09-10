package com.yorimichi.yorimichi.domain.GroupBuy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyParticipant {

    private Long participantId;
    private Long groupBuyId;
    private Long memberId;
    private Integer quantity;
    private String participationStatus;
}
