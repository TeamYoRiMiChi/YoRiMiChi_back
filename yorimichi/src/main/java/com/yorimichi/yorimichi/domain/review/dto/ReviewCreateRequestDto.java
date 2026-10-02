package com.yorimichi.yorimichi.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 리뷰 등록 요청
 *
 * REVIEW 테이블 컬럼 중 클라이언트가 정하는 것은 이 세 가지뿐입니다.
 * member_id는 로그인 정보에서, product_id·sale_type은 주문 상품에서 서버가 채웁니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class ReviewCreateRequestDto {

    @NotNull(message = "注文商品を選択してください。")
    private Long orderItemId;

    @NotNull(message = "星の評価を選択してください。")
    @Min(value = 1, message = "星の評価は1〜5で選択してください。")
    @Max(value = 5, message = "星の評価は1〜5で選択してください。")
    private Integer rating;

    /** 내용은 선택 입력 (REVIEW.content는 NULL 허용) */
    @Size(max = 1000, message = "レビュー内容は1000文字以内で入力してください。")
    private String content;
}
