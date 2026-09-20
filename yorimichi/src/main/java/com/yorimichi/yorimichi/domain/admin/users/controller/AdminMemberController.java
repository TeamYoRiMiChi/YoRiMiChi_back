package com.yorimichi.yorimichi.domain.admin.users.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.admin.users.dto.AdminMemberResponseDto;
import com.yorimichi.yorimichi.domain.admin.users.dto.AdminMemberStatusUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.users.service.AdminMemberService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import jakarta.validation.Valid;
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
    
    @PatchMapping("/{memberId}/status")
    public ApiResponse<Void> updateMemberStatus(
    		@AuthenticationPrincipal Long adminMemberId,
    		@PathVariable("memberId") Long targetMemberId,
    		@Valid @RequestBody AdminMemberStatusUpdateRequestDto request) {
    	adminMemberService.updateMemberStatus(adminMemberId, targetMemberId, request.getStatus());
    	
    	return ApiResponse.success(null);
    }
    
    @PatchMapping("/{memberId}/demote")
    public ApiResponse<Void> demoteAdminToUser(
            @AuthenticationPrincipal Long adminMemberId,
            @PathVariable("memberId") Long targetMemberId) {

        adminMemberService.demoteAdminToUser(adminMemberId, targetMemberId);
        return ApiResponse.success(null);
    }
    
}