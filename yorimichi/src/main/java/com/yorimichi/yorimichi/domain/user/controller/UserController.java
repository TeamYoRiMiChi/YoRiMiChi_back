package com.yorimichi.yorimichi.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import com.yorimichi.yorimichi.domain.user.dto.CognitoOnboardingRequestDto;
import com.yorimichi.yorimichi.domain.user.dto.UserResponseDto;
import com.yorimichi.yorimichi.domain.user.service.UserService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Returns the currently authenticated member.
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ApiResponse<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.success(
                userService.getMyInfo(jwt.getSubject())
        );
    }

    /**
     * Creates a member record after Cognito authentication.
     * POST /api/users/onboarding
     */
    @PostMapping("/onboarding")
    public ApiResponse<UserResponseDto> onboardCognitoUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CognitoOnboardingRequestDto request
    ) {
        Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");

        if (!Boolean.TRUE.equals(emailVerified)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String cognitoSub = jwt.getSubject();
        String verifiedEmail = jwt.getClaimAsString("email");

        return ApiResponse.success(
                userService.onboardCognitoUser(
                        cognitoSub,
                        verifiedEmail,
                        request
                ),
                "会員情報の登録が完了しました。"
        );
    }
}
