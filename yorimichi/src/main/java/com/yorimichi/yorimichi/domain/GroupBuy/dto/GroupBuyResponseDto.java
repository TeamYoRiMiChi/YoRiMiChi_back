package com.yorimichi.yorimichi.domain.GroupBuy.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공동구매 상세조회
 * GROUP_BUY와 PRODUCT를 함께 조회해 상세 화면에 전달합니다.
 */
@Getter
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class GroupBuyResponseDto {

    // 공동구매 번호와 해당 공동구매의 상품 번호는 서로 다름
    private Long groupBuyId;
    private Long productId;

    // PRODUCT 테이블에서 함께 조회하는 상품 정보
    private String productName;
    private String productNameJp;
    private String brand;
    private BigDecimal priceJpy;
    private BigDecimal originalPriceJpy;
    private String thumbnailUrl;

    // 공동구매 모집 제목과 설명
    private String title;
    private String description;

    // DB 기준으로 참여 인원이 아닌 모집 수량입니다.
    private Integer targetQuantity;
    private Integer currentQuantity;

    // 모집 시작 시각과 마감 시각
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 모집 상태: RECRUITING, SUCCESS, FAILED, CANCELLED
    private String status;
}
