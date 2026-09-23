package com.yorimichi.yorimichi.domain.admin.GroupBuy.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyCreateRequestDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyDetailResponseDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyResponseDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyStatusUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyUpdateRequestDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.repository.AdminGroupBuyMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminGroupBuyService {

    private static final int MAX_PAGE_SIZE = 10;

    // GROUP_BUY.status가 실제로 쓰는 값 (GroupBuyResponseDto 주석 기준)
    private static final Set<String> ALLOWED_STATUSES =
            Set.of("RECRUITING", "SUCCESS", "FAILED", "CANCELLED");

    // 목록 화면의 달성률 필터 값
    private static final Set<String> ALLOWED_PROGRESS_FILTERS =
            Set.of("UNDER_50", "OVER_50", "COMPLETE");

    // 목록 정렬 기준 → 실제 정렬에 쓸 컬럼 (SQL에 그대로 들어가므로 반드시 화이트리스트로만 매핑합니다)
    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "GROUP_BUY_ID", "gb.group_buy_id",
            "PRODUCT_NAME", "p.product_name",
            "CREATOR_NAME", "m.name",
            "START_DATE", "gb.start_date"
    );

    private static final Set<String> ALLOWED_SORT_DIRECTIONS = Set.of("ASC", "DESC");

    private final AdminGroupBuyMapper adminGroupBuyMapper;

    // 공동구매 목록
    @Transactional(readOnly = true)
    public PageResponse<AdminGroupBuyResponseDto> getGroupBuys(
            String keyword,
            String status,
            String progress,
            String sortBy,
            String sortDirection,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int offset = (safePage - 1) * safeSize;

        String searchKeyword = keyword == null || keyword.isBlank()
                ? null : keyword.trim();

        // 달성률 필터는 프론트에서 넘어오는 값이 화이트리스트를 벗어나면 조용히 무시합니다.
        String progressFilter = progress != null && ALLOWED_PROGRESS_FILTERS.contains(progress)
                ? progress : null;

        // 정렬도 마찬가지로 화이트리스트에 없으면 기본값(공동구매 ID 내림차순)으로 조용히 대체합니다.
        // sortBy/sortDirection은 프론트에서 안 보낼 수도 있으므로(null) Map.of()/Set.of()에 null을
        // 그대로 넘기면 NPE가 나므로 반드시 null 체크를 먼저 합니다.
        String sortColumn = (sortBy != null && SORT_COLUMNS.containsKey(sortBy))
                ? SORT_COLUMNS.get(sortBy)
                : SORT_COLUMNS.get("GROUP_BUY_ID");
        String direction = (sortDirection != null && ALLOWED_SORT_DIRECTIONS.contains(sortDirection))
                ? sortDirection
                : "DESC";

        List<AdminGroupBuyResponseDto> content = adminGroupBuyMapper.findGroupBuys(
                searchKeyword,
                status,
                progressFilter,
                sortColumn,
                direction,
                offset,
                safeSize
        );

        long totalElements = adminGroupBuyMapper.countGroupBuys(searchKeyword, status, progressFilter);

        return new PageResponse<>(content, safePage, safeSize, totalElements);
    }

    // 공동구매 상세
    @Transactional(readOnly = true)
    public AdminGroupBuyDetailResponseDto getGroupBuyDetail(Long groupBuyId) {
        AdminGroupBuyDetailResponseDto detail = adminGroupBuyMapper.findGroupBuyDetail(groupBuyId);

        if (detail == null) {
            throw new CustomException(ErrorCode.GROUP_BUY_NOT_FOUND);
        }

        return detail;
    }

    // 신규 등록: 이미 GROUP_BUY로 등록된 기존 상품에 새 모집 라운드를 엽니다.
    // (상품 자체는 상품관리 화면에서 이미 만들어져 있어야 하며, 여기서는 만들지 않습니다.)
    @Transactional
    public void createGroupBuy(Long adminMemberId, AdminGroupBuyCreateRequestDto request) {
        if (adminMemberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String saleType = adminGroupBuyMapper.findProductSaleType(request.getProductId());

        if (saleType == null) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        if (!"GROUP_BUY".equals(saleType)) {
            throw new CustomException(ErrorCode.NOT_GROUP_BUY_PRODUCT);
        }

        if (adminGroupBuyMapper.countRecruitingGroupBuy(request.getProductId()) > 0) {
            throw new CustomException(ErrorCode.GROUP_BUY_ALREADY_RECRUITING);
        }

        if (isBlank(request.getTitle())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (request.getTargetQuantity() == null || request.getTargetQuantity() < 1) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (request.getStartDate() == null || request.getEndDate() == null
                || !request.getEndDate().isAfter(request.getStartDate())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        int inserted = adminGroupBuyMapper.insertGroupBuy(
                request.getProductId(),
                adminMemberId,
                request.getTitle().trim(),
                request.getDescription(),
                request.getTargetQuantity(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (inserted != 1) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 모집 정보 수정 (제목·설명·목표 수량·모집 기간)
    @Transactional
    public void updateGroupBuy(Long groupBuyId, AdminGroupBuyUpdateRequestDto request) {
        Integer currentQuantity = adminGroupBuyMapper.findCurrentQuantity(groupBuyId);

        if (currentQuantity == null) {
            throw new CustomException(ErrorCode.GROUP_BUY_NOT_FOUND);
        }

        if (isBlank(request.getTitle())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (request.getTargetQuantity() == null || request.getTargetQuantity() < 1) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 이미 모인 수량보다 목표 수량을 더 낮게 줄일 수는 없습니다.
        if (request.getTargetQuantity() < currentQuantity) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (request.getStartDate() != null && request.getEndDate() != null
                && !request.getEndDate().isAfter(request.getStartDate())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        int updated = adminGroupBuyMapper.updateGroupBuy(
                groupBuyId,
                request.getTitle().trim(),
                request.getDescription(),
                request.getTargetQuantity(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (updated == 0) {
            throw new CustomException(ErrorCode.GROUP_BUY_NOT_FOUND);
        }
    }

    // 상태 강제 변경 (RECRUITING / SUCCESS / FAILED / CANCELLED)
    @Transactional
    public void updateStatus(Long groupBuyId, AdminGroupBuyStatusUpdateRequestDto request) {
        String currentStatus = adminGroupBuyMapper.findStatus(groupBuyId);

        if (currentStatus == null) {
            throw new CustomException(ErrorCode.GROUP_BUY_NOT_FOUND);
        }

        String nextStatus = request.getStatus();

        if (!ALLOWED_STATUSES.contains(nextStatus)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        adminGroupBuyMapper.updateStatus(groupBuyId, nextStatus);
    }

    // 삭제: 참여자가 있는 공동구매는 삭제를 막고, 상태를 취소로 바꾸도록 안내합니다.
    @Transactional
    public void deleteGroupBuy(Long groupBuyId) {
        Integer currentQuantity = adminGroupBuyMapper.findCurrentQuantity(groupBuyId);

        if (currentQuantity == null) {
            throw new CustomException(ErrorCode.GROUP_BUY_NOT_FOUND);
        }

        if (currentQuantity > 0) {
            throw new CustomException(ErrorCode.GROUP_BUY_HAS_PARTICIPANTS);
        }

        adminGroupBuyMapper.deleteParticipants(groupBuyId);

        int deleted = adminGroupBuyMapper.deleteGroupBuy(groupBuyId);

        if (deleted == 0) {
            throw new CustomException(ErrorCode.GROUP_BUY_NOT_FOUND);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
