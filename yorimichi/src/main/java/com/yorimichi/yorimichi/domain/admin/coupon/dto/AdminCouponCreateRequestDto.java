package com.yorimichi.yorimichi.domain.admin.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 쿠폰 등록 요청
 *
 * issueType: ALL(누구나 발급 가능) | TARGET(운영툴에서 대상 지정 지급)
 *   - ALL로 등록해도 회원들에게 자동으로 뿌려지지는 않습니다.
 *     고객이 스스로 받아가는 이벤트 쿠폰함(고객용 쿠폰 API)에 노출되는 쿠폰이라는 뜻이고,
 *     TARGET 쿠폰은 이 화면의 "발급" 버튼(POST /api/admin/coupons/{id}/issue)으로만 지급됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class AdminCouponCreateRequestDto {

    @NotBlank
    private String couponCode;

    @NotBlank
    private String couponName;

    /** FIXED | PERCENT */
    @NotBlank
    private String discountType;

    @NotNull
    @Positive
    private BigDecimal discountValue;

    @PositiveOrZero
    private BigDecimal minOrderAmount;

    private BigDecimal maxDiscountAmount;

    /** ALL | TARGET */
    @NotBlank
    private String issueType;

    @NotNull
    private LocalDateTime validFrom;

    @NotNull
    private LocalDateTime validTo;

    /** 전체 발급 가능 수량. null이면 무제한 */
    @PositiveOrZero
    private Integer usageLimit;
}
