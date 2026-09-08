package com.yorimichi.yorimichi.domain.wishlist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.product.repository.ProductMapper;
import com.yorimichi.yorimichi.domain.wishlist.dto.WishlistItemResponseDto;
import com.yorimichi.yorimichi.domain.wishlist.repository.ProductWishlistMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import java.util.List;

/**
 * 상품 화면용 찜 로직
 *
 * 마이페이지의 WishlistService와 역할이 달라 이름을 구분했습니다.
 * 이쪽은 추가·삭제 토글이 주된 용도입니다.
 */
@Service
@RequiredArgsConstructor
public class ProductWishlistService {

    private final ProductWishlistMapper productWishlistMapper;
    private final ProductMapper productMapper;

    /**
     * 찜한 상품 id 목록
     *
     * 상품 목록·상세 화면에서 하트가 켜졌는지만 판단하면 되므로
     * 상품 정보 없이 id만 돌려줍니다.
     */
    @Transactional(readOnly = true)
    public List<Long> getWishlistProductIds(Long memberId) {
        return productWishlistMapper.findProductIdsByMemberId(memberId);
    }

    /** 찜 목록 (상품 정보 포함) */
    @Transactional(readOnly = true)
    public List<WishlistItemResponseDto> getWishlistItems(Long memberId) {
        return productWishlistMapper.findByMemberId(memberId)
                .stream()
                .map(WishlistItemResponseDto::new)
                .toList();
    }

    /**
     * 찜 추가
     *
     * 이미 찜한 상품이면 조용히 넘어갑니다.
     * 화면에서 하트를 두 번 눌러도 에러가 나지 않도록요.
     */
    @Transactional
    public void add(Long memberId, Long productId) {
        productMapper.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (productWishlistMapper.exists(memberId, productId)) {
            return;
        }

        productWishlistMapper.insert(memberId, productId);
    }

    /** 찜 삭제 */
    @Transactional
    public void remove(Long memberId, Long productId) {
        productWishlistMapper.delete(memberId, productId);
    }
}
