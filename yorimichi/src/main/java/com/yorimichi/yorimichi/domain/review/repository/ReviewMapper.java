package com.yorimichi.yorimichi.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.review.entity.Review;
import com.yorimichi.yorimichi.domain.review.entity.ReviewRatingCount;
import com.yorimichi.yorimichi.domain.review.entity.ReviewTarget;

@Mapper
public interface ReviewMapper {

    /** 내 주문 상품 한 건 (소유자 확인 포함). 남의 주문이거나 없으면 empty */
    Optional<ReviewTarget> findTarget(@Param("orderItemId") Long orderItemId,
                                      @Param("memberId") Long memberId);

    /** 이 주문 상품에 이미 리뷰가 있는지 */
    boolean existsByOrderItemId(@Param("orderItemId") Long orderItemId);

    void insert(Review review);

    /** 상품의 리뷰 목록 (최신순) */
    List<Review> findByProductId(@Param("productId") Long productId,
                                 @Param("limit") int limit);

    /** 상품의 별점별 개수 */
    List<ReviewRatingCount> countByRating(@Param("productId") Long productId);

    /** 내 주문 중 이미 리뷰를 쓴 주문 상품 ID들 */
    List<Long> findReviewedItemIdsByOrder(@Param("orderId") Long orderId,
                                          @Param("memberId") Long memberId);
}
