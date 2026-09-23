package com.yorimichi.yorimichi.domain.admin.GroupBuy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 공동구매 목록에 표시할 한 건
 *
 * PRODUCT를 조인해 상품 정보(상품명·썸네일·정가)까지 함께 내려줍니다.
 */
@Getter
@NoArgsConstructor
public class AdminGroupBuyResponseDto {

    private Long groupBuyId;
    private Long productId;

    private String productName;
    private String productNameJp;
    private String brand;
    private String thumbnailUrl;
    private BigDecimal priceJpy;

    private String title;
    private Integer targetQuantity;
    private Integer currentQuantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private LocalDateTime createdAt;

    // 목록 테이블에 등록자 정보도 바로 보여주기 위해 MEMBER를 LEFT JOIN해서 채웁니다.
    private String creatorName;
    private String creatorEmail;
}
