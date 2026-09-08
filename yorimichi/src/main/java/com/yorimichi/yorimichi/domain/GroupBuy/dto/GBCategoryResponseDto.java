
package com.yorimichi.yorimichi.domain.GroupBuy.dto;
import com.yorimichi.yorimichi.domain.GroupBuy.entity.GBCategory;

import lombok.Getter;


/**
 * 카테고리 응답
 *
 * 화면에 필요한 값만
 */
@Getter
public class GBCategoryResponseDto {

    private final Long id;
    private final String name;
    private final Long parentId;

    public GBCategoryResponseDto(GBCategory category) {
        this.id = category.getCategoryId();
        this.name = category.getCategoryName();
        this.parentId = category.getParentCategoryId();
    }
}
