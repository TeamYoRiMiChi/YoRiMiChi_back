package com.yorimichi.yorimichi.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LocalLoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
