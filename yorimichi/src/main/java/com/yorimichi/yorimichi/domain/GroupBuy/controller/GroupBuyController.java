package com.yorimichi.yorimichi.domain.GroupBuy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyParticipationRequestDto;
import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyParticipationResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.service.GroupBuyService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/group-buys")
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<GroupBuyResponseDto>> getGroupBuy(
            @PathVariable("productId") Long productId) {
        return groupBuyService.getGroupBuyByProductId(productId)
                .map(detail -> ResponseEntity.ok(ApiResponse.success(detail)))
              
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<GroupBuyResponseDto>fail("共同購入が見つかりません。")));
    }

    @PostMapping("/{productId}/participants")
    public ApiResponse<GroupBuyParticipationResponseDto> participate(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody GroupBuyParticipationRequestDto request) {

        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.success(
                groupBuyService.participate(memberId, productId, request.getQuantity()),
                "共同購入への申し込みが完了しました。"
        );
    }

    @GetMapping("/{productId}/participants/me")
    public ApiResponse<GroupBuyParticipationResponseDto> getMyParticipation(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("productId") Long productId) {

        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.success(groupBuyService.getMyParticipation(memberId, productId));
    }
}
