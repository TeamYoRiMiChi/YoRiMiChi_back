package com.yorimichi.yorimichi.domain.user.dto;

import lombok.Getter;

import java.time.LocalDateTime;

import com.yorimichi.yorimichi.domain.user.entity.User;

@Getter
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String phone;
    private LocalDateTime createdAt;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.phone = user.getPhone();
        this.createdAt = user.getCreatedAt();
    }
}