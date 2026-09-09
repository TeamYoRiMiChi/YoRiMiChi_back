package com.yorimichi.yorimichi.domain.overseas.service;

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
 * 해외직구 상품 서비스
 *
 * DB 접근은 공용 ProductMapper를 쓰되, saleType을 OVERSEAS로 고정합니다.
 * 공동구매 전용 상품이 해외직구 목록에 섞일 수 없습니다.
 *
 * 해외직구에만 필요한 규칙(관세 안내, 배송 단계 등)이 생기면 여기에 추가하세요.
 * 공동구매 코드를 건드리지 않아도 됩니다.
 */
@Service
@RequiredArgsConstructor
public class OverseasProductService {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProductMapper productMapper;

    /**
     * 해외직구 상품 목록
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

        String keywordFilter = blankToNull(keyword);

        List<Product> products = productMapper.findAll(
                Product.SALE_TYPE_OVERSEAS, categoryId, keywordFilter, sort, offset, safeSize);

        long total = productMapper.countAll(
                Product.SALE_TYPE_OVERSEAS, categoryId, keywordFilter);

        List<ProductResponseDto> content = products.stream()
                .map(ProductResponseDto::new)
                .toList();

        return new PageResponse<>(content, safePage, safeSize, total);
    }

    /**
     * 해외직구 상품 단건
     *
     * 공동구매 전용 상품 id로 접근하면 찾을 수 없다고 응답합니다.
     * 주소만 바꿔서 다른 판매 방식의 상품을 여는 걸 막습니다.
     */
    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId) {
        Product product = productMapper.findById(productId)
                .filter(Product::isOverseas)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return new ProductResponseDto(product);
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
