package com.yorimichi.yorimichi.domain.admin.product.controller;

import com.yorimichi.yorimichi.domain.admin.product.dto.AdminProductImageRegisterRequest;
import com.yorimichi.yorimichi.domain.admin.product.dto.AdminProductImageResponse;
import com.yorimichi.yorimichi.domain.admin.product.service.S3ProductImageService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Profile("rds")
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class S3ProductImageController {

    private final S3ProductImageService s3ProductImageService;

    @PostMapping(value = "/{productId}/images", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<AdminProductImageResponse>> registerImages(
            @CurrentMemberId Long memberId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody AdminProductImageRegisterRequest request
    ) {
        return ApiResponse.success(s3ProductImageService.registerImages(
                memberId, productId, request.imageKeys()
        ));
    }
}
