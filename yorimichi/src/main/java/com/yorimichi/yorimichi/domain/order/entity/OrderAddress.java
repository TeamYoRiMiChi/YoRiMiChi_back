package com.yorimichi.yorimichi.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배송지 (ADDRESS 테이블)
 *
 * 주문서에서 기본 배송지를 불러올 때 사용합니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddress {

    private Long addressId;
    private Long memberId;
    private String receiverName;
    private String receiverPhone;
    private String postalCode;
    private String address;
    private String addressDetail;
    private Boolean isDefault;
}
