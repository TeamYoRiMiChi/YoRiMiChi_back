package com.yorimichi.yorimichi.domain.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InquiryCreateRequestDto {

    @NotBlank(message = "お問い合わせの種類を選択してください。")
    @Pattern(
            regexp = "^(ORDER|DELIVERY|PRODUCT|GROUP_BUY|ETC)$",
            message = "お問い合わせの種類が正しくありません。"
    )
    private String category;

    @NotBlank(message = "件名を入力してください。")
    @Size(max = 100, message = "件名は100文字以内で入力してください。")
    private String title;

    @NotBlank(message = "お問い合わせ内容を入力してください。")
    @Size(max = 1000, message = "お問い合わせ内容は1000文字以内で入力してください。")
    private String content;
}
