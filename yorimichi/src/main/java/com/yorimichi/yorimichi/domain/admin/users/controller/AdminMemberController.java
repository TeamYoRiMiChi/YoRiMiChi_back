package com.yorimichi.yorimichi.domain.admin.users.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.admin.users.dto.AdminMemberResponseDto;
import com.yorimichi.yorimichi.domain.admin.users.service.AdminMemberService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping
    public ApiResponse<List<AdminMemberResponseDto>> getMembers(
            @AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(adminMemberService.getMembers(memberId));
    }
}