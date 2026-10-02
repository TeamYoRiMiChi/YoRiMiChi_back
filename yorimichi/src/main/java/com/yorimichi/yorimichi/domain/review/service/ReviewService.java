package com.yorimichi.yorimichi.domain.review.service;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.review.dto.ReviewCreateRequestDto;
import com.yorimichi.yorimichi.domain.review.dto.ReviewListResponseDto;
import com.yorimichi.yorimichi.domain.review.entity.Review;
import com.yorimichi.yorimichi.domain.review.entity.ReviewTarget;
import com.yorimichi.yorimichi.domain.review.repository.ReviewMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final int DEFAULT_LIST_SIZE = 20;
    private static final int MAX_LIST_SIZE = 50;

    private final ReviewMapper reviewMapper;

    /**
     * 리뷰 등록
     *
     * 내 주문의 상품에만 쓸 수 있고, 주문 상품 하나당 한 번만 쓸 수 있습니다.
     * 취소된 주문에는 쓸 수 없습니다.
     */
    @Transactional
    public Long create(Long memberId, ReviewCreateRequestDto request) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        ReviewTarget target = reviewMapper
                .findTarget(request.getOrderItemId(), memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_ORDER_ITEM_NOT_FOUND));

        if ("CANCELLED".equals(target.getOrderStatus())) {
            throw new CustomException(ErrorCode.REVIEW_NOT_ALLOWED);
        }

        if (reviewMapper.existsByOrderItemId(target.getOrderItemId())) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        String content = request.getContent() == null || request.getContent().isBlank()
                ? null
                : request.getContent().trim();

        Review review = Review.builder()
                .memberId(memberId)
                .productId(target.getProductId())
                .saleType(target.getSaleType() == null ? "OVERSEAS" : target.getSaleType())
                .orderItemId(target.getOrderItemId())
                .rating(request.getRating())
                .content(content)
                .build();

        try {
            reviewMapper.insert(review);
        } catch (DuplicateKeyException exception) {
            // 동시에 두 번 눌렀을 때 UNIQUE(order_item_id)가 막아줍니다
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        return review.getReviewId();
    }

    /** 상품의 리뷰 목록 + 요약 (로그인 없이 조회 가능) */
    @Transactional(readOnly = true)
    public ReviewListResponseDto getProductReviews(Long productId, Integer size) {
        int limit = size == null ? DEFAULT_LIST_SIZE : Math.max(1, Math.min(size, MAX_LIST_SIZE));

        return new ReviewListResponseDto(
                reviewMapper.countByRating(productId),
                reviewMapper.findByProductId(productId, limit)
        );
    }

    /** 이 주문에서 내가 이미 리뷰를 쓴 주문 상품 ID들 */
    @Transactional(readOnly = true)
    public List<Long> getReviewedItemIds(Long memberId, Long orderId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return reviewMapper.findReviewedItemIdsByOrder(orderId, memberId);
    }
}
