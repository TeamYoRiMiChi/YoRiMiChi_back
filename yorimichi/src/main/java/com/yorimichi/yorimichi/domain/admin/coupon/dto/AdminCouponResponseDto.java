package com.yorimichi.yorimichi.domain.admin.coupon.dto;

import lombok.Getter;

import com.yorimichi.yorimichi.domain.admin.coupon.entity.Coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 관리자 - 쿠폰 응답
 */
@Getter
public class AdminCouponResponseDto {

    private final Long couponId;
    private final String couponCode;
    private final String couponName;
    private final String discountType;
    private final BigDecimal discountValue;
    private final BigDecimal minOrderAmount;
    private final BigDecimal maxDiscountAmount;
    private final String issueType;
    private final LocalDateTime validFrom;
    private final LocalDateTime validTo;
    private final Integer usageLimit;
    private final Integer issuedCount;
    private final String status;

    public AdminCouponResponseDto(Coupon c) {
        this.couponId = c.getCouponId();
        this.couponCode = c.getCouponCode();
        this.couponName = c.getCouponName();
        this.discountType = c.getDiscountType();
        this.discountValue = c.getDiscountValue();
        this.minOrderAmount = c.getMinOrderAmount();
        this.maxDiscountAmount = c.getMaxDiscountAmount();
        this.issueType = c.getIssueType();
        this.validFrom = c.getValidFrom();
        this.validTo = c.getValidTo();
        this.usageLimit = c.getUsageLimit();
        this.issuedCount = c.getIssuedCount();
        this.status = c.getStatus();
    }
}
