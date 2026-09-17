package com.yorimichi.yorimichi.domain.admin.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원별 쿠폰 발급 · 사용 이력 (MEMBER_COUPON 테이블)
 *
 * - orderId: 이 쿠폰이 실제로 사용된 주문. 미사용 상태면 null
 *            (한 주문에는 쿠폰을 하나만 적용한다고 가정 → order_id UNIQUE)
 * - status : AVAILABLE(사용가능), USED(사용완료), EXPIRED(기간만료로 소멸)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCoupon {

    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_USED = "USED";
    public static final String STATUS_EXPIRED = "EXPIRED";

    private Long memberCouponId;
    private Long couponId;
    private Long memberId;
    private Long orderId;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
