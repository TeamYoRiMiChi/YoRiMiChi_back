package com.yorimichi.yorimichi.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.yorimichi.yorimichi.domain.user.dto.UserResponseDto;
import com.yorimichi.yorimichi.domain.user.dto.UserSignupRequestDto;
import com.yorimichi.yorimichi.domain.user.service.UserService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponseDto> signup(@Valid @RequestBody UserSignupRequestDto request) {
        UserResponseDto response = userService.signup(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponseDto> getUser(@PathVariable Long id) {
        UserResponseDto response = userService.getUser(id);
        return ApiResponse.success(response);
    }
}