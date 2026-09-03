package com.yorimichi.yorimichi.domain.overseas.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 상품 카테고리 (CATEGORY 테이블)
 *
 * parentCategoryId가 NULL이면 최상위 카테고리입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private Long categoryId;
    private String categoryName;
    private Long parentCategoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
