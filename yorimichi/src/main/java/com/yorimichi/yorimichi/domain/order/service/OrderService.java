package com.yorimichi.yorimichi.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.cart.entity.CartItem;
import com.yorimichi.yorimichi.domain.cart.repository.CartMapper;
import com.yorimichi.yorimichi.domain.order.dto.*;
import com.yorimichi.yorimichi.domain.order.entity.Order;
import com.yorimichi.yorimichi.domain.order.entity.OrderAddress;
import com.yorimichi.yorimichi.domain.order.entity.OrderLine;
import com.yorimichi.yorimichi.domain.order.repository.OrderMapper;
import com.yorimichi.yorimichi.domain.product.entity.Product;
import com.yorimichi.yorimichi.domain.product.repository.ProductMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    /**
     * 주문서 데이터 조회
     *
     * 주문 방식이 두 가지입니다.
     *   - 바로구매 : productId를 넘기면 그 상품 하나만
     *   - 장바구니 : productId가 null이면 담긴 상품 전체
     *
     * 실제 주문은 아직 만들지 않고 미리보기만 돌려줍니다.
     */
    @Transactional(readOnly = true)
    public OrderCheckoutResponseDto getCheckout(Long memberId, Long productId, Integer quantity) {

        BigDecimal rate = getExchangeRate();

        List<OrderCheckoutItemDto> items = (productId != null)
                ? List.of(toDirectItem(productId, quantity, rate))
                : loadCartItems(memberId).stream()
                        .map(ci -> toCheckoutItem(ci, rate))
                        .toList();

        Amounts amounts = calculate(items);

        OrderAddressDto address = orderMapper.findDefaultAddress(memberId)
                .map(OrderAddressDto::new)
                .orElse(null);

        String customsCode = orderMapper.findCustomsCode(memberId).orElse(null);

        return new OrderCheckoutResponseDto(
                address,
                customsCode,
                items,
                rate,
                amounts.productAmount,
                amounts.overseasShipping,
                amounts.domesticShipping,
                amounts.customsDuty,
                amounts.total
        );
    }

    /**
     * 주문 생성
     *
     * 하나의 트랜잭션에서 다음을 처리합니다.
     *   1) 주문 대상 확인 (바로구매 또는 장바구니)
     *   2) 배송지 확정 (저장된 주소 또는 직접 입력)
     *   3) 금액 재계산 (프론트가 보낸 값을 믿지 않음)
     *   4) ORDERS · ORDER_ITEM 저장
     *   5) PAYMENT · SHIPPING 생성
     *   6) 재고 차감 · 판매량 증가
     *   7) 장바구니 주문이면 장바구니 비우기
     *
     * 중간에 하나라도 실패하면 전부 되돌아갑니다.
     */
    @Transactional
    public OrderResponseDto createOrder(Long memberId, OrderCreateRequestDto request) {

        BigDecimal rate = getExchangeRate();

        /* 1) 주문할 상품 확정 */
        List<OrderCheckoutItemDto> items = request.isDirectPurchase()
                ? List.of(toDirectItem(request.getProductId(), request.getQuantity(), rate))
                : loadCartItems(memberId).stream()
                        .map(ci -> toCheckoutItem(ci, rate))
                        .toList();

        /* 2) 배송지 확정 */
        ShippingTarget shipping = resolveAddress(memberId, request);

        /* 3) 통관부호 확정 */
        String customsCode = resolveCustomsCode(memberId, request);

        Amounts amounts = calculate(items);

        /* 4) 주문 저장 */
        Order order = Order.builder()
                .memberId(memberId)
                .orderNumber(generateOrderNumber())
                .orderType("NORMAL")
                .receiverName(shipping.receiverName)
                .receiverPhone(shipping.receiverPhone)
                .postalCode(shipping.postalCode)
                .address(shipping.address)
                .addressDetail(shipping.addressDetail)
                .personalCustomsCode(customsCode)
                .appliedExchangeRate(rate)
                .productAmount(amounts.productAmount)
                .shippingFee(amounts.overseasShipping.add(amounts.domesticShipping))
                .customsDuty(amounts.customsDuty)
                .totalAmount(amounts.total)
                .orderStatus("PENDING")
                .build();

        orderMapper.insertOrder(order);

        /* 5) 주문 상품 저장 + 재고 차감 */
        for (OrderCheckoutItemDto item : items) {
            orderMapper.insertOrderItem(OrderLine.builder()
                    .orderId(order.getOrderId())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .priceJpy(item.getPriceJpy())
                    .priceKrw(item.getPriceKrw())
                    .quantity(item.getQuantity())
                    .itemTotal(item.getItemTotal())
                    .build());

            int updated = orderMapper.decreaseStock(item.getProductId(), item.getQuantity());
            if (updated == 0) {
                // WHERE stock >= quantity 조건에 걸린 경우 = 재고 부족
                throw new CustomException(ErrorCode.OUT_OF_STOCK);
            }
        }

        /* 6) 결제·배송 정보 생성 */
        orderMapper.insertPayment(order.getOrderId(), request.getPaymentMethod(), amounts.total);
        orderMapper.insertShipping(order.getOrderId());

        /* 7) 장바구니 주문이었으면 비웁니다. 바로구매는 건드리지 않습니다 */
        if (!request.isDirectPurchase()) {
            cartMapper.findCartByMemberId(memberId)
                    .ifPresent(cart -> cartMapper.deleteAllItems(cart.getCartId()));
        }

        log.info("order created - orderNumber={}, memberId={}, direct={}, total={}",
                order.getOrderNumber(), memberId, request.isDirectPurchase(), amounts.total);

        List<OrderLine> savedLines = orderMapper.findItemsByOrderId(order.getOrderId());
        return new OrderResponseDto(order, savedLines);
    }

    /** 주문 상세 조회 */
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long memberId, Long orderId) {
        Order order = orderMapper.findByIdAndMember(orderId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return new OrderResponseDto(order, orderMapper.findItemsByOrderId(orderId));
    }


    /* ============================================
       내부 헬퍼
    ============================================ */

    /** 바로구매 — 상품 하나를 주문 항목으로 만듭니다 */
    private OrderCheckoutItemDto toDirectItem(Long productId, Integer quantity, BigDecimal rate) {

        int qty = (quantity == null || quantity < 1) ? 1 : quantity;

        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!"ACTIVE".equals(product.getStatus())) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (product.getStock() == null || product.getStock() < qty) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }

        return new OrderCheckoutItemDto(
                product.getProductId(),
                product.getBrand(),
                product.getProductName(),
                product.getThumbnailUrl(),
                product.getPriceJpy(),
                OrderCalculator.toKrw(product.getPriceJpy(), rate),
                qty,
                OrderCalculator.overseasShipping(1),
                BigDecimal.ZERO
        );
    }

    /** 장바구니를 읽고 비어 있으면 예외 */
    private List<CartItem> loadCartItems(Long memberId) {
        List<CartItem> items = cartMapper.findCartByMemberId(memberId)
                .map(cart -> cartMapper.findItemsByCartId(cart.getCartId()))
                .orElse(List.of());

        if (items.isEmpty()) {
            throw new CustomException(ErrorCode.CART_EMPTY);
        }
        return items;
    }

    /**
     * 배송지 확정
     *
     * addressId가 있으면 저장된 주소를,
     * 없으면 요청에 담긴 직접 입력값을 씁니다.
     * 둘 다 없으면 기본 배송지를 찾고, 그것도 없으면 예외입니다.
     */
    private ShippingTarget resolveAddress(Long memberId, OrderCreateRequestDto request) {

        if (request.getAddressId() != null) {
            OrderAddress saved = orderMapper
                    .findAddressByIdAndMember(request.getAddressId(), memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));

            return ShippingTarget.from(saved);
        }

        if (request.hasManualAddress()) {
            validateManualAddress(request);
            return new ShippingTarget(
                    request.getReceiverName().trim(),
                    request.getReceiverPhone().trim(),
                    request.getPostalCode().trim(),
                    request.getAddress().trim(),
                    request.getAddressDetail() == null ? null : request.getAddressDetail().trim()
            );
        }

        return orderMapper.findDefaultAddress(memberId)
                .map(ShippingTarget::from)
                .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));
    }

    private void validateManualAddress(OrderCreateRequestDto request) {
        if (isBlank(request.getReceiverName())
                || isBlank(request.getReceiverPhone())
                || isBlank(request.getPostalCode())
                || isBlank(request.getAddress())) {
            throw new CustomException(ErrorCode.ADDRESS_NOT_FOUND);
        }
    }

    /**
     * 통관부호 확정
     *
     * 회원 정보에 저장된 값을 우선 쓰고,
     * 없으면 주문서에서 입력한 값을 쓰면서 회원 정보에도 저장합니다.
     * 다음 주문부터는 다시 입력하지 않아도 되도록요.
     */
    private String resolveCustomsCode(Long memberId, OrderCreateRequestDto request) {

        String saved = orderMapper.findCustomsCode(memberId).orElse(null);
        if (!isBlank(saved)) {
            return saved;
        }

        String input = request.getPersonalCustomsCode();
        if (isBlank(input)) {
            throw new CustomException(ErrorCode.CUSTOMS_CODE_REQUIRED);
        }

        String code = input.trim().toUpperCase();
        orderMapper.updateCustomsCode(memberId, code);
        return code;
    }

    /** 최신 환율. 없으면 기본값 */
    private BigDecimal getExchangeRate() {
        return orderMapper.findLatestExchangeRate()
                .orElse(OrderCalculator.DEFAULT_EXCHANGE_RATE);
    }

    private OrderCheckoutItemDto toCheckoutItem(CartItem ci, BigDecimal rate) {
        if (!ci.isAvailable()) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }

        return new OrderCheckoutItemDto(
                ci.getProductId(),
                ci.getBrand(),
                ci.getProductName(),
                ci.getThumbnailUrl(),
                ci.getPriceJpy(),
                OrderCalculator.toKrw(ci.getPriceJpy(), rate),
                ci.getQuantity(),
                OrderCalculator.overseasShipping(1),
                BigDecimal.ZERO   // 국내 배송비는 주문 단위라 상품별로는 0
        );
    }

    /** 상품 소계와 배송비·관세를 합쳐 최종 금액을 만듭니다 */
    private Amounts calculate(List<OrderCheckoutItemDto> items) {
        BigDecimal productAmount = items.stream()
                .map(OrderCheckoutItemDto::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overseas = OrderCalculator.overseasShipping(items.size());
        BigDecimal domestic = OrderCalculator.domesticShipping(productAmount);
        BigDecimal customs = OrderCalculator.customsDuty(productAmount);

        BigDecimal total = productAmount.add(overseas).add(domestic).add(customs);

        return new Amounts(productAmount, overseas, domestic, customs, total);
    }

    /** 주문번호 생성 (YM-20260907-A1B2C3) */
    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (int i = 0; i < 5; i++) {
            String suffix = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 6)
                    .toUpperCase();

            String orderNumber = "YM-" + date + "-" + suffix;

            if (!orderMapper.existsOrderNumber(orderNumber)) {
                return orderNumber;
            }
        }

        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }


    /* ===== 내부 데이터 묶음 ===== */

    /** 주문에 실제로 기록할 배송지 값 */
    private record ShippingTarget(String receiverName,
                                  String receiverPhone,
                                  String postalCode,
                                  String address,
                                  String addressDetail) {

        static ShippingTarget from(OrderAddress a) {
            return new ShippingTarget(
                    a.getReceiverName(),
                    a.getReceiverPhone(),
                    a.getPostalCode(),
                    a.getAddress(),
                    a.getAddressDetail()
            );
        }
    }

    private record Amounts(BigDecimal productAmount,
                           BigDecimal overseasShipping,
                           BigDecimal domesticShipping,
                           BigDecimal customsDuty,
                           BigDecimal total) {
    }
}
