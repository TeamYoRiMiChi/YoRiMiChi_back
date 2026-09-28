package com.yorimichi.yorimichi.domain.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateRequestDto {

    @NotBlank(message = "お名前を入力してください。")
    @Size(max = 50, message = "お名前は50文字以内で入力してください。")
    private String name;

    @NotBlank(message = "電話番号を入力してください。")
    @Pattern(
        regexp = "^(070|080|090)-\\d{4}-\\d{4}$",
        message = "070・080・090から始まる携帯電話番号を入力してください。"
    )
    private String phone;
}
