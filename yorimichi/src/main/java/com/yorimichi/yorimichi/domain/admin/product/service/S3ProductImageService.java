package com.yorimichi.yorimichi.domain.admin.product.service;

import com.yorimichi.yorimichi.domain.admin.product.dto.AdminProductImageResponse;
import com.yorimichi.yorimichi.domain.admin.product.repository.AdminProductImageMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.storage.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

/** S3에 직접 업로드한 파일의 키를 상품에 등록합니다. */
@Service
@Profile("rds")
@RequiredArgsConstructor
public class S3ProductImageService {

    private static final int MAX_IMAGES_PER_PRODUCT = 10;

    private final AdminProductImageMapper imageMapper;
    private final S3UploadService s3UploadService;

    @Transactional
    public List<AdminProductImageResponse> registerImages(
            Long memberId, Long productId, List<String> imageKeys
    ) {
        if (memberId == null || memberId <= 0) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (imageKeys == null || imageKeys.isEmpty()
                || imageKeys.stream().anyMatch(key -> key == null || key.isBlank())
                || new HashSet<>(imageKeys).size() != imageKeys.size()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (imageKeys.size() > MAX_IMAGES_PER_PRODUCT) {
            throw new CustomException(ErrorCode.PRODUCT_IMAGE_LIMIT_EXCEEDED);
        }
        if (productId == null || imageMapper.lockProduct(productId) == null) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (imageMapper.countByProductId(productId) + imageKeys.size()
                > MAX_IMAGES_PER_PRODUCT) {
            throw new CustomException(ErrorCode.PRODUCT_IMAGE_LIMIT_EXCEEDED);
        }

        // 전체 키를 확인한 뒤 저장하여, 검증 실패 시 부분 등록을 막습니다.
        for (String imageKey : imageKeys) {
            if (imageMapper.countByImageKey(imageKey) > 0) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            s3UploadService.validateUploadedImage(memberId, imageKey);
        }

        boolean needThumbnail = imageMapper.countThumbnail(productId) == 0;
        int nextOrder = imageMapper.findMaxOrder(productId) + 1;
        for (String imageKey : imageKeys) {
            imageMapper.insert(productId, imageKey, nextOrder++, needThumbnail);
            needThumbnail = false;
        }
        return imageMapper.findByProductId(productId);
    }
}
