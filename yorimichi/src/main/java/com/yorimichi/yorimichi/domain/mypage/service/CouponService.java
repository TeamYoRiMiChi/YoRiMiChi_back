package com.yorimichi.yorimichi.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.admin.coupon.entity.Coupon;
import com.yorimichi.yorimichi.domain.mypage.dto.ClaimableCouponResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.MyCouponResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.CouponMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * 마이페이지 - 쿠폰함 서비스
 */
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponMapper couponMapper;

    /** 로그인 회원이 보유한 쿠폰 전체 */
    @Transactional(readOnly = true)
    public List<MyCouponResponseDto> getMyCoupons(Long memberId) {
        return couponMapper.findMyCoupons(memberId);
    }

    /** 로그인 회원이 지금 받을 수 있는 이벤트 쿠폰 목록 */
    @Transactional(readOnly = true)
    public List<ClaimableCouponResponseDto> getClaimableCoupons(Long memberId) {
        return couponMapper.findClaimableCoupons(memberId);
    }

    /**
     * 이벤트 쿠폰을 회원이 스스로 발급받습니다.
     *
     * TARGET(운영툴에서 대상 지정 지급)인 쿠폰은 여기서 받을 수 없고,
     * ACTIVE + issueType ALL + 유효기간 내 + 수량 여유가 있을 때만 발급됩니다.
     */
    @Transactional
    public void claimCoupon(Long memberId, Long couponId) {
        Coupon coupon = couponMapper.findCouponById(couponId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUPON_NOT_FOUND));

        if (!coupon.isIssuable() || !Coupon.ISSUE_TYPE_ALL.equals(coupon.getIssueType())) {
            throw new CustomException(ErrorCode.COUPON_NOT_CLAIMABLE);
        }

        if (coupon.isPastValidTo()) {
            throw new CustomException(ErrorCode.COUPON_NOT_CLAIMABLE);
        }

        if (coupon.getUsageLimit() != null && coupon.getIssuedCount() >= coupon.getUsageLimit()) {
            throw new CustomException(ErrorCode.COUPON_USAGE_LIMIT_EXCEEDED);
        }

        if (couponMapper.existsMemberCoupon(couponId, memberId)) {
            throw new CustomException(ErrorCode.COUPON_ALREADY_CLAIMED);
        }

        couponMapper.insertMemberCoupon(couponId, memberId);
        couponMapper.increaseIssuedCount(couponId);
    }
}
