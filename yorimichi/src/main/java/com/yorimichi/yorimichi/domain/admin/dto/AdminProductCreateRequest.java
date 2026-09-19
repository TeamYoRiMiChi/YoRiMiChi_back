package com.yorimichi.yorimichi.domain.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminProductCreateRequest {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String saleType;

    private String brand;

    @NotBlank
    private String productName;

    private String productNameJp;

    @NotNull
    private BigDecimal priceJpy;

    private BigDecimal originalPriceJpy;

    @NotNull
    @Min(0)
    private Integer stock;

    private String thumbnailUrl;

    @NotBlank
    private String status;

    // 공동구매 정보
    private String groupBuyTitle;

    private String groupBuyDescription;

    @Min(1)
    private Integer targetQuantity;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}