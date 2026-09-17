package com.yorimichi.yorimichi.domain.postal.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 우편번호 검색 결과 (zipcloud 응답을 화면에 맞게 옮겨 담음)
 */
@Getter
@Builder
public class PostalAddressDto {

    private String zipcode;
    private String prefCode;
    private String address1;
    private String address2;
    private String address3;
    private String kana1;
    private String kana2;
    private String kana3;

    /** address1 + address2 + address3 를 이어붙인 값 — 화면 주소칸에 바로 채워 넣기 편하도록 */
    private String fullAddress;
}
