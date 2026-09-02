package com.yorimichi.yorimichi.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 응답
 *
 * accessToken은 프론트가 Redux에 저장하고,
 * 이후 요청마다 Authorization 헤더에 담아 보냅니다.
 */
@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;
}
