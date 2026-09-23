package com.yorimichi.yorimichi.domain.admin.GroupBuy.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관리자 공동구매 상태 강제 변경 요청
 *
 * status: RECRUITING, SUCCESS, FAILED, CANCELLED
 */
@Getter
@Setter
@NoArgsConstructor
public class AdminGroupBuyStatusUpdateRequestDto {

    private String status;
}
