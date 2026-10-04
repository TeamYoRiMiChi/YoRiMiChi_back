package com.yorimichi.yorimichi.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.mypage.dto.MyReviewResponseDto;
import com.yorimichi.yorimichi.domain.mypage.dto.ReviewUpdateRequestDto;
import com.yorimichi.yorimichi.domain.mypage.repository.MyReviewMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyReviewService {


	private final MyReviewMapper myReviewMapper;

	@Transactional(readOnly = true)
	public PageResponse<MyReviewResponseDto> getMyReviews(
			long memberId,
			int page,
			int size
			) {
		int safePage = Math.max(page, 1);
		int safeSize = Math.min(Math.max(size, 1), 50);
		int offset = (safePage - 1) * safeSize;

		List<MyReviewResponseDto> reviews =
				myReviewMapper.findMyReviewsByMemberId(
						memberId,
						offset,
						safeSize
						);

		long totalReviews =
				myReviewMapper.countMyReviewsByMemberId(memberId);

		return new PageResponse<>(
				reviews,
				safePage,
				safeSize,
				totalReviews
				);
	}

	@Transactional
	public void updateMyReview(
			long memberId,
			long reviewId,
			ReviewUpdateRequestDto request
			) {
		String content = request.getContent();

		if(content != null && !content.isBlank()) {
		    content = content.trim();
		} else {
		    content = null;
		}

		int updatedCount = myReviewMapper.updateMyReview(
				reviewId,
				memberId,
				request.getRating(),
				content
				);

		if(updatedCount == 0) {
		    throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
		}
	}

	@Transactional
	public void deleteMyReview(
			long memberId,
			long reviewId
			) {
		int deletedCount =
				myReviewMapper.deleteMyReview(reviewId, memberId);

		if(deletedCount == 0) {
		    throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
		}
	}
}
