package com.yorimichi.yorimichi.domain.user.dto;

import lombok.Getter;

import java.time.LocalDateTime;

import com.yorimichi.yorimichi.domain.user.entity.User;

/**
 * 회원 정보 응답
 *
 * 비밀번호는 절대 포함하지 않습니다.
 */
@Getter
public class UserResponseDto {

    private final Long memberId;
    private final String email;
    private final String name;
    private final String phone;
    private final String role;
    private final String status;
    private final LocalDateTime createdAt;

    public UserResponseDto(User user) {
        this.memberId = user.getMemberId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
    }
}
