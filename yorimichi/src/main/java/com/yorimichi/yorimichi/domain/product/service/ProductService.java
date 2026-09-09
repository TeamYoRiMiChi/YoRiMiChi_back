package com.yorimichi.yorimichi.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.domain.product.entity.Product;
import com.yorimichi.yorimichi.domain.product.repository.ProductMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

/**
 * 상품 공용 서비스
 *
 * 해외직구·공동구매 어느 쪽에도 속하지 않는,
 * 판매 방식과 무관한 조회만 담당합니다.
 *
 * 목록 조회는 각 도메인이 맡습니다.
 *   해외직구 → OverseasProductService
 *   공동구매 → GroupBuyProductService
 *
 * 그래야 한쪽 기능을 고칠 때 다른 쪽 코드를 건드리지 않습니다.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    /**
     * 상품 단건 조회 (판매 방식 무관)
     *
     * 장바구니·주문처럼 이미 담긴 상품을 다시 읽을 때 씁니다.
     * 화면에서 목록을 그릴 때는 각 도메인 서비스를 쓰세요.
     */
    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId) {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return new ProductResponseDto(product);
    }
}
