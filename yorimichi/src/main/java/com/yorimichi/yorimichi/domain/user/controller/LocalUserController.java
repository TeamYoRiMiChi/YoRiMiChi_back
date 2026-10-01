package com.yorimichi.yorimichi.domain.user.controller;

import com.yorimichi.yorimichi.domain.user.dto.*;
import com.yorimichi.yorimichi.domain.user.service.LocalAuthenticationService;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@Profile("local")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class LocalUserController {
    private final LocalAuthenticationService authentication;

    @PostMapping("/signup")
    public ApiResponse<UserResponseDto> signup(@Valid @RequestBody LocalSignupRequest request) {
        return ApiResponse.success(authentication.signup(request));
    }

    @PostMapping("/login")
    public ApiResponse<LocalAuthenticationService.LoginResult> login(@Valid @RequestBody LocalLoginRequest request) {
        return ApiResponse.success(authentication.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponseDto> me(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.success(authentication.me(Long.parseLong(jwt.getSubject())));
    }
}
