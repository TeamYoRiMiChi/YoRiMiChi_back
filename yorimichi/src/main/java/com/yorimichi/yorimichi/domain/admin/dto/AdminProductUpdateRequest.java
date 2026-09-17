package com.yorimichi.yorimichi.domain.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminProductUpdateRequest {

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    @NotNull(message = "재고는 필수입니다.")
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer stock;

    @NotBlank(message = "판매 상태는 필수입니다.")
    private String status;
}
