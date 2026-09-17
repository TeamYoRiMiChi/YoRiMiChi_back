package com.yorimichi.yorimichi.domain.admin.categories.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.admin.categories.dto.AdminCategoryCreateRequestDto;
import com.yorimichi.yorimichi.domain.admin.categories.dto.AdminCategoryResponseDto;
import com.yorimichi.yorimichi.domain.admin.categories.dto.AdminCategoryUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.categories.repository.AdminCategoryMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

	private static final int MAX_PAGE_SIZE = 10;

	private final AdminCategoryMapper adminCategoryMapper;

	// 조회
	@Transactional(readOnly = true)
	public PageResponse<AdminCategoryResponseDto> getCategories(
			String keyword, int page, int size
			) {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
		int offset = (safePage - 1) * safeSize;

		String searchKeyword = keyword == null || keyword.isBlank()
				? null : keyword.trim();

		List<AdminCategoryResponseDto> content = adminCategoryMapper.findCategories(
				searchKeyword, offset, safeSize
				);

		long totalCategories = adminCategoryMapper.countCategories(searchKeyword);

		return new PageResponse<>(
				content, safePage, safeSize, totalCategories
				);
	}


	// 등록
	@Transactional
	public void createCategory(AdminCategoryCreateRequestDto request) {
		String categoryName = request.getCategoryName().trim();

		long duplicateCount = 
				adminCategoryMapper.countCategoriesByName(
						null, 
						categoryName
						);

		if (duplicateCount > 0) {
			throw new CustomException(
					ErrorCode.DUPLICATE_CATEGORY_NAME
					);
		}

		try {
			adminCategoryMapper.insertCategory(categoryName);
		} catch (DuplicateKeyException e) {
			throw new CustomException(
					ErrorCode.DUPLICATE_CATEGORY_NAME
					);
		}
	}

	// 수정
	@Transactional
	public void updateCategory(
			Long categoryId, AdminCategoryUpdateRequestDto request
			) {

		String categoryName = request.getCategoryName().trim();

		long duplicateCount = 
				adminCategoryMapper.countCategoriesByName(
						categoryId,
						categoryName
						);

		if (duplicateCount > 0) {
			throw new CustomException(
					ErrorCode.DUPLICATE_CATEGORY_NAME
					);
		}		

		try {
			adminCategoryMapper.updateCategory(categoryId, categoryName);
		} catch (DuplicateKeyException e) {
			throw new CustomException(
					ErrorCode.DUPLICATE_CATEGORY_NAME
					);
		}	    
	}

	// 삭제
	@Transactional
	public void deleteCategory(Long categoryId) {
		try {
			int deletedCount = adminCategoryMapper.deleteCategory(categoryId);

			if(deletedCount == 0) {
				throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
			}
		} catch(DataIntegrityViolationException e) {
			throw new CustomException(ErrorCode.CATEGORY_IN_USE);
		}

	}
}
