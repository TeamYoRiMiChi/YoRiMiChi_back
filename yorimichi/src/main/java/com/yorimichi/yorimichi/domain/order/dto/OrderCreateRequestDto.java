package com.yorimichi.yorimichi.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문 생성 요청
 *
 * 금액은 받지 않습니다.
 * 프론트가 보낸 금액을 그대로 믿으면 사용자가 개발자 도구로
 * 총액을 1원으로 바꿔 결제할 수 있기 때문입니다.
 * 서버가 상품 가격을 다시 읽어 직접 계산합니다.
 *
 * 주문 방식은 두 가지입니다.
 *   - 장바구니 주문 : productId가 null → 담긴 상품 전체
 *   - 바로구매     : productId 지정   → 그 상품만
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderCreateRequestDto {

    /* ===== 주문 대상 ===== */

    /** 바로구매할 상품. null이면 장바구니 주문 */
    private Long productId;

    /** 바로구매 수량 (productId가 있을 때만 사용) */
    private Integer quantity;


    /* ===== 배송지 ===== */

    /**
     * 저장된 배송지 id.
     * null이면 아래 직접 입력값을 사용합니다.
     */
    private Long addressId;

    private String receiverName;
    private String receiverPhone;
    private String postalCode;
    private String address;
    private String addressDetail;

    /** 배송 메모 (선택) */
    private String deliveryMemo;


    /* ===== 통관 ===== */

    /**
     * 개인통관고유부호.
     * 회원 정보에 저장된 값이 없을 때 이 값을 씁니다.
     */
    private String personalCustomsCode;


    /* ===== 결제 ===== */

    @NotBlank(message = "決済方法を選択してください。")
    private String paymentMethod;   // CARD | KAKAOPAY | NAVERPAY | TRANSFER


    /** 바로구매 주문인지 */
    public boolean isDirectPurchase() {
        return productId != null;
    }

    /** 배송지를 직접 입력했는지 */
    public boolean hasManualAddress() {
        return addressId == null
                && receiverName != null && !receiverName.isBlank()
                && address != null && !address.isBlank();
    }
}
