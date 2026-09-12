package com.yorimichi.yorimichi.domain.mypage.dto;

import com.yorimichi.yorimichi.domain.user.entity.User;

import lombok.Getter;

@Getter
public class ProfileResponseDto {

    private final String email;
    private final String name;
    private final String phone;

    public ProfileResponseDto(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
    }
}