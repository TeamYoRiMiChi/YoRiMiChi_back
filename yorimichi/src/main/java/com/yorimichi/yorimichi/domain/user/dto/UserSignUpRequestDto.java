package com.yorimichi.yorimichi.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원가입 요청
 */
@Getter
@Setter
@NoArgsConstructor
public class UserSignUpRequestDto {

    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "メールアドレスの形式が正しくありません。")
    private String email;

    @NotBlank(message = "パスワードを入力してください。")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください。")
    private String password;

    @NotBlank(message = "お名前を入力してください。")
    private String name;

    private String phone;
}
