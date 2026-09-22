package com.yorimichi.yorimichi.domain.admin.GroupBuy.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyDetailResponseDto;
import com.yorimichi.yorimichi.domain.admin.GroupBuy.dto.AdminGroupBuyResponseDto;

@Mapper
public interface AdminGroupBuyMapper {

    // 공동구매 목록 (검색어: 상품명·모집 제목 / 상태 필터 / 달성률 필터 / 정렬)
    // sortColumn·sortDirection은 Service에서 화이트리스트로 검증한 값만 넘어옵니다.
    List<AdminGroupBuyResponseDto> findGroupBuys(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("progress") String progress,
            @Param("sortColumn") String sortColumn,
            @Param("sortDirection") String sortDirection,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countGroupBuys(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("progress") String progress
    );

    // 공동구매 상세
    AdminGroupBuyDetailResponseDto findGroupBuyDetail(@Param("groupBuyId") Long groupBuyId);

    // 수정 전 검증용 (없는 공동구매면 null)
    String findStatus(@Param("groupBuyId") Long groupBuyId);

    Integer findCurrentQuantity(@Param("groupBuyId") Long groupBuyId);

    // 모집 정보 수정 (제목·설명·목표 수량·모집 기간)
    int updateGroupBuy(
            @Param("groupBuyId") Long groupBuyId,
            @Param("title") String title,
            @Param("description") String description,
            @Param("targetQuantity") Integer targetQuantity,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 상태 강제 변경
    int updateStatus(
            @Param("groupBuyId") Long groupBuyId,
            @Param("status") String status
    );

    // 신규 등록: 이미 존재하는 상품의 판매유형 확인 (없으면 null)
    String findProductSaleType(@Param("productId") Long productId);

    // 신규 등록: 해당 상품에 이미 모집 중인 라운드가 있는지
    int countRecruitingGroupBuy(@Param("productId") Long productId);

    // 신규 등록: 기존 상품에 새 모집 라운드 생성 (status는 항상 RECRUITING, current_quantity는 0으로 시작)
    int insertGroupBuy(
            @Param("productId") Long productId,
            @Param("creatorId") Long creatorId,
            @Param("title") String title,
            @Param("description") String description,
            @Param("targetQuantity") Integer targetQuantity,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 삭제: 참여자 기록 먼저 제거 (참여자가 없는 공동구매만 삭제 허용하므로 보통 0건)
    int deleteParticipants(@Param("groupBuyId") Long groupBuyId);

    int deleteGroupBuy(@Param("groupBuyId") Long groupBuyId);
}
