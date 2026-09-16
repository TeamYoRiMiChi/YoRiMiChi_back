package com.yorimichi.yorimichi.domain.mypage.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.mypage.dto.ClaimableCouponResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.MyCouponResponseDto;
import com.yorimichi.yorimichi.domain.mypage.service.CouponService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * 마이페이지 - 쿠폰함 API
 *
 * 프론트: src/api/couponApi.js, src/components/MyPage/MyCoupons/MyCoupons.jsx
 *
 * GET  /api/coupons/me              내가 보유한 쿠폰 전체 (사용 가능 / 사용 완료 / 만료 포함)
 * GET  /api/coupons/claimable       지금 받을 수 있는 이벤트 쿠폰 목록
 * POST /api/coupons/{couponId}/claim  이벤트 쿠폰 받기 (회원이 스스로 발급받음)
 *
 * 모두 로그인이 필요합니다. SecurityConfig에서 "/api/coupons/**"는 인증 없이 접근 가능한
 * 목록(permitAll)에 없으므로, 토큰이 없으면 필터 단계에서 이미 401로 막힙니다.
 * 아래 memberId null 체크는 MyProfileController와 동일하게 방어적으로 둔 것입니다.
 */
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/me")
    public ApiResponse<List<MyCouponResponseDto>> getMyCoupons(
            @AuthenticationPrincipal Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.success(couponService.getMyCoupons(memberId));
    }

    @GetMapping("/claimable")
    public ApiResponse<List<ClaimableCouponResponseDto>> getClaimableCoupons(
            @AuthenticationPrincipal Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return ApiResponse.success(couponService.getClaimableCoupons(memberId));
    }

    @PostMapping("/{couponId}/claim")
    public ApiResponse<Void> claimCoupon(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("couponId") Long couponId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        couponService.claimCoupon(memberId, couponId);

        return ApiResponse.<Void>success(null, "クーポンを受け取りました。");
    }
}
