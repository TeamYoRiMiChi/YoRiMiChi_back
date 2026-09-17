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

    /*
     * 배송지 (전부 任意 — 셋 다 입력하면 가입 직후 자동으로
     * 기본 배송지(自宅)로 등록됩니다. 받는분 이름/연락처는 위 name/phone을 그대로 씁니다)
     */
    private String postalCode;
    private String address;
    private String addressDetail;
}
