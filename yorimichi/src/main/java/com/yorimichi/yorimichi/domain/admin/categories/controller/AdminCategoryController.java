package com.yorimichi.yorimichi.domain.admin.categories.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.admin.categories.dto.AdminCategoryCreateRequestDto;
import com.yorimichi.yorimichi.domain.admin.categories.dto.AdminCategoryResponseDto;
import com.yorimichi.yorimichi.domain.admin.categories.dto.AdminCategoryUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.categories.service.AdminCategoryService;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import com.yorimichi.yorimichi.global.response.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

	private final AdminCategoryService adminCategoryService;

	// 조회
	@GetMapping
	public ApiResponse<PageResponse<AdminCategoryResponseDto>> getCategories(
			@RequestParam(name = "keyword", required = false) String keyword, 
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue ="10") int size
			) {
		return ApiResponse.success(adminCategoryService
				.getCategories(keyword, page, size));
	}

	// 등록
	@PostMapping
	public ApiResponse<Void> createCategory(
			@Valid @RequestBody AdminCategoryCreateRequestDto request
			) {
		adminCategoryService.createCategory(request);

		return ApiResponse.success(null, "カテゴリーを登録しました。");
	}

	// 수정
	@PatchMapping("/{categoryId}")
	public ApiResponse<Void> updateCategory(
			@PathVariable("categoryId") Long categoryId,
			@Valid @RequestBody AdminCategoryUpdateRequestDto request
			) {
		adminCategoryService.updateCategory(categoryId, request);

		return ApiResponse.success(null,  "カテゴリーを更新しました。");
	}

	// 삭제
	@DeleteMapping("/{categoryId}")
	public ApiResponse<Void> deleteCategory(
			@PathVariable("categoryId") Long categoryId
			) {
		adminCategoryService.deleteCategory(categoryId);
		
		return ApiResponse.success(null, "カテゴリーを削除しました。");
	}
}
