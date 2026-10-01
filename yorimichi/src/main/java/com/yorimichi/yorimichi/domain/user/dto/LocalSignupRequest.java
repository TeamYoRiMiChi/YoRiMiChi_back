package com.yorimichi.yorimichi.domain.user.dto;

import jakarta.validation.constraints.*;

public record LocalSignupRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 100) String name,
        @Pattern(regexp = "^(070|080|090)-\\d{4}-\\d{4}$") String phone,
        String postalCode, String address, String addressDetail) {}
