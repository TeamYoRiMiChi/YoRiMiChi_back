package com.yorimichi.yorimichi.domain.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 배송지 등록/수정 요청 (마이페이지 - 배송지 관리)
 *
 * 기본 배송지 여부는 여기서 직접 받지 않습니다.
 * 첫 배송지는 서비스에서 자동으로 기본 배송지가 되고,
 * 기본 배송지를 지우면 남은 배송지 중 하나가 자동으로 승격됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class AddressRequestDto {

    @NotBlank(message = "配送先の名前を入力してください。")
    @Size(max = 50, message = "配送先の名前は50文字以内で入力してください。")
    private String addressName;

    @NotBlank(message = "受取人のお名前を入力してください。")
    @Size(max = 50, message = "受取人のお名前は50文字以内で入力してください。")
    private String receiverName;

    @NotBlank(message = "連絡先を入力してください。")
    @Size(max = 20, message = "連絡先は20文字以内で入力してください。")
    private String receiverPhone;

    @NotBlank(message = "郵便番号を入力してください。")
    @Size(max = 10, message = "郵便番号は10文字以内で入力してください。")
    private String postalCode;

    @NotBlank(message = "住所を入力してください。")
    @Size(max = 255, message = "住所は255文字以内で入力してください。")
    private String address;

    @Size(max = 255, message = "詳細住所は255文字以内で入力してください。")
    private String addressDetail;
}
