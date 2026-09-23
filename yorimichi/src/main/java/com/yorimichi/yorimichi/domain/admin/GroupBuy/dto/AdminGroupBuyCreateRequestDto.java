package com.yorimichi.yorimichi.domain.admin.GroupBuy.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 공동구매 신규 등록 요청
 *
 * 상품을 새로 만드는 게 아니라, 이미 판매유형이 GROUP_BUY로 등록된
 * 기존 상품(productId)에 새 모집 라운드(GROUP_BUY 행)를 하나 여는 요청입니다.
 * 상품 자체 정보(상품명·가격·재고 등)는 상품관리 화면에서 이미 등록된 값을 그대로 씁니다.
 */
@Getter
@NoArgsConstructor
public class AdminGroupBuyCreateRequestDto {

    @NotNull
    private Long productId;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Min(1)
    private Integer targetQuantity;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;
}
