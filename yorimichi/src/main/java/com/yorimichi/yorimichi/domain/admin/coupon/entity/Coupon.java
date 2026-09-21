package com.yorimichi.yorimichi.domain.admin.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 쿠폰 정책 (COUPON 테이블)
 *
 * - discountType : FIXED(정액할인), PERCENT(정률할인)
 * - discountValue: FIXED면 엔화 정액, PERCENT면 0~100 사이 퍼센트값
 * - maxDiscountAmount: PERCENT 할인일 때 할인 금액 상한. FIXED면 null
 * - issueType    : ALL(누구나 발급 가능), TARGET(운영툴에서 대상 지정 지급)
 * - usageLimit   : 전체 발급 가능 수량. null이면 무제한
 * - issuedCount  : 지금까지 발급된 수량 (발급될 때 +1 되는 캐시성 카운터)
 * - status       : ACTIVE, STOPPED(운영자가 중지), EXPIRED
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    public static final String DISCOUNT_TYPE_FIXED = "FIXED";
    public static final String DISCOUNT_TYPE_PERCENT = "PERCENT";

    public static final String ISSUE_TYPE_ALL = "ALL";
    public static final String ISSUE_TYPE_TARGET = "TARGET";

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_STOPPED = "STOPPED";
    public static final String STATUS_EXPIRED = "EXPIRED";

    private Long couponId;
    private String couponCode;
    private String couponName;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private String issueType;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Integer usageLimit;
    private Integer issuedCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 지금 시점에서 유효기간이 지났는지 (status 컬럼과 별개로 날짜만 봄) */
    public boolean isPastValidTo() {
        return validTo != null && validTo.isBefore(LocalDateTime.now());
    }

    /** 지금 발급해도 되는 상태인지 (운영자가 중지하지도, 만료 처리되지도 않음) */
    public boolean isIssuable() {
        return STATUS_ACTIVE.equals(status);
    }
}
