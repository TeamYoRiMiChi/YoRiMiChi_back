package com.yorimichi.yorimichi.domain.GroupBuy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyParticipationRequestDto {

    @NotNull
    @Min(1)
    private Integer quantity;
}
