package com.yorimichi.yorimichi.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 요청
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "メールアドレスの形式が正しくありません。")
    private String email;

    @NotBlank(message = "パスワードを入力してください。")
    private String password;
}
