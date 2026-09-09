
package com.yorimichi.yorimichi.domain.GroupBuy.dto;
import com.yorimichi.yorimichi.domain.GroupBuy.entity.GBCategory;

import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 카테고리 응답
 *
 * 화면에 필요한 값만
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GBCategoryResponseDto {

    private  Long id;
    private  String name;
    private  Long parentId;

    public GBCategoryResponseDto(GBCategory category) {
        this.id = category.getCategoryId();
        this.name = category.getCategoryName();
        this.parentId = category.getParentCategoryId();
    }
}
