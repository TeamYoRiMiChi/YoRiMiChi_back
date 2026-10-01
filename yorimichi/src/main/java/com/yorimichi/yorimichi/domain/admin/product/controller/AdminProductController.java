package com.yorimichi.yorimichi.domain.admin.product.controller;
import com.yorimichi.yorimichi.domain.admin.product.dto.AdminProductCreateRequest;
import com.yorimichi.yorimichi.domain.admin.product.dto.AdminProductUpdateRequest;
import com.yorimichi.yorimichi.domain.admin.product.dto.AdminProductImageResponse;
import com.yorimichi.yorimichi.domain.admin.product.service.AdminProductImageService;
import com.yorimichi.yorimichi.domain.admin.product.service.AdminProductService;
import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
/**
 * 관리자 상품관리 API
 *
 * GET   /api/admin/products
 * → 관리자 상품 전체 조회
 *
 * PATCH /api/admin/products/{productId}
 * → 카테고리, 재고, 판매 상태 수정
 *
 * GET    /api/admin/products/{productId}/images
 * POST   /api/admin/products/{productId}/images
 * DELETE /api/admin/products/{productId}/images/{imageId}
 * PATCH  /api/admin/products/{productId}/images/{imageId}/thumbnail
 * → 상품 이미지 조회·업로드·삭제·대표 이미지 변경
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final AdminProductImageService adminProductImageService;

    /**
     * 관리자 상품 전체 조회
     */
    @GetMapping
    public ApiResponse<List<ProductResponseDto>> getProducts() {
        return ApiResponse.success(
                adminProductService.getProducts()
        );
    }
    
    /**관리자 상품 등록**/
    @PostMapping
    public ApiResponse<ProductResponseDto> createProduct(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody AdminProductCreateRequest request
    ) {
        return ApiResponse.success(
                adminProductService.createProduct(
                        memberId,
                        request
                )
        );
    }
    

    /**
     * 관리자 상품 수정
     */
    @PatchMapping("/{productId}")
    public ApiResponse<ProductResponseDto> updateProduct(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody AdminProductUpdateRequest request
    ) {
        return ApiResponse.success(
                adminProductService.updateProduct(
                        productId,
                        request
                )
        );
    }
    /**삭제**/
    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteProduct(
            @PathVariable("productId") Long productId
    ) {
        adminProductService.deleteProduct(productId);
        return ApiResponse.success(null);
    }

    /**
     * 상품 이미지 목록
     * GET /api/admin/products/{productId}/images
     */
    @GetMapping("/{productId}/images")
    public ApiResponse<List<AdminProductImageResponse>> getImages(
            @PathVariable("productId") Long productId
    ) {
        return ApiResponse.success(
                adminProductImageService.getImages(productId)
        );
    }

    /**
     * 상품 이미지 업로드 (여러 장)
     * POST /api/admin/products/{productId}/images
     *
     * multipart/form-data, 파일 필드 이름은 files
     */
    @PostMapping(
            value = "/{productId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<List<AdminProductImageResponse>> uploadImages(
            @PathVariable("productId") Long productId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        return ApiResponse.success(
                adminProductImageService.addImages(productId, files)
        );
    }

    /**
     * 상품 이미지 한 장 삭제
     * DELETE /api/admin/products/{productId}/images/{imageId}
     */
    @DeleteMapping("/{productId}/images/{imageId}")
    public ApiResponse<List<AdminProductImageResponse>> deleteImage(
            @PathVariable("productId") Long productId,
            @PathVariable("imageId") Long imageId
    ) {
        return ApiResponse.success(
                adminProductImageService.deleteImage(productId, imageId)
        );
    }

    /**
     * 대표 이미지 변경
     * PATCH /api/admin/products/{productId}/images/{imageId}/thumbnail
     */
    @PatchMapping("/{productId}/images/{imageId}/thumbnail")
    public ApiResponse<List<AdminProductImageResponse>> changeThumbnail(
            @PathVariable("productId") Long productId,
            @PathVariable("imageId") Long imageId
    ) {
        return ApiResponse.success(
                adminProductImageService.changeThumbnail(productId, imageId)
        );
    }
}
