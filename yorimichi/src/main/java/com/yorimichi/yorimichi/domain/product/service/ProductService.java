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
 *
 * 예외적으로 헤더 통합검색(search)만 여기서 다룹니다.
 * 해외직구·공동구매를 가리지 않고 PRODUCT 테이블 전체를 뒤져야 하는데,
 * findAll(saleType, ...)이 saleType=null이면 원래 "구분 없이 전체"를
 * 조회하도록 이미 만들어져 있어서 새 매퍼 쿼리 없이 그대로 재사용합니다.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int MAX_PAGE_SIZE = 50;

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

    /**
     * 헤더 통합검색 — 해외직구/공동구매 구분 없이 상품명·브랜드로 검색
     *
     * saleType을 null로 넘기면 searchConditions의 sale_type 조건이 빠져서
     * 두 판매 방식이 섞인 결과가 나옵니다. 정렬(recommend/popular/newest)도
     * findAll의 기존 <choose> 분기를 그대로 씁니다.
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductResponseDto> search(String keyword,
                                                    Long categoryId,
                                                    String sort,
                                                    int page,
                                                    int size) {

        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        String keywordFilter = blankToNull(keyword);

        List<Product> products = productMapper.findAll(
                null, categoryId, keywordFilter, sort, offset, safeSize);

        long total = productMapper.countAll(null, categoryId, keywordFilter);

        List<ProductResponseDto> content = products.stream()
                .map(ProductResponseDto::new)
                .toList();

        return new PageResponse<>(content, safePage, safeSize, total);
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
