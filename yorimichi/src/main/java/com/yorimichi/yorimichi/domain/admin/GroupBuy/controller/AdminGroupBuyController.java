package com.yorimichi.yorimichi.domain.admin.GroupBuy.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyCreateRequestDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyDetailResponseDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyResponseDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyStatusUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.service.AdminGroupBuyService;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 공동구매 관리 API
 *
 * GET    /api/admin/group-buys                     목록 조회 (검색어·상태·달성률 필터·정렬·페이지네이션)
 * POST   /api/admin/group-buys                     신규 등록 (이미 있는 GROUP_BUY 상품에 새 모집 라운드 오픈)
 * GET    /api/admin/group-buys/{groupBuyId}         상세 조회
 * PATCH  /api/admin/group-buys/{groupBuyId}         제목·설명·목표수량·모집기간 수정
 * PATCH  /api/admin/group-buys/{groupBuyId}/status  상태 강제 변경
 * DELETE /api/admin/group-buys/{groupBuyId}         삭제 (참여자가 없을 때만)
 *
 * 상품 자체의 신규 생성(카테고리·가격·재고 등)은 AdminProductController에서 처리하므로,
 * 여기서는 이미 만들어진 GROUP_BUY 상품을 대상으로 모집 라운드를 관리하는 기능만 다룹니다.
 */
@RestController
@RequestMapping("/api/admin/group-buys")
@RequiredArgsConstructor
public class AdminGroupBuyController {

    private final AdminGroupBuyService adminGroupBuyService;

    // 공동구매 목록
    @GetMapping
    public ApiResponse<PageResponse<AdminGroupBuyResponseDto>> getGroupBuys(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "progress", required = false) String progress,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDirection", required = false) String sortDirection,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ApiResponse.success(
                adminGroupBuyService.getGroupBuys(
                        keyword, status, progress, sortBy, sortDirection, page, size
                )
        );
    }

    // 신규 등록 (기존 GROUP_BUY 상품에 새 모집 라운드 오픈)
    @PostMapping
    public ApiResponse<Void> createGroupBuy(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody AdminGroupBuyCreateRequestDto request
    ) {
        adminGroupBuyService.createGroupBuy(memberId, request);

        return ApiResponse.success(null, "共同購入を登録しました。");
    }

    // 공동구매 상세
    @GetMapping("/{groupBuyId}")
    public ApiResponse<AdminGroupBuyDetailResponseDto> getGroupBuyDetail(
            @PathVariable("groupBuyId") Long groupBuyId
    ) {
        return ApiResponse.success(
                adminGroupBuyService.getGroupBuyDetail(groupBuyId)
        );
    }

    // 모집 정보 수정
    @PatchMapping("/{groupBuyId}")
    public ApiResponse<Void> updateGroupBuy(
            @PathVariable("groupBuyId") Long groupBuyId,
            @RequestBody AdminGroupBuyUpdateRequestDto request
    ) {
        adminGroupBuyService.updateGroupBuy(groupBuyId, request);

        return ApiResponse.success(null, "共同購入情報を修正しました。");
    }

    // 상태 강제 변경
    @PatchMapping("/{groupBuyId}/status")
    public ApiResponse<Void> updateStatus(
            @PathVariable("groupBuyId") Long groupBuyId,
            @RequestBody AdminGroupBuyStatusUpdateRequestDto request
    ) {
        adminGroupBuyService.updateStatus(groupBuyId, request);

        return ApiResponse.success(null, "共同購入ステータスを変更しました。");
    }

    // 삭제 (참여자가 없을 때만)
    @DeleteMapping("/{groupBuyId}")
    public ApiResponse<Void> deleteGroupBuy(
            @PathVariable("groupBuyId") Long groupBuyId
    ) {
        adminGroupBuyService.deleteGroupBuy(groupBuyId);

        return ApiResponse.success(null, "共同購入を削除しました。");
    }
}
