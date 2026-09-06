package com.yorimichi.yorimichi.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 장바구니 수량 변경 요청 */
@Getter
@Setter
@NoArgsConstructor
public class CartItemUpdateRequestDto {

    @NotNull(message = "数量を入力してください。")
    @Min(value = 1, message = "数量は1以上で入力してください。")
    private Integer quantity;
}
