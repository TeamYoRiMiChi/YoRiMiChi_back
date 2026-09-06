package com.yorimichi.yorimichi.domain.GroupBuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GBCategoryResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.service.GBCategoryService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/gb-categories")
@RequiredArgsConstructor
public class GBCategoryController {

    private final GBCategoryService gbCategoryService;

    @GetMapping
    public ApiResponse<List<GBCategoryResponseDto>> getCategories() {
    	System.out.println("GBCategoryController::::");
        return ApiResponse.success(
                gbCategoryService.getCategories()
        ); 
    }
}