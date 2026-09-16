package com.yorimichi.yorimichi.domain.mypage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 마이페이지 - 보유 쿠폰(MEMBER_COUPON) 응답
 *
 * 프론트: src/api/couponApi.js의 toMyCouponView
 *
 * MEMBER_COUPON 자체에는 쿠폰명 등이 없어서, 매퍼에서 COUPON과 조인해
 * 조회 결과를 바로 이 DTO로 채워 받습니다.
 *
 * status는 admin(AdminMemberCouponResponseDto)과 동일하게
 * "AVAILABLE인데 유효기간이 지났으면 EXPIRED로 간주"한 실질 상태입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCouponResponseDto {

    private Long memberCouponId;
    private Long couponId;
    private String couponName;
    private String couponCode;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validTo;
    private String status;
}
