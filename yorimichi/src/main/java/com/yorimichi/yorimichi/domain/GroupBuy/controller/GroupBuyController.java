package com.yorimichi.yorimichi.domain.GroupBuy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyParticipationRequestDto;
import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyParticipationResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.service.GroupBuyService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 공동구매 조회 및 참여를 처리하는 API입니다.
 *
 * GET  /api/group-buys/{productId}                 공동구매 상세 조회
 * POST /api/group-buys/{productId}/participants    공동구매 참여
 * GET  /api/group-buys/{productId}/participants/me 내 참여 정보 조회
 */
@RestController
@RequestMapping("/api/group-buys")
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    /**
     * Returns the active group-buy information for a product.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<GroupBuyResponseDto>> getGroupBuy(
            @PathVariable("productId") Long productId) {
        return groupBuyService.getGroupBuyByProductId(productId)
                .map(detail -> ResponseEntity.ok(ApiResponse.success(detail)))
              
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<GroupBuyResponseDto>fail("共同購入が見つかりません。")));
    }

    /**
     * Registers the authenticated member as a participant.
     */
    @PostMapping("/{productId}/participants")
    public ApiResponse<GroupBuyParticipationResponseDto> participate(
            @CurrentMemberId Long memberId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody GroupBuyParticipationRequestDto request) {
        return ApiResponse.success(
                groupBuyService.participate(memberId, productId, request.getQuantity()),
                "共同購入への申し込みが完了しました。"
        );
    }

    /**
     * Returns the authenticated member's participation information.
     */
    @GetMapping("/{productId}/participants/me")
    public ApiResponse<GroupBuyParticipationResponseDto> getMyParticipation(
            @CurrentMemberId Long memberId,
            @PathVariable("productId") Long productId) {
        return ApiResponse.success(groupBuyService.getMyParticipation(memberId, productId));
    }
}
