package com.yorimichi.yorimichi.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.product.repository.ProductImageMapper;
import com.yorimichi.yorimichi.global.storage.ImageUrlResolver;

import java.util.List;

/**
 * 상품 이미지 조회
 *
 * 상세 화면의 갤러리처럼 한 상품의 이미지를 전부 보여줄 때 씁니다.
 * (목록·장바구니·주문의 대표 이미지는 각 매퍼 쿼리에서 PRODUCT_IMAGE를 바로 조회합니다.)
 */
@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageMapper productImageMapper;

    /** 화면에서 바로 쓸 수 있는 전체 주소 목록 (대표 이미지가 맨 앞) */
    @Transactional(readOnly = true)
    public List<String> findImageUrls(Long productId) {
        return productImageMapper.findImageUrlsByProductId(productId).stream()
                .map(ImageUrlResolver::resolve)
                .filter(url -> url != null)
                .toList();
    }
}
