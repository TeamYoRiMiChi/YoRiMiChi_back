package com.yorimichi.yorimichi.domain.overseas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.overseas.dto.*;
import com.yorimichi.yorimichi.domain.overseas.service.*;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import java.util.List;

/**
 * 카테고리 API
 *
 * GET /api/categories
 *
 * 응답 예시:
 * {
 *   "success": true,
 *   "data": [
 *     { "id": 1, "name": "すべて", "parentId": null },
 *     { "id": 2, "name": "ファッション", "parentId": null }
 *   ],
 *   "message": null
 * }
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponseDto>> getCategories() {
        return ApiResponse.success(categoryService.getCategories());
    }
}
