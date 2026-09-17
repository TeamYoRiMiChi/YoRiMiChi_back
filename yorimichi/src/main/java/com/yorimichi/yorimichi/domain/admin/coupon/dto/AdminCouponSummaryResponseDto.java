package com.yorimichi.yorimichi.domain.admin.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 - 쿠폰 요약 통계 응답
 *
 * activeCoupons : status = ACTIVE 이면서 아직 유효기간(valid_to)이 지나지 않은 쿠폰 수
 * expiredCoupons: status = EXPIRED 이거나 유효기간이 지난 쿠폰 수
 * stoppedCoupons: status = STOPPED (운영자가 중지, 유효기간과 무관) 인 쿠폰 수
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCouponSummaryResponseDto {

    private long totalCoupons;
    private long activeCoupons;
    private long expiredCoupons;
    private long stoppedCoupons;
    private long issuedMemberCoupons;
}
