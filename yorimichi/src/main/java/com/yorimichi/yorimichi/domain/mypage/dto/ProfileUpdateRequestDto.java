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
    	regexp = "^\\d{2,3}-\\d{4}-\\d{4}$",
    	message = "電話番号の形式が正しくありません。"
    )
    private String phone;

    @Size(
        min = 8,
        max = 100,
        message = "新しいパスワードは8文字以上で入力してください。"
    )
    
    @Pattern(
    	regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
    	message = "パスワードには英字と数字を含めてください。"
    )
    private String newPassword;
}
