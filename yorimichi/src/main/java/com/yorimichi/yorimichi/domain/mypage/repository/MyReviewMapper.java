package com.yorimichi.yorimichi.domain.mypage.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.mypage.dto.MyReviewResponseDto;

@Mapper
public interface MyReviewMapper {

	List<MyReviewResponseDto> findMyReviewsByMemberId(
			@Param("memberId") long memberId,
			@Param("offset") int offset,
			@Param("size") int size
			);

	long countMyReviewsByMemberId(@Param("memberId") long memberId);

	int updateMyReview(
			@Param("reviewId") long reviewId,
			@Param("memberId") long memberId,
			@Param("rating") int rating,
			@Param("content") String content
			);

	int deleteMyReview(
			@Param("reviewId") long reviewId,
			@Param("memberId") long memberId
			);
}
