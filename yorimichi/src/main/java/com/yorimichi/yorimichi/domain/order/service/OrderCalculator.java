package com.yorimichi.yorimichi.domain.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 주문 금액 계산 규칙
 *
 * 계산식을 한곳에 모아둡니다.
 * 주문서 미리보기와 실제 주문 생성이 같은 계산을 써야
 * 화면에 보인 금액과 결제 금액이 어긋나지 않습니다.
 */
public final class OrderCalculator {

    /** 환율 정보가 없을 때 쓰는 기본값 (JPY → KRW) */
    public static final BigDecimal DEFAULT_EXCHANGE_RATE = new BigDecimal("9.5");

    /** 상품 1건당 해외 배송비 */
    private static final BigDecimal OVERSEAS_SHIPPING_PER_ITEM = new BigDecimal("8000");

    /** 주문 1건당 국내 배송비 */
    private static final BigDecimal DOMESTIC_SHIPPING = new BigDecimal("3000");

    /** 국내 배송비 무료 기준 */
    private static final BigDecimal FREE_DOMESTIC_THRESHOLD = new BigDecimal("50000");

    /** 관세 면세 한도 (물품가액 기준) */
    private static final BigDecimal CUSTOMS_FREE_LIMIT = new BigDecimal("150000");

    /** 면세 한도 초과분에 적용할 세율 */
    private static final BigDecimal CUSTOMS_RATE = new BigDecimal("0.13");

    private OrderCalculator() {
    }

    /** 엔화 → 원화 (원 단위 반올림) */
    public static BigDecimal toKrw(BigDecimal priceJpy, BigDecimal rate) {
        if (priceJpy == null) return BigDecimal.ZERO;
        return priceJpy.multiply(rate).setScale(0, RoundingMode.HALF_UP);
    }

    /** 해외 배송비 — 상품 종류 수만큼 부과 */
    public static BigDecimal overseasShipping(int itemKinds) {
        return OVERSEAS_SHIPPING_PER_ITEM.multiply(BigDecimal.valueOf(itemKinds));
    }

    /** 국내 배송비 — 5만원 이상이면 무료 */
    public static BigDecimal domesticShipping(BigDecimal productAmount) {
        return productAmount.compareTo(FREE_DOMESTIC_THRESHOLD) >= 0
                ? BigDecimal.ZERO
                : DOMESTIC_SHIPPING;
    }

    /**
     * 관세 — 물품가액이 15만원을 넘으면 초과분의 13%
     *
     * 실제 관세는 품목별 세율과 부가세가 따로 붙지만,
     * 포트폴리오 범위에서는 단일 세율로 단순화했습니다.
     */
    public static BigDecimal customsDuty(BigDecimal productAmount) {
        if (productAmount.compareTo(CUSTOMS_FREE_LIMIT) <= 0) {
            return BigDecimal.ZERO;
        }
        return productAmount.subtract(CUSTOMS_FREE_LIMIT)
                .multiply(CUSTOMS_RATE)
                .setScale(0, RoundingMode.HALF_UP);
    }
}
