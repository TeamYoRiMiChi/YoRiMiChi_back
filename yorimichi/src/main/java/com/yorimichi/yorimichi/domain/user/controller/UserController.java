package com.yorimichi.yorimichi.domain.user.controller;

import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import com.yorimichi.yorimichi.domain.user.dto.CognitoOnboardingRequestDto;
import com.yorimichi.yorimichi.domain.user.dto.UserResponseDto;
import com.yorimichi.yorimichi.domain.user.service.UserService;
import com.yorimichi.yorimichi.global.auth.CognitoAccountService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

@RestController
@Profile("!local")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CognitoAccountService cognitoAccountService;

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
        String cognitoSub = jwt.getSubject();
        String verifiedEmail = cognitoAccountService.getVerifiedEmail(
                jwt.getClaimAsString("username")
        );
        List<String> groups = jwt.getClaimAsStringList("cognito:groups");
        boolean cognitoAdmin = groups != null && groups.contains("ADMIN");

        return ApiResponse.success(
                userService.onboardCognitoUser(
                        cognitoSub,
                        verifiedEmail,
                        cognitoAdmin,
                        request
                ),
                "会員情報の登録が完了しました。"
        );
    }
}
