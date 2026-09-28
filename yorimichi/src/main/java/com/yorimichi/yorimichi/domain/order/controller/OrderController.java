package com.yorimichi.yorimichi.domain.order.controller;

import com.yorimichi.yorimichi.domain.order.dto.OrderCheckoutResponseDto;
import com.yorimichi.yorimichi.domain.order.dto.OrderCreateRequestDto;
import com.yorimichi.yorimichi.domain.order.dto.OrderResponseDto;
import com.yorimichi.yorimichi.domain.order.service.OrderService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 로그인한 회원의 주문을 처리하는 API입니다.
 *
 * GET  /api/orders/checkout   주문서에 필요한 배송지, 통관부호, 상품 및 금액 조회
 * POST /api/orders            주문 생성
 * GET  /api/orders/{orderId}  주문 상세 조회
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Returns the information needed to render the checkout page.
     *
     * @param memberId    authenticated member's internal database ID
     * @param productId   product selected for an immediate purchase
     * @param quantity    quantity for an immediate purchase
     * @param saleType    sale type selected by the client
     * @param cartItemIds cart item IDs included in the order
     */
    @GetMapping("/checkout")
    public ApiResponse<OrderCheckoutResponseDto> getCheckout(
            @CurrentMemberId Long memberId,
            @RequestParam(
                    value = "productId",
                    required = false
            ) Long productId,
            @RequestParam(
                    value = "quantity",
                    required = false,
                    defaultValue = "1"
            ) Integer quantity,
            @RequestParam(
                    value = "saleType",
                    required = false
            ) String saleType,
            @RequestParam(
                    value = "cartItemIds",
                    required = false
            ) List<Long> cartItemIds
    ) {
        return ApiResponse.success(
                orderService.getCheckout(
                        memberId,
                        productId,
                        quantity,
                        saleType,
                        cartItemIds
                )
        );
    }

    /**
     * Creates an order for the authenticated member.
     */
    @PostMapping
    public ApiResponse<OrderResponseDto> createOrder(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody OrderCreateRequestDto request
    ) {
        return ApiResponse.success(
                orderService.createOrder(memberId, request),
                "ご注文が完了しました。"
        );
    }

    /**
     * Returns an order owned by the authenticated member.
     */
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> getOrder(
            @CurrentMemberId Long memberId,
            @PathVariable("orderId") Long orderId
    ) {
        return ApiResponse.success(
                orderService.getOrder(memberId, orderId)
        );
    }
}