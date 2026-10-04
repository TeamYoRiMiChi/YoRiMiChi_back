package com.yorimichi.yorimichi.domain.mypage.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewUpdateRequestDto {

    @NotNull(message = "星の評価を選択してください。")
    @Min(value = 1, message = "星の評価は1〜5で選択してください。")
    @Max(value = 5, message = "星の評価は1〜5で選択してください。")
    private Integer rating;

    @Size(max = 1000, message = "レビュー内容は1000文字以内で入力してください。")
    private String content;
}