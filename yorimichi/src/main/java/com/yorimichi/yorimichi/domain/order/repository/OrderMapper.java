package com.yorimichi.yorimichi.domain.order.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.order.entity.Order;
import com.yorimichi.yorimichi.domain.order.entity.OrderAddress;
import com.yorimichi.yorimichi.domain.order.entity.OrderLine;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {

    /* ===== 주문서 준비 ===== */

    /** 회원의 기본 배송지 (없으면 가장 최근 등록분) */
    Optional<OrderAddress> findDefaultAddress(@Param("memberId") Long memberId);

    /** 특정 배송지 (소유자 확인 포함) */
    Optional<OrderAddress> findAddressByIdAndMember(@Param("addressId") Long addressId,
                                                    @Param("memberId") Long memberId);

    /** 회원의 개인통관고유부호 */
    Optional<String> findCustomsCode(@Param("memberId") Long memberId);

    /** 주문서에서 입력한 통관부호를 회원 정보에 저장 */
    void updateCustomsCode(@Param("memberId") Long memberId,
                           @Param("customsCode") String customsCode);

    /** 최신 환율 (JPY → KRW). 없으면 empty */
    Optional<BigDecimal> findLatestExchangeRate();


    /* ===== 주문 생성 ===== */

    void insertOrder(Order order);

    void insertOrderItem(OrderLine item);

    /** 결제 정보 생성 (PENDING 상태로) */
    void insertPayment(@Param("orderId") Long orderId,
                       @Param("paymentMethod") String paymentMethod,
                       @Param("amount") BigDecimal amount);

    /** 배송 정보 생성 (PREPARING 상태로) */
    void insertShipping(@Param("orderId") Long orderId);

    /** 재고 차감 + 판매량 증가 */
    int decreaseStock(@Param("productId") Long productId,
                      @Param("quantity") int quantity);


    /* ===== 주문 조회 ===== */

    Optional<Order> findByIdAndMember(@Param("orderId") Long orderId,
                                      @Param("memberId") Long memberId);

    List<OrderLine> findItemsByOrderId(@Param("orderId") Long orderId);

    /** 주문번호 중복 확인 */
    boolean existsOrderNumber(@Param("orderNumber") String orderNumber);
}
