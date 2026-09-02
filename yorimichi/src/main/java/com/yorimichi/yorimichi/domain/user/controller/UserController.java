package com.yorimichi.yorimichi.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.yorimichi.yorimichi.domain.user.dto.LoginRequestDto;
import com.yorimichi.yorimichi.domain.user.dto.LoginResponseDto;
import com.yorimichi.yorimichi.domain.user.dto.UserResponseDto;
import com.yorimichi.yorimichi.domain.user.dto.UserSignUpRequestDto;
import com.yorimichi.yorimichi.domain.user.service.UserService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     * POST /api/users
     */
    @PostMapping
    public ApiResponse<UserResponseDto> signup(@Valid @RequestBody UserSignUpRequestDto request) {
        return ApiResponse.success(userService.signup(request), "会員登録が完了しました。");
    }

    /**
     * 로그인
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ApiResponse.success(userService.login(request));
    }

    /**
     * 이메일 중복 확인
     * GET /api/users/check-email?email=xxx
     */
    @GetMapping("/check-email")
    public ApiResponse<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {
        boolean duplicated = userService.isEmailDuplicated(email);
        return ApiResponse.success(Map.of("duplicated", duplicated));
    }

    /**
     * 내 정보 조회 (로그인 필요)
     * GET /api/users/me
     *
     * JwtAuthenticationFilter가 토큰에서 꺼낸 memberId가 principal로 들어옵니다.
     */
    @GetMapping("/me")
    public ApiResponse<UserResponseDto> getMyInfo(@AuthenticationPrincipal Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return ApiResponse.success(userService.getMyInfo(memberId));
    }

    /**
     * 회원 단건 조회
     * GET /api/users/{memberId}
     */
    @GetMapping("/{memberId}")
    public ApiResponse<UserResponseDto> getUser(@PathVariable("memberId") Long memberId) {
        return ApiResponse.success(userService.getUser(memberId));
    }
}
