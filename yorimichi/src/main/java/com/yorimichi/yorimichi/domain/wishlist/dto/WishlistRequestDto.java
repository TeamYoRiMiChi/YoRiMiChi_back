package com.yorimichi.yorimichi.domain.wishlist.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 찜 추가 요청 */
@Getter
@Setter
@NoArgsConstructor
public class WishlistRequestDto {

    @NotNull(message = "商品を指定してください。")
    private Long productId;
}
