package com.yorimichi.yorimichi.domain.mypage.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.admin.coupon.entity.Coupon;
import com.yorimichi.yorimichi.domain.mypage.dto.ClaimableCouponResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.MyCouponResponseDto;

/**
 * 마이페이지 - 쿠폰함 DB 접근
 *
 * COUPON · MEMBER_COUPON 테이블은 admin 쿠폰 관리 패키지(domain.admin.coupon)에서
 * 이미 관리하고 있어 엔티티(Coupon)는 그대로 재사용하고, 고객용 조회·발급·주문 적용에
 * 필요한 쿼리만 이 매퍼에 따로 둡니다.
 */
@Mapper
public interface CouponMapper {

    /** 로그인 회원이 보유한 쿠폰 전체 (사용 가능 / 사용 완료 / 만료 포함) */
    List<MyCouponResponseDto> findMyCoupons(@Param("memberId") Long memberId);

    /**
     * 로그인 회원이 지금 받을 수 있는 이벤트 쿠폰 목록.
     * issueType이 ALL(누구나 발급 가능)이고, 아직 발급받지 않았고,
     * 발급 기간·수량이 남아있는 쿠폰만 내려줍니다.
     */
    List<ClaimableCouponResponseDto> findClaimableCoupons(@Param("memberId") Long memberId);

    /**
     * 보유 쿠폰 단건 (본인 소유 확인 포함).
     * 주문에 쿠폰을 적용하기 전, 실제로 이 회원 것이 맞는지 · 쓸 수 있는 상태인지
     * 검증할 때 씁니다. findMyCoupons와 같은 형태(실질 상태 포함)로 내려줍니다.
     */
    Optional<MyCouponResponseDto> findMyCouponById(@Param("memberCouponId") Long memberCouponId,
                                                    @Param("memberId") Long memberId);

    /**
     * 주문에 쿠폰 사용 처리.
     * status가 AVAILABLE일 때만 USED로 바뀌므로, 동시에 같은 쿠폰으로 두 번 주문해도
     * 한쪽만 성공합니다 (반환값 0이면 이미 다른 주문에 쓰였다는 뜻).
     */
    int markCouponUsed(@Param("memberCouponId") Long memberCouponId, @Param("orderId") Long orderId);

    /** 쿠폰 단건 조회 (발급 가능 여부 검증용) */
    Optional<Coupon> findCouponById(@Param("couponId") Long couponId);

    /** 이미 발급받은 쿠폰인지 여부 */
    boolean existsMemberCoupon(@Param("couponId") Long couponId, @Param("memberId") Long memberId);

    /** 회원이 스스로 쿠폰 발급받기 (MEMBER_COUPON에 한 행 추가) */
    int insertMemberCoupon(@Param("couponId") Long couponId, @Param("memberId") Long memberId);

    /** 발급 수량(issued_count) +1 */
    int increaseIssuedCount(@Param("couponId") Long couponId);
}
