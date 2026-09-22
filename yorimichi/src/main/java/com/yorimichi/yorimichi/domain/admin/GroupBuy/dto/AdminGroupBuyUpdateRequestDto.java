package com.yorimichi.yorimichi.domain.admin.GroupBuy.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관리자 공동구매 수정 요청
 *
 * 제목·설명·목표 수량·모집 기간만 고칠 수 있습니다.
 * current_quantity(참여 수량)는 실제 참여를 통해서만 바뀌고,
 * status는 별도의 상태 변경 엔드포인트에서만 바뀝니다 — 여기서 함께
 * 건드리면 참여 처리 로직(GroupBuyService.participate)과 어긋날 수 있어서
 * 의도적으로 분리했습니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class AdminGroupBuyUpdateRequestDto {

    private String title;
    private String description;
    private Integer targetQuantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
