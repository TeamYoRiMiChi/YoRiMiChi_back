package com.yorimichi.yorimichi.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.cart.entity.CartItem;
import com.yorimichi.yorimichi.domain.cart.repository.CartMapper;
import com.yorimichi.yorimichi.domain.mypage.dto.MyCouponResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.CouponMapper;
import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.service.GroupBuyService;
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
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final CouponMapper couponMapper;
    private final GroupBuyService groupBuyService;

    /**
     * 주문서 데이터 조회
     *
     * 주문 방식이 두 가지입니다.
     *   - 바로구매 : productId를 넘기면 그 상품 하나만
     *   - 장바구니 : productId가 null이면 담긴 상품 전체
     *
     * 실제 주문은 아직 만들지 않고 미리보기만 돌려줍니다.
     * 쿠폰 할인은 여기서 계산하지 않습니다 — 어떤 쿠폰을 쓸지는 이 화면 이후에
     * 사용자가 고르므로, 실제 반영은 주문 생성(createOrder) 시점에만 이뤄집니다.
     */
    @Transactional(readOnly = true)
    public OrderCheckoutResponseDto getCheckout(Long memberId, Long productId, Integer quantity,
                                                String saleType, List<Long> cartItemIds) {

        BigDecimal rate = getExchangeRate();

        List<OrderCheckoutItemDto> items = (productId != null)
                ? List.of(toDirectItem(productId, quantity, rate, saleType))
                : loadOrderableCartItems(memberId, saleType, cartItemIds).stream()
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
     *   3) 금액 재계산 (프론트가 보낸 값을 믿지 않음) + 쿠폰 검증·할인 계산
     *   4) ORDERS · ORDER_ITEM 저장
     *   5) 쿠폰을 썼다면 MEMBER_COUPON을 사용 처리 (이 주문에 묶어 이력을 남김)
     *   6) PAYMENT · SHIPPING 생성
     *   7) 재고 차감 · 판매량 증가
     *   8) 장바구니 주문이면 장바구니 비우기
     *
     * 중간에 하나라도 실패하면 전부 되돌아갑니다.
     */
    @Transactional
    public OrderResponseDto createOrder(Long memberId, OrderCreateRequestDto request) {

        BigDecimal rate = getExchangeRate();
        boolean directGroupBuy = request.isDirectPurchase()
                && "GROUP_BUY".equals(normalizeSaleType(request.getSaleType()));
        GroupBuyResponseDto groupBuy = directGroupBuy
                ? groupBuyService.getGroupBuyByProductId(request.getProductId())
                        .orElseThrow(() -> new CustomException(ErrorCode.GROUP_BUY_NOT_FOUND))
                : null;

        /* 1) 주문할 상품 확정 */
        List<CartItem> orderedCartItems = request.isDirectPurchase()
                ? List.of()
                : loadOrderableCartItems(
                        memberId, request.getSaleType(), request.getCartItemIds());

        List<CartItem> groupBuyCartItems = orderedCartItems.stream()
                .filter(item -> "GROUP_BUY".equals(item.getSaleType()))
                .toList();
        boolean containsGroupBuy = directGroupBuy || !groupBuyCartItems.isEmpty();
        boolean containsOverseas = !request.isDirectPurchase()
                && orderedCartItems.stream().anyMatch(item -> !"GROUP_BUY".equals(item.getSaleType()));
        String orderType = containsGroupBuy
                ? (containsOverseas ? "MIXED" : "GROUP_BUY")
                : "NORMAL";
        Long orderGroupBuyId = groupBuy == null ? singleGroupBuyId(groupBuyCartItems) : groupBuy.getGroupBuyId();

        List<OrderCheckoutItemDto> items = request.isDirectPurchase()
                ? List.of(toDirectItem(
                        request.getProductId(), request.getQuantity(), rate, request.getSaleType()))
                : orderedCartItems.stream().map(ci -> toCheckoutItem(ci, rate)).toList();

        /* 2) 배송지 확정 */
        ShippingTarget shipping = resolveAddress(memberId, request);

        /* 3) 통관부호 확정 */
        String customsCode = resolveCustomsCode(memberId, request);

        Amounts amounts = calculate(items);

        /* 3-1) 쿠폰 검증 + 할인 계산. 쿠폰을 쓰지 않으면 appliedCoupon은 null, 할인은 0원입니다. */
        MyCouponResponseDto appliedCoupon =
                resolveCoupon(memberId, request.getMemberCouponId(), amounts.productAmount);

        BigDecimal couponDiscount = appliedCoupon != null
                ? OrderCalculator.couponDiscount(
                        appliedCoupon.getDiscountType(),
                        appliedCoupon.getDiscountValue(),
                        appliedCoupon.getMaxDiscountAmount(),
                        amounts.productAmount)
                : BigDecimal.ZERO;

        BigDecimal finalTotal = amounts.total.subtract(couponDiscount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        /* 4) 주문 저장 */
        Order order = Order.builder()
                .memberId(memberId)
                .groupBuyId(orderGroupBuyId)
                .orderNumber(generateOrderNumber())
                .orderType(orderType)
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
                .totalAmount(finalTotal)
                .orderStatus(containsGroupBuy ? "PAID" : "PENDING")
                .build();

        orderMapper.insertOrder(order);

        /*
         * 4-1) 쿠폰 사용 처리.
         * WHERE status = 'AVAILABLE' 조건으로 갱신하므로, 검증 이후 동시에 다른 주문에서
         * 먼저 같은 쿠폰을 써버린 경우 0건이 갱신됩니다 — 그러면 트랜잭션 전체를 되돌립니다.
         */
        if (appliedCoupon != null) {
            int marked = couponMapper.markCouponUsed(appliedCoupon.getMemberCouponId(), order.getOrderId());
            if (marked == 0) {
                throw new CustomException(ErrorCode.COUPON_NOT_USABLE);
            }
        }

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

        /* 6) 결제·배송 정보 생성 (쿠폰 할인이 반영된 최종 금액으로 결제 생성) */
        orderMapper.insertPayment(
                order.getOrderId(),
                request.getPaymentMethod(),
                containsGroupBuy ? "PAID" : "PENDING",
                finalTotal
        );
        orderMapper.insertShipping(order.getOrderId());

        /* 테스트 결제 완료 후에만 공동구매 참여 수량을 반영합니다. */
        if (directGroupBuy) {
            groupBuyService.participate(memberId, request.getProductId(), request.getQuantity());
        }
        for (CartItem item : groupBuyCartItems) {
            groupBuyService.participate(memberId, item.getProductId(), item.getQuantity());
        }

        /* 7) 주문한 장바구니 항목만 삭제합니다. 품절 상품과 다른 판매 방식은 남깁니다. */
        if (!request.isDirectPurchase()) {
            orderedCartItems.forEach(item -> cartMapper.deleteItem(item.getCartItemId()));
        }

        log.info("order created - orderNumber={}, memberId={}, direct={}, couponDiscount={}, total={}",
                order.getOrderNumber(), memberId, request.isDirectPurchase(), couponDiscount, finalTotal);

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
    private OrderCheckoutItemDto toDirectItem(Long productId, Integer quantity, BigDecimal rate,
                                              String requestedSaleType) {

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
                requestedSaleType == null ? product.getSaleType() : normalizeSaleType(requestedSaleType),
                product.getBrand(),
                product.getProductName(),
                product.getThumbnailUrl(),
                product.getPriceJpy(),
                OrderCalculator.toKrw(product.getPriceJpy(), rate),
                qty,
                BigDecimal.ZERO,
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

    /** 판매 방식이 같고 현재 주문 가능한 장바구니 상품만 고릅니다. */
    private List<CartItem> loadOrderableCartItems(Long memberId, String saleType,
                                                  List<Long> cartItemIds) {
        String normalizedSaleType = normalizeSaleType(saleType);
        boolean hasSelection = cartItemIds != null && !cartItemIds.isEmpty();

        List<CartItem> matchingItems = loadCartItems(memberId).stream()
                .filter(item -> !hasSelection || cartItemIds.contains(item.getCartItemId()))
                .filter(item -> normalizedSaleType == null
                        || normalizedSaleType.equals(item.getSaleType()))
                .toList();

        if (matchingItems.isEmpty()) {
            throw new CustomException(ErrorCode.CART_EMPTY);
        }

        List<CartItem> orderableItems = matchingItems.stream()
                .filter(CartItem::isAvailable)
                .toList();

        if (orderableItems.isEmpty()) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }
        return orderableItems;
    }

    private String normalizeSaleType(String saleType) {
        if (saleType == null || saleType.isBlank()) return null;

        String normalized = saleType.trim().toUpperCase();
        if (!"OVERSEAS".equals(normalized) && !"GROUP_BUY".equals(normalized)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return normalized;
    }

    /** 주문에 공동구매가 하나만 포함된 경우 ORDERS의 대표 공동구매 번호를 저장합니다. */
    private Long singleGroupBuyId(List<CartItem> groupBuyItems) {
        List<Long> ids = groupBuyItems.stream()
                .map(CartItem::getGroupBuyId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        return ids.size() == 1 ? ids.get(0) : null;
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

    /**
     * 쿠폰 검증.
     *
     * memberCouponId가 없으면 쿠폰을 쓰지 않는 주문이라 null을 돌려줍니다.
     * 있으면 실제로 이 회원 것이 맞는지, 지금 쓸 수 있는 상태(AVAILABLE)인지,
     * 최소 주문 금액을 채웠는지까지 확인합니다.
     * (프론트에서도 이미 걸러서 보내지만, 요청을 조작해 보낼 수 있으므로 서버에서 다시 검증합니다)
     */
    private MyCouponResponseDto resolveCoupon(Long memberId, Long memberCouponId, BigDecimal productAmount) {
        if (memberCouponId == null) {
            return null;
        }

        MyCouponResponseDto coupon = couponMapper.findMyCouponById(memberCouponId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUPON_NOT_FOUND));

        if (!"AVAILABLE".equals(coupon.getStatus())) {
            throw new CustomException(ErrorCode.COUPON_NOT_USABLE);
        }

        if (productAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new CustomException(ErrorCode.COUPON_MIN_ORDER_AMOUNT_NOT_MET);
        }

        return coupon;
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
                ci.getSaleType(),
                ci.getBrand(),
                ci.getProductName(),
                ci.getThumbnailUrl(),
                ci.getPriceJpy(),
                OrderCalculator.toKrw(ci.getPriceJpy(), rate),
                ci.getQuantity(),
                BigDecimal.ZERO,
                BigDecimal.ZERO   // 국내 배송비는 주문 단위라 상품별로는 0
        );
    }

    /** 상품 소계와 배송비·관세를 합쳐 최종 금액을 만듭니다 */
    private Amounts calculate(List<OrderCheckoutItemDto> items) {
        BigDecimal productAmount = items.stream()
                .map(OrderCheckoutItemDto::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overseasProductAmount = items.stream()
                .filter(item -> !"GROUP_BUY".equals(item.getSaleType()))
                .map(OrderCheckoutItemDto::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean hasOverseasProduct = overseasProductAmount.compareTo(BigDecimal.ZERO) > 0;

        /* 해외직구 상품이 하나라도 있으면 주문당 배송비를 한 번만 부과합니다. */
        BigDecimal overseas = OrderCalculator.overseasShipping(hasOverseasProduct ? 1 : 0);
        /* 공동구매도 국내 배송 대상이므로 전체 상품 금액 기준으로 주문당 한 번 부과합니다. */
        BigDecimal domestic = OrderCalculator.domesticShipping(productAmount);
        BigDecimal customs = hasOverseasProduct
                ? OrderCalculator.customsDuty(overseasProductAmount)
                : BigDecimal.ZERO;

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
