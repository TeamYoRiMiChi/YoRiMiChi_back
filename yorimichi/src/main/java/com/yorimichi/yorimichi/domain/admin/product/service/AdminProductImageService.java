package com.yorimichi.yorimichi.domain.admin.product.service;

import com.yorimichi.yorimichi.domain.admin.product.dto.AdminProductImageResponse;
import com.yorimichi.yorimichi.domain.admin.product.repository.AdminProductImageMapper;
import com.yorimichi.yorimichi.domain.admin.product.repository.AdminProductMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.storage.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 관리자 상품 이미지 관리
 *
 * 파일은 ImageStorageService가 저장합니다.
 *   - local 프로필 → 이 서버의 upload 폴더
 *   - rds 프로필   → S3 버킷
 * PRODUCT_IMAGE에는 파일 자체가 아니라 저장소 키(예: 2026-10-01/uuid.jpg)만 넣습니다.
 *
 * 대표 이미지(is_thumbnail = 1)는 상품마다 한 장이며, 목록·장바구니·주문에 쓰입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductImageService {

    /** 상품 한 개에 올릴 수 있는 이미지 최대 장수 */
    private static final int MAX_IMAGES_PER_PRODUCT = 10;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final AdminProductImageMapper imageMapper;
    private final AdminProductMapper productMapper;
    private final ImageStorageService imageStorageService;

    /** 상품 이미지 목록 */
    public List<AdminProductImageResponse> getImages(Long productId) {
        requireProduct(productId);
        return imageMapper.findByProductId(productId);
    }

    /**
     * 이미지 여러 장 추가
     *
     * 대표 이미지가 아직 없는 상품이면 이번에 올린 첫 번째 파일이 대표 이미지가 됩니다.
     * 중간에 실패하면 DB는 롤백되고, 이미 저장한 파일은 지웁니다.
     */
    @Transactional
    public List<AdminProductImageResponse> addImages(Long productId, List<MultipartFile> files) {
        lockProduct(productId);

        List<MultipartFile> uploads = files == null
                ? List.of()
                : files.stream().filter(file -> file != null && !file.isEmpty()).toList();

        if (uploads.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        for (MultipartFile file : uploads) {
            if (!isAllowedImage(file)) {
                throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
            }
        }

        int existingCount = imageMapper.countByProductId(productId);
        if (existingCount + uploads.size() > MAX_IMAGES_PER_PRODUCT) {
            throw new CustomException(ErrorCode.PRODUCT_IMAGE_LIMIT_EXCEEDED);
        }

        boolean needThumbnail = imageMapper.countThumbnail(productId) == 0;
        int nextOrder = imageMapper.findMaxOrder(productId) + 1;

        List<String> savedKeys = new ArrayList<>();
        try {
            for (MultipartFile file : uploads) {
                String key = imageStorageService.upload(file);
                savedKeys.add(key);

                imageMapper.insert(productId, key, nextOrder, needThumbnail);

                nextOrder++;
                needThumbnail = false;
            }
        } catch (RuntimeException e) {
            // DB는 트랜잭션이 롤백해 주지만 저장소에 올라간 파일은 직접 지워야 합니다.
            savedKeys.forEach(this::deleteFileQuietly);
            throw e;
        }

        return imageMapper.findByProductId(productId);
    }

    /**
     * 이미지 한 장 삭제
     *
     * 지운 이미지가 대표 이미지였다면 남은 이미지 중 가장 앞선 것을 대표로 올립니다.
     */
    @Transactional
    public List<AdminProductImageResponse> deleteImage(Long productId, Long imageId) {
        lockProduct(productId);

        String rawUrl = imageMapper.findRawImageUrl(imageId, productId);
        if (rawUrl == null) {
            throw new CustomException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        }

        imageMapper.delete(imageId, productId);

        if (imageMapper.countThumbnail(productId) == 0) {
            Long firstImageId = imageMapper.findFirstImageId(productId);
            if (firstImageId != null) {
                imageMapper.setThumbnail(firstImageId, productId);
            }
        }

        // 우리 저장소에 올린 파일(키)만 지웁니다. 직접 입력한 http 주소는 건드리지 않습니다.
        if (isStorageKey(rawUrl)) {
            deleteFileQuietly(rawUrl);
        }

        return imageMapper.findByProductId(productId);
    }

    /** 대표 이미지 변경 */
    @Transactional
    public List<AdminProductImageResponse> changeThumbnail(Long productId, Long imageId) {
        lockProduct(productId);

        if (imageMapper.findRawImageUrl(imageId, productId) == null) {
            throw new CustomException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        }

        imageMapper.clearThumbnail(productId);
        imageMapper.setThumbnail(imageId, productId);

        return imageMapper.findByProductId(productId);
    }

    private void requireProduct(Long productId) {
        if (productMapper.countProductById(productId) == 0) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    private void lockProduct(Long productId) {
        if (imageMapper.lockProduct(productId) == null) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    /** 파일 이름이 아니라 실제 Content-Type으로 이미지인지 확인합니다. */
    private boolean isAllowedImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null
                && ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase());
    }

    /** http(s) 주소가 아니면 우리 저장소의 키입니다. (ImageUrlResolver와 같은 기준) */
    private boolean isStorageKey(String value) {
        String v = value.trim();
        return !(v.startsWith("http://") || v.startsWith("https://")
                || v.startsWith("//") || v.startsWith("/") || v.startsWith("data:"));
    }

    private void deleteFileQuietly(String key) {
        try {
            imageStorageService.delete(key);
        } catch (RuntimeException e) {
            // 파일 삭제 실패가 관리자 화면 동작을 막지는 않게 하고, 남은 파일은 로그로 남깁니다.
            log.warn("상품 이미지 파일 삭제에 실패했습니다. key={}", key, e);
        }
    }
}
