package com.yorimichi.yorimichi.domain.GroupBuy.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공동구매 상세조회
 * 지금은 GROUP_BUY 테이블에서 확인한 항목만 
 * 상품 정보와 실제 조회 연결은 다음 단계에서 추가
 */
@Getter
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class GroupBuyResponseDto {

    // 공동구매 번호와 해당 공동구매의 상품 번호는 서로 다름
    private Long groupBuyId;
    private Long productId;

    // 공동구매 모집 제목과 설명
    private String title;
    private String description;

    // DB 기준으로 수량 참여 인원 수를 뜻X
    private Integer targetQuantity;
    private Integer currentQuantity;

    // 모집 시작 시각과 마감 시각
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 모집 상태: RECRUITING, SUCCESS, FAILED, CANCELLED
    private String status;
}
