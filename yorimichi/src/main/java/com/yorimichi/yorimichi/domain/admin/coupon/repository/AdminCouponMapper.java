package com.yorimichi.yorimichi.domain.admin.coupon.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminCouponSummaryResponseDto;
import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminMemberCouponResponseDto;
import com.yorimichi.yorimichi.domain.admin.coupon.entity.Coupon;

import java.util.List;
import java.util.Optional;

/**
 * 관리자 - 쿠폰 DB 접근
 */
@Mapper
public interface AdminCouponMapper {

    /**
     * 조건에 맞는 쿠폰 목록 (페이지 단위)
     *
     * @param keyword      쿠폰명 · 쿠폰코드 검색어. null이면 전체
     * @param discountType FIXED | PERCENT. null이면 전체
     * @param status       ACTIVE | STOPPED | EXPIRED. null이면 전체
     * @param offset       건너뛸 개수
     * @param size         가져올 개수
     */
    List<Coupon> findAll(@Param("keyword") String keyword,
                          @Param("discountType") String discountType,
                          @Param("status") String status,
                          @Param("offset") int offset,
                          @Param("size") int size);

    /** 같은 조건의 전체 개수 (페이지 수 계산용) */
    long countAll(@Param("keyword") String keyword,
                  @Param("discountType") String discountType,
                  @Param("status") String status);

    /** 쿠폰 단건 조회 */
    Optional<Coupon> findById(@Param("couponId") Long couponId);

    /** 쿠폰 코드 중복 여부 */
    boolean existsByCouponCode(@Param("couponCode") String couponCode);

    /** 쿠폰 등록. 성공하면 coupon.couponId에 채번된 PK가 채워집니다. */
    int insertCoupon(Coupon coupon);

    /** 발급 수량(issued_count) 누적 */
    int increaseIssuedCount(@Param("couponId") Long couponId, @Param("amount") int amount);

    /** 쿠폰 상태 변경 (중지 등). 실제 삭제 대신 status만 바꿉니다. */
    int updateStatus(@Param("couponId") Long couponId, @Param("status") String status);

    /** 요약 통계 (전체 / 사용가능 / 만료 / 회원 발급 건수) */
    AdminCouponSummaryResponseDto getCouponSummary();

    /**
     * 조건에 맞는 회원쿠폰(발급 내역) 목록 (페이지 단위)
     * COUPON·MEMBER와 조인해서 쿠폰명 · 회원 이메일까지 함께 내려줍니다.
     *
     * @param keyword 회원 이메일 · 이름, 쿠폰명 · 쿠폰코드 검색어. null이면 전체
     * @param status  AVAILABLE | USED | EXPIRED. null이면 전체
     */
    List<AdminMemberCouponResponseDto> findMemberCoupons(@Param("keyword") String keyword,
                                                          @Param("status") String status,
                                                          @Param("offset") int offset,
                                                          @Param("size") int size);

    /** 같은 조건의 전체 개수 */
    long countMemberCoupons(@Param("keyword") String keyword,
                             @Param("status") String status);

    /**
     * 지정한 이메일 중 실제로 발급 대상(활동 회원 + 아직 미발급)이 되는 인원 수.
     * usageLimit이 있는 쿠폰에서, 발급 전에 남은 수량과 비교해 초과 여부를 미리 확인할 때 씁니다.
     */
    long countEligibleEmails(@Param("couponId") Long couponId, @Param("emails") List<String> emails);

    /**
     * 활동 중인 전체 회원 중 실제로 발급 대상(아직 미발급)이 되는 인원 수.
     * usageLimit이 있는 쿠폰에서, 전체 회원 발급 전에 남은 수량과 비교해 초과 여부를 미리 확인할 때 씁니다.
     */
    long countEligibleAllMembers(@Param("couponId") Long couponId);

    /**
     * 지정한 이메일의 활동 회원들에게 쿠폰 발급 (이미 발급받은 회원은 제외)
     * @return 새로 발급된 건수
     */
    int issueToEmails(@Param("couponId") Long couponId, @Param("emails") List<String> emails);

    /**
     * 활동 중인 전체 회원에게 쿠폰 발급 (이미 발급받은 회원은 제외)
     * usageLimit 초과 여부는 서비스 레이어에서 countEligibleAllMembers로 미리 확인 후 호출합니다.
     *
     * @return 새로 발급된 건수
     */
    int issueToAllMembers(@Param("couponId") Long couponId);
}
