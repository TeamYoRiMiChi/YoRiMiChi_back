package com.yorimichi.yorimichi.domain.admin.categories.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.admin.categories.dto.AdminCategoryResponseDto;

@Mapper
public interface AdminCategoryMapper {

	// 조회
	List<AdminCategoryResponseDto> findCategories(
			@Param("keyword") String keyword, 
			@Param("offset") int offset, 
			@Param("size") int size
			);

	long countCategories(@Param("keyword") String keyword);

	// 등록
	int insertCategory(@Param("categoryName") String categoryName);

	long countCategoriesByName(
			@Param("excludedCategoryId") Long excludedCategoryId, 
			@Param("categoryName") String categoryName
			);

	// 수정
	int updateCategory(
			@Param("categoryId") Long categoryId,
			@Param("categoryName") String categoryName
			);

	// 삭제
	int deleteCategory(@Param("categoryId") Long categoryId);
}
