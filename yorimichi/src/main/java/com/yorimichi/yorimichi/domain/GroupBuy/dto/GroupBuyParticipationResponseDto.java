package com.yorimichi.yorimichi.domain.GroupBuy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupBuyParticipationResponseDto {

    private Long participantId;
    private Long groupBuyId;
    private Long productId;
    private Integer quantity;
    private Integer currentQuantity;
    private Integer targetQuantity;
    private String participationStatus;
}
