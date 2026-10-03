package com.yorimichi.yorimichi.domain.admin.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AdminProductImageRegisterRequest(
        @NotEmpty @Size(max = 10) List<@NotBlank String> imageKeys
) {
}
