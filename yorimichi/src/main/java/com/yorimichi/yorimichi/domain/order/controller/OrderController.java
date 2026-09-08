package com.yorimichi.yorimichi.domain.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.yorimichi.yorimichi.domain.order.dto.OrderCheckoutResponseDto;
import com.yorimichi.yorimichi.domain.order.dto.OrderCreateRequestDto;
import com.yorimichi.yorimichi.domain.order.dto.OrderResponseDto;
import com.yorimichi.yorimichi.domain.order.service.OrderService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

/**
 * 주문 API (로그인 필요)
 *
 * GET  /api/orders/checkout   주문서 데이터 (배송지 · 통관부호 · 상품 · 금액)
 * POST /api/orders            주문 생성
 * GET  /api/orders/{orderId}  주문 상세
 *
 * memberId는 JwtAuthenticationFilter가 토큰에서 꺼내 넣어줍니다.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문서 화면에 필요한 데이터를 한 번에 내려줍니다
     *
     * @param productId 바로구매할 상품. 없으면 장바구니 주문
     * @param quantity  바로구매 수량 (기본 1)
     */
    @GetMapping("/checkout")
    public ApiResponse<OrderCheckoutResponseDto> getCheckout(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "quantity", required = false, defaultValue = "1") Integer quantity) {

        return ApiResponse.success(
                orderService.getCheckout(requireLogin(memberId), productId, quantity)
        );
    }

    @PostMapping
    public ApiResponse<OrderResponseDto> createOrder(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody OrderCreateRequestDto request) {

        return ApiResponse.success(
                orderService.createOrder(requireLogin(memberId), request),
                "ご注文が完了しました。"
        );
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> getOrder(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("orderId") Long orderId) {

        return ApiResponse.success(orderService.getOrder(requireLogin(memberId), orderId));
    }


    private Long requireLogin(Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return memberId;
    }
}
