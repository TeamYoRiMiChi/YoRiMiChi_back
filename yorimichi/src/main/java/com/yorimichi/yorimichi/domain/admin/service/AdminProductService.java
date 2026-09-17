package com.yorimichi.yorimichi.domain.admin.service;

import com.yorimichi.yorimichi.domain.admin.dto.AdminProductUpdateRequest;
import com.yorimichi.yorimichi.domain.admin.repository.AdminProductMapper;
import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {

    private static final Set<String> ALLOWED_STATUSES =
            Set.of(
                    "ACTIVE",
                    "SOLD_OUT",
                    "HIDDEN"
            );

    private final AdminProductMapper adminProductMapper;
    private final ProductService productService;

    @Transactional
    public ProductResponseDto updateProduct(
            Long productId,
            AdminProductUpdateRequest request
    ) {
        // PRODUCT 테이블에서 사용하는 상태인지 검사
        if (!ALLOWED_STATUSES.contains(request.getStatus())) {
            throw new IllegalArgumentException(
                    "올바르지 않은 상품 상태입니다."
            );
        }

        int updatedRows = adminProductMapper.updateProduct(
                productId,
                request
        );

        // 수정된 상품이 없을 경우
        if (updatedRows == 0) {
            throw new IllegalArgumentException(
                    "존재하지 않는 상품입니다."
            );
        }

        // 수정된 상품을 다시 조회해서 반환
        return productService.getProduct(productId);
    }
}