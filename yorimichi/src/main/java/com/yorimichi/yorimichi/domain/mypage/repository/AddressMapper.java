package com.yorimichi.yorimichi.domain.mypage.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.order.entity.OrderAddress;

/**
 * 마이페이지 - 배송지 관리 DB 접근
 *
 * ADDRESS 테이블은 order 패키지(OrderMapper)에서 주문서 조회용으로 이미 쓰고 있어
 * 엔티티(OrderAddress)는 그대로 재사용하고, 고객이 직접 배송지를 등록/수정/삭제하는
 * 마이페이지 전용 쿼리만 이 매퍼에 따로 둡니다.
 */
@Mapper
public interface AddressMapper {

    /** 로그인 회원의 배송지 전체 (기본 배송지 먼저, 그다음 최신순) */
    List<OrderAddress> findMyAddresses(@Param("memberId") Long memberId);

    /** 배송지 단건 (본인 소유 확인 포함) */
    Optional<OrderAddress> findByIdAndMember(@Param("addressId") Long addressId,
                                              @Param("memberId") Long memberId);

    /** 회원이 등록해둔 배송지가 하나라도 있는지 (첫 배송지는 자동으로 기본 배송지가 됩니다) */
    boolean existsAnyAddress(@Param("memberId") Long memberId);

    /** 등록 — 성공하면 address(파라미터)의 addressId에 생성된 PK가 채워집니다 */
    int insertAddress(OrderAddress address);

    /** 수정 (본인 소유 확인 포함) */
    int updateAddress(OrderAddress address);

    /** 삭제 (본인 소유 확인 포함) */
    int deleteAddress(@Param("addressId") Long addressId, @Param("memberId") Long memberId);

    /** 기본 배송지가 삭제됐을 때, 가장 최근에 등록한 배송지를 기본으로 승격 */
    int promoteLatestToDefault(@Param("memberId") Long memberId);

    /** 이 회원의 기존 기본 배송지 플래그를 내림 (기본 배송지를 바꾸기 전에 먼저 호출) */
    int clearDefault(@Param("memberId") Long memberId);

    /** 지정한 배송지를 기본 배송지로 세팅 (본인 소유 확인 포함) */
    int setDefault(@Param("addressId") Long addressId, @Param("memberId") Long memberId);
}
