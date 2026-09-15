package com.yorimichi.yorimichi.domain.admin.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 관리자 - 회원쿠폰(발급 내역) 응답
 *
 * MEMBER_COUPON 자체에는 쿠폰명·코드·회원 이메일이 없어서,
 * 매퍼에서 COUPON·MEMBER와 조인해 조회 결과를 바로 이 DTO로 채워 받습니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMemberCouponResponseDto {

    private Long memberCouponId;
    private Long couponId;
    private String couponName;
    private String couponCode;
    private Long memberId;
    private String memberEmail;
    private String memberName;
    private Long orderId;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;
    private String status;
}
