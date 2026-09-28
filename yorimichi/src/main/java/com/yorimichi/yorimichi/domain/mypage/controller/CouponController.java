package com.yorimichi.yorimichi.domain.mypage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.ClaimableCouponResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.MyCouponResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.CouponService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * 로그인한 회원의 쿠폰을 관리하는 API입니다.
 *
 * GET  /api/coupons/me               보유 쿠폰 전체 조회
 * GET  /api/coupons/claimable        받을 수 있는 이벤트 쿠폰 조회
 * POST /api/coupons/{couponId}/claim 이벤트 쿠폰 받기
 */
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * Returns every coupon owned by the authenticated member.
     */
    @GetMapping("/me")
    public ApiResponse<List<MyCouponResponseDto>> getMyCoupons(
            @CurrentMemberId Long memberId) {
        return ApiResponse.success(couponService.getMyCoupons(memberId));
    }

    /**
     * Returns coupons that the authenticated member can claim.
     */
    @GetMapping("/claimable")
    public ApiResponse<List<ClaimableCouponResponseDto>> getClaimableCoupons(
            @CurrentMemberId Long memberId) {
        return ApiResponse.success(couponService.getClaimableCoupons(memberId));
    }

    /**
     * Claims an event coupon for the authenticated member.
     */
    @PostMapping("/{couponId}/claim")
    public ApiResponse<Void> claimCoupon(
            @CurrentMemberId Long memberId,
            @PathVariable("couponId") Long couponId) {
        couponService.claimCoupon(memberId, couponId);

        return ApiResponse.<Void>success(null, "クーポンを受け取りました。");
    }
}
