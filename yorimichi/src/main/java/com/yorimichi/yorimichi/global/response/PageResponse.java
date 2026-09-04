package com.yorimichi.yorimichi.global.response;

import lombok.Getter;

import java.util.List;

/**
 * 페이지 응답 공통 형태
 *
 * 상품·주문·리뷰 등 목록을 나눠 보내는 API에서 함께 씁니다.
 * 프론트는 어떤 API든 같은 모양으로 받으므로 처리 코드를 재사용할 수 있습니다.
 *
 * {
 *   "content": [...],
 *   "page": 1,
 *   "size": 8,
 *   "totalElements": 21,
 *   "totalPages": 3
 * }
 */
@Getter
public class PageResponse<T> {

    private final List<T> content;
    private final int page;          // 현재 페이지 (1부터)
    private final int size;          // 페이지당 개수
    private final long totalElements; // 전체 개수
    private final int totalPages;     // 전체 페이지 수
    private final boolean first;
    private final boolean last;

    public PageResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        this.first = page <= 1;
        this.last = page >= this.totalPages;
    }
}
