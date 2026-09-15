package com.yorimichi.yorimichi.domain.admin.coupon.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 쿠폰 수동 발급 요청
 *
 * - issueToAll이 true면 emails는 무시하고 활동 중인 전체 회원에게 발급합니다.
 * - issueToAll이 false면 emails에 담긴 이메일의 회원에게만 발급합니다.
 * - 이미 이 쿠폰을 발급받은 회원은 다시 발급하지 않습니다(중복 발급 방지).
 */
@Getter
@Setter
@NoArgsConstructor
public class AdminCouponIssueRequestDto {

    private List<String> emails;
    private boolean issueToAll;
}
