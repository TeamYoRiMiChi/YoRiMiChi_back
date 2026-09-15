package com.yorimichi.yorimichi.domain.admin.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminCouponCreateRequestDto;
import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminCouponIssueRequestDto;
import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminCouponResponseDto;
import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminCouponSummaryResponseDto;
import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminMemberCouponResponseDto;
import com.yorimichi.yorimichi.domain.admin.coupon.service.AdminCouponService;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

import java.util.Map;

/**
 * 관리자 - 쿠폰 관리 API
 *
 * 프론트: src/api/Admin/CouponManagement/adminCouponApi.js
 *
 * GET  /api/admin/coupons             쿠폰 목록 (검색어 · 할인방식 · 상태 · 페이징)
 * GET  /api/admin/coupons/summary     요약 통계
 * POST /api/admin/coupons             쿠폰 등록
 * POST /api/admin/coupons/{id}/issue  쿠폰 수동 발급 (선택 회원 / 전체 회원)
 * POST /api/admin/coupons/{id}/stop   쿠폰 중지 (삭제 대신 status만 STOPPED로 변경)
 * GET  /api/admin/member-coupons      회원쿠폰(발급 내역) 목록
 *
 * ADMIN 권한 검사는 SecurityConfig의 "/api/admin/**" 경로 규칙을 따릅니다.
 */
@RestController
@RequiredArgsConstructor
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    @GetMapping("/api/admin/coupons")
    public ApiResponse<PageResponse<AdminCouponResponseDto>> getCoupons(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "discountType", required = false) String discountType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "8") int size) {

        return ApiResponse.success(
                adminCouponService.getCoupons(keyword, discountType, status, page, size));
    }

    @GetMapping("/api/admin/coupons/summary")
    public ApiResponse<AdminCouponSummaryResponseDto> getCouponSummary() {
        return ApiResponse.success(adminCouponService.getCouponSummary());
    }

    @PostMapping("/api/admin/coupons")
    public ApiResponse<AdminCouponResponseDto> createCoupon(
            @Valid @RequestBody AdminCouponCreateRequestDto request) {

        return ApiResponse.success(adminCouponService.createCoupon(request));
    }

    @PostMapping("/api/admin/coupons/{couponId}/issue")
    public ApiResponse<Map<String, Integer>> issueCoupon(
            @PathVariable("couponId") Long couponId,
            @RequestBody AdminCouponIssueRequestDto request) {

        int issuedCount = adminCouponService.issueCoupon(couponId, request);
        return ApiResponse.success(Map.of("issuedCount", issuedCount));
    }

    @PostMapping("/api/admin/coupons/{couponId}/stop")
    public ApiResponse<Map<String, String>> stopCoupon(@PathVariable("couponId") Long couponId) {
        adminCouponService.stopCoupon(couponId);
        return ApiResponse.success(Map.of("status", "STOPPED"));
    }

    @GetMapping("/api/admin/member-coupons")
    public ApiResponse<PageResponse<AdminMemberCouponResponseDto>> getMemberCoupons(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "8") int size) {

        return ApiResponse.success(
                adminCouponService.getMemberCoupons(keyword, status, page, size));
    }
}
