package com.yorimichi.yorimichi.domain.admin.GroupBuy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.yorimichi.yorimichi.global.storage.ImageUrlResolver;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 공동구매 상세
 *
 * 목록보다 설명·모집자 정보·참여자 수까지 함께 내려줍니다.
 */
@Getter
@NoArgsConstructor
public class AdminGroupBuyDetailResponseDto {

    private Long groupBuyId;
    private Long productId;

    private String productName;
    private String productNameJp;
    private String brand;
    private String thumbnailUrl;
    private BigDecimal priceJpy;

    private String title;
    private String description;
    private Integer targetQuantity;
    private Integer currentQuantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long creatorId;
    private String creatorName;
    private String creatorEmail;

    /** 참여 취소하지 않은(JOINED) 참여자 수 */
    private Integer participantCount;

    // 저장소 키로 저장돼 있어도 화면에서 바로 쓸 수 있는 URL로 바꿔서 응답합니다.
    public String getThumbnailUrl() {
        return ImageUrlResolver.resolve(thumbnailUrl);
    }
}
