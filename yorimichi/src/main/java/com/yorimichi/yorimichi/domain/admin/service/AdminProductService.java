package com.yorimichi.yorimichi.domain.admin.service;

import com.yorimichi.yorimichi.domain.admin.dto.AdminProductUpdateRequest;
import com.yorimichi.yorimichi.domain.admin.repository.AdminProductMapper;
import com.yorimichi.yorimichi.domain.product.dto.ProductResponseDto;
import com.yorimichi.yorimichi.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import com.yorimichi.yorimichi.domain.admin.dto.AdminProductCreateRequest;
import com.yorimichi.yorimichi.domain.product.entity.Product;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {

    // 관리자가 변경할 수 있는 PRODUCT 상태
    private static final Set<String> ALLOWED_STATUSES =
            Set.of(
                    "ACTIVE",
                    "SOLD_OUT",
                    "HIDDEN"
            );

    private final AdminProductMapper adminProductMapper;
    private final ProductService productService;

    /**
     * 관리자 상품관리 화면에 표시할 전체 상품 조회
     *
     * Mapper가 DB에서 Product 목록을 가져오고,
     * 각 Product를 프론트 응답용 ProductResponseDto로 변환한다.
     */
    public List<ProductResponseDto> getProducts() {
        return adminProductMapper.findAllProducts()
                .stream()
                .map(ProductResponseDto::new)
                .toList();
    }
    
    /**관리자 상품 등록**/
    @Transactional
    public ProductResponseDto createProduct(
            Long memberId,
            AdminProductCreateRequest request
    ) {
        if (memberId == null) {
            throw new IllegalArgumentException(
                    "로그인이 필요한 요청입니다."
            );
        }

        if (
                !"OVERSEAS".equals(
                        request.getSaleType()
                )
                && !"GROUP_BUY".equals(
                        request.getSaleType()
                )
        ) {
            throw new IllegalArgumentException(
                    "올바르지 않은 판매 유형입니다."
            );
        }

        if (
                !ALLOWED_STATUSES.contains(
                        request.getStatus()
                )
        ) {
            throw new IllegalArgumentException(
                    "올바르지 않은 상품 상태입니다."
            );
        }

        /*
         * 공동구매 상품일 때만
         * 공동구매 입력값을 검사합니다.
         */
        if (
                "GROUP_BUY".equals(
                        request.getSaleType()
                )
        ) {
            if (
                    request.getGroupBuyTitle() == null
                    || request.getGroupBuyTitle()
                        .isBlank()
            ) {
                throw new IllegalArgumentException(
                        "공동구매 제목은 필수입니다."
                );
            }

            if (
                    request.getTargetQuantity() == null
                    || request.getTargetQuantity() < 1
            ) {
                throw new IllegalArgumentException(
                        "목표 수량은 1개 이상이어야 합니다."
                );
            }

            /*
             * 판매 중인 공동구매만
             * 모집 기간을 필수로 검사합니다.
             */
            if (
                    "ACTIVE".equals(
                            request.getStatus()
                    )
            ) {
                if (
                        request.getStartDate() == null
                        || request.getEndDate() == null
                ) {
                    throw new IllegalArgumentException(
                            "모집 기간은 필수입니다."
                    );
                }

                if (
                        !request.getEndDate()
                                .isAfter(
                                        request.getStartDate()
                                )
                ) {
                    throw new IllegalArgumentException(
                            "마감일은 시작일보다 뒤여야 합니다."
                    );
                }
            }
        }

        Product product = Product.builder()
                .categoryId(request.getCategoryId())
                .saleType(request.getSaleType())
                .brand(request.getBrand())
                .productName(request.getProductName())
                .productNameJp(request.getProductNameJp())
                .priceJpy(request.getPriceJpy())
                .originalPriceJpy(
                        request.getOriginalPriceJpy()
                )
                .stock(request.getStock())
                .salesCount(0)
                .thumbnailUrl(request.getThumbnailUrl())
                .status(request.getStatus())
                .build();

        int insertedRows =
                adminProductMapper.insertProduct(product);

        if (
                insertedRows != 1
                || product.getProductId() == null
        ) {
            throw new IllegalStateException(
                    "상품 등록에 실패했습니다."
            );
        }

        /*
         * 판매 중인 공동구매만
         * GROUP_BUY 테이블에 모집 정보를 등록합니다.
         */
        if (
                "GROUP_BUY".equals(
                        request.getSaleType()
                )
                && "ACTIVE".equals(
                        request.getStatus()
                )
        ) {
            int groupBuyRows =
                    adminProductMapper.insertGroupBuy(
                            product.getProductId(),
                            memberId,
                            request
                    );

            if (groupBuyRows != 1) {
                throw new IllegalStateException(
                        "공동구매 등록에 실패했습니다."
                );
            }
        }

        return productService.getProduct(
                product.getProductId()
        );
    }

    /**
     * 관리자 상품 수정
     */
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

        // PRODUCT 테이블 수정
        int updatedRows = adminProductMapper.updateProduct(
                productId,
                request
        );

        // 수정된 상품이 없는 경우
        if (updatedRows == 0) {
            throw new IllegalArgumentException(
                    "존재하지 않는 상품입니다."
            );
        }

        // 수정된 상품을 다시 조회해서 반환
        return productService.getProduct(productId);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        int productExists =
                adminProductMapper.countProductById(productId);

        if (productExists == 0) {
            throw new IllegalArgumentException(
                    "존재하지 않는 상품입니다."
            );
        }

        adminProductMapper.deleteGroupBuyByProductId(
                productId
        );

        adminProductMapper.deleteProduct(productId);
    }
		
  
	
}