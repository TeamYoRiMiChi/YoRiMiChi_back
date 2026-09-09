package com.yorimichi.yorimichi.domain.GroupBuy.service;

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

/**
 * 공동구매 상품 서비스
 *
 * DB 접근은 공용 ProductMapper를 쓰되, saleType을 GROUP_BUY로 고정합니다.
 * 해외직구 상품이 공동구매 목록에 섞일 수 없습니다.
 *
 * 공동구매에만 필요한 규칙(모집 진행률, 마감 임박 정렬 등)이 생기면
 * 여기에 추가하세요. 해외직구 코드를 건드리지 않아도 됩니다.
 */
@Service
@RequiredArgsConstructor
public class GroupBuyProductService {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProductMapper productMapper;

    /** 공동구매 상품 목록 */
    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDto> getProducts(Long categoryId,
                                                        String keyword,
                                                        String sort,
                                                        int page,
                                                        int size) {

        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        String keywordFilter = blankToNull(keyword);

        List<Product> products = productMapper.findAll(
                Product.SALE_TYPE_GROUP_BUY, categoryId, keywordFilter, sort, offset, safeSize);

        long total = productMapper.countAll(
                Product.SALE_TYPE_GROUP_BUY, categoryId, keywordFilter);

        List<ProductResponseDto> content = products.stream()
                .map(ProductResponseDto::new)
                .toList();

        return new PageResponse<>(content, safePage, safeSize, total);
    }

    /**
     * 공동구매 상품 단건
     *
     * 해외직구 상품 id로 접근하면 찾을 수 없다고 응답합니다.
     */
    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId) {
        Product product = productMapper.findById(productId)
                .filter(Product::isGroupBuyOnly)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return new ProductResponseDto(product);
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
