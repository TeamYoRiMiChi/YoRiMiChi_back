package com.yorimichi.yorimichi.domain.admin.categories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminCategoryUpdateRequestDto {

    @NotBlank(message = "カテゴリー名を入力してください。")
    @Size(max = 100, message = "カテゴリー名は100文字以内で入力してください。")
    private String categoryName;
}