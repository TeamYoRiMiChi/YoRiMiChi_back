package com.yorimichi.yorimichi.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 (MEMBER 테이블)
 *
 * - password: 소셜 로그인 전용 회원은 NULL
 * - status: ACTIVE, WITHDRAWN, SUSPENDED
 * - withdrawnAt: 탈퇴 시각 (soft delete, row는 삭제하지 않음)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long memberId;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String personalCustomsCode;
    private String role;
    private String status;
    private LocalDateTime withdrawnAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 정상적으로 로그인할 수 있는 상태인지 */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    /** 소셜 로그인 전용 계정인지 (비밀번호 없음) */
    public boolean isSocialOnly() {
        return password == null || password.isBlank();
    }
}
