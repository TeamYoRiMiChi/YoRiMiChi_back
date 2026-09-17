package com.yorimichi.yorimichi.domain.admin.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminCouponCreateRequestDto;
import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminCouponIssueRequestDto;
import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminCouponResponseDto;
import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminCouponSummaryResponseDto;
import com.yorimichi.yorimichi.domain.admin.coupon.dto.AdminMemberCouponResponseDto;
import com.yorimichi.yorimichi.domain.admin.coupon.entity.Coupon;
import com.yorimichi.yorimichi.domain.admin.coupon.repository.AdminCouponMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.PageResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 관리자 - 쿠폰 관리 서비스
 */
@Service
@RequiredArgsConstructor
public class AdminCouponService {

    private final AdminCouponMapper adminCouponMapper;

    /** 쿠폰 목록 (검색어 · 할인방식 · 상태 · 페이징) */
    @Transactional(readOnly = true)
    public PageResponse<AdminCouponResponseDto> getCoupons(String keyword, String discountType,
                                                             String status, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = size > 0 ? size : 8;
        int offset = (safePage - 1) * safeSize;

        List<Coupon> coupons = adminCouponMapper.findAll(keyword, discountType, status, offset, safeSize);
        long totalElements = adminCouponMapper.countAll(keyword, discountType, status);

        List<AdminCouponResponseDto> content = coupons.stream()
                .map(AdminCouponResponseDto::new)
                .toList();

        return new PageResponse<>(content, safePage, safeSize, totalElements);
    }

    /** 요약 통계 */
    @Transactional(readOnly = true)
    public AdminCouponSummaryResponseDto getCouponSummary() {
        return adminCouponMapper.getCouponSummary();
    }

    /** 쿠폰 등록 */
    @Transactional
    public AdminCouponResponseDto createCoupon(AdminCouponCreateRequestDto request) {
        validateDiscountType(request.getDiscountType());
        validateIssueType(request.getIssueType());

        if (adminCouponMapper.existsByCouponCode(request.getCouponCode())) {
            throw new CustomException(ErrorCode.DUPLICATE_COUPON_CODE);
        }

        Coupon coupon = Coupon.builder()
                .couponCode(request.getCouponCode())
                .couponName(request.getCouponName())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount() != null
                        ? request.getMinOrderAmount() : BigDecimal.ZERO)
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .issueType(request.getIssueType())
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .usageLimit(request.getUsageLimit())
                .issuedCount(0)
                .status(Coupon.STATUS_ACTIVE)
                .build();

        adminCouponMapper.insertCoupon(coupon);

        return new AdminCouponResponseDto(coupon);
    }

    /** 회원쿠폰(발급 내역) 목록 (검색어 · 상태 · 페이징) */
    @Transactional(readOnly = true)
    public PageResponse<AdminMemberCouponResponseDto> getMemberCoupons(String keyword, String status,
                                                                         int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = size > 0 ? size : 8;
        int offset = (safePage - 1) * safeSize;

        List<AdminMemberCouponResponseDto> content =
                adminCouponMapper.findMemberCoupons(keyword, status, offset, safeSize);
        long totalElements = adminCouponMapper.countMemberCoupons(keyword, status);

        return new PageResponse<>(content, safePage, safeSize, totalElements);
    }

    /**
     * 쿠폰 수동 발급 (선택한 회원 또는 전체 회원)
     * 이미 발급받은 회원은 매퍼에서 걸러지므로 중복 발급되지 않습니다.
     *
     * usageLimit(전체 발급 가능 수량)이 걸려 있는 쿠폰은 남은 수량(usageLimit - issuedCount)을 넘어서지 않도록 합니다.
     * 전체 회원 발급이든 선택한 회원 발급이든, 발급 대상 인원이 남은 수량을 넘으면
     * 일부만 잘라서 발급하지 않고 아예 발급 자체를 막고 예외를 던집니다.
     * (자동으로 몇 명만 발급되는 상황 자체를 없애, 관리자가 항상 "전원 발급" 또는 "발급 거부" 둘 중 하나만 보게 함)
     *
     * @return 새로 발급된 건수
     */
    @Transactional
    public int issueCoupon(Long couponId, AdminCouponIssueRequestDto request) {
        Coupon coupon = adminCouponMapper.findById(couponId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUPON_NOT_FOUND));

        if (!coupon.isIssuable()) {
            throw new CustomException(ErrorCode.INVALID_COUPON_ISSUE_REQUEST);
        }

        // usageLimit이 null이면 무제한, 아니면 남은 발급 가능 수량
        Integer usageLimit = coupon.getUsageLimit();
        Integer remaining = usageLimit != null ? usageLimit - coupon.getIssuedCount() : null;

        if (remaining != null && remaining <= 0) {
            throw new CustomException(ErrorCode.COUPON_USAGE_LIMIT_EXCEEDED);
        }

        int issuedCount;
        if (request.isIssueToAll()) {
            if (remaining != null) {
                long eligible = adminCouponMapper.countEligibleAllMembers(couponId);
                if (eligible > remaining) {
                    throw new CustomException(ErrorCode.COUPON_USAGE_LIMIT_EXCEEDED);
                }
            }

            issuedCount = adminCouponMapper.issueToAllMembers(couponId);
        } else {
            List<String> emails = request.getEmails();
            if (emails == null || emails.isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_COUPON_ISSUE_REQUEST);
            }

            if (remaining != null) {
                long eligible = adminCouponMapper.countEligibleEmails(couponId, emails);
                if (eligible > remaining) {
                    throw new CustomException(ErrorCode.COUPON_USAGE_LIMIT_EXCEEDED);
                }
            }

            issuedCount = adminCouponMapper.issueToEmails(couponId, emails);
        }

        if (issuedCount > 0) {
            adminCouponMapper.increaseIssuedCount(couponId, issuedCount);
        }

        return issuedCount;
    }

    /**
     * 쿠폰 중지.
     * 실제로 행을 삭제하지 않고 status만 STOPPED로 바꿉니다 — 이미 발급된 MEMBER_COUPON 내역은
     * COUPON을 FK로 물고 있어서 삭제하면 이력이 깨지기 때문에, 신규 발급만 막는 방식입니다.
     * ACTIVE 상태인 쿠폰만 중지할 수 있습니다 (이미 만료됐거나 중지된 쿠폰은 대상 아님).
     */
    @Transactional
    public void stopCoupon(Long couponId) {
        Coupon coupon = adminCouponMapper.findById(couponId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUPON_NOT_FOUND));

        if (!Coupon.STATUS_ACTIVE.equals(coupon.getStatus())) {
            throw new CustomException(ErrorCode.INVALID_COUPON_STATUS_CHANGE);
        }

        adminCouponMapper.updateStatus(couponId, Coupon.STATUS_STOPPED);
    }

    private void validateDiscountType(String discountType) {
        if (!Coupon.DISCOUNT_TYPE_FIXED.equals(discountType)
                && !Coupon.DISCOUNT_TYPE_PERCENT.equals(discountType)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validateIssueType(String issueType) {
        if (!Coupon.ISSUE_TYPE_ALL.equals(issueType)
                && !Coupon.ISSUE_TYPE_TARGET.equals(issueType)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
