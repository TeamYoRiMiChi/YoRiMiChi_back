package com.yorimichi.yorimichi.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.domain.product.entity.Product;
import com.yorimichi.yorimichi.domain.product.repository.ProductMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.PageResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProductMapper productMapper;

    /**
     * 상품 목록 조회
     *
     * 페이지 번호와 크기를 서버에서 한 번 더 다듬습니다.
     * 프론트에서 잘못된 값(0페이지, 1000개 요청)이 와도
     * 서버가 무리한 조회를 하지 않도록 막는 역할입니다.
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDto> getProducts(Long categoryId,
                                                        String keyword,
                                                        String sort,
                                                        int page,
                                                        int size) {

        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        // 카테고리 1(すべて)은 전체 조회이므로 조건에서 뺍니다
        Long categoryFilter = (categoryId == null || categoryId == 1L) ? null : categoryId;

        String keywordFilter = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        List<Product> products =
                productMapper.findAll(categoryFilter, keywordFilter, sort, offset, safeSize);

        long total = productMapper.countAll(categoryFilter, keywordFilter);

        List<ProductResponseDto> content = products.stream()
                .map(ProductResponseDto::new)
                .toList();

        return new PageResponse<>(content, safePage, safeSize, total);
    }

    /** 상품 단건 조회 */
    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId) {
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return new ProductResponseDto(product);
    }
}
