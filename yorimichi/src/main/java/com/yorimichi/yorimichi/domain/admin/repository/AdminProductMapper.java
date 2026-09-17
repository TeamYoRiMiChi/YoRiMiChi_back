package com.yorimichi.yorimichi.domain.admin.repository;

import com.yorimichi.yorimichi.domain.admin.dto.AdminProductUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminProductMapper {

    int updateProduct(
            @Param("productId") Long productId,
            @Param("request") AdminProductUpdateRequest request
    );
}
