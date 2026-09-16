package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 마이페이지 - 지금 받을 수 있는 이벤트 쿠폰 응답
 *
 * 프론트: src/api/couponApi.js의 toClaimableCouponView
 *
 * COUPON 중 issueType이 ALL(누구나 발급 가능)이면서 아직 이 회원이
 * 받지 않은 것만 매퍼에서 걸러 내려줍니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimableCouponResponseDto {

    private Long couponId;
    private String couponName;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validTo;
}
