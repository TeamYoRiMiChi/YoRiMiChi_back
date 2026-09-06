package com.yorimichi.yorimichi.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.yorimichi.yorimichi.domain.cart.dto.CartItemAddRequestDto;
import com.yorimichi.yorimichi.domain.cart.dto.CartItemUpdateRequestDto;
import com.yorimichi.yorimichi.domain.cart.dto.CartResponseDto;
import com.yorimichi.yorimichi.domain.cart.service.CartService;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;
import com.yorimichi.yorimichi.global.response.ApiResponse;

/**
 * 장바구니 API (로그인 필요)
 *
 * GET    /api/cart                  내 장바구니 조회
 * POST   /api/cart/items            담기
 * PATCH  /api/cart/items/{id}       수량 변경
 * DELETE /api/cart/items/{id}       한 건 삭제
 * DELETE /api/cart/items            전체 비우기
 *
 * memberId는 JwtAuthenticationFilter가 토큰에서 꺼내 넣어줍니다.
 * 요청 본문으로 받지 않기 때문에 다른 사람의 장바구니를 건드릴 수 없습니다.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponseDto> getCart(@AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(cartService.getCart(requireLogin(memberId)));
    }

    @PostMapping("/items")
    public ApiResponse<CartResponseDto> addItem(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody CartItemAddRequestDto request) {

        return ApiResponse.success(
                cartService.addItem(requireLogin(memberId), request),
                "カートに追加しました。"
        );
    }

    @PatchMapping("/items/{cartItemId}")
    public ApiResponse<CartResponseDto> updateQuantity(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("cartItemId") Long cartItemId,
            @Valid @RequestBody CartItemUpdateRequestDto request) {

        return ApiResponse.success(
                cartService.updateQuantity(requireLogin(memberId), cartItemId, request.getQuantity())
        );
    }

    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<CartResponseDto> removeItem(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("cartItemId") Long cartItemId) {

        return ApiResponse.success(
                cartService.removeItem(requireLogin(memberId), cartItemId)
        );
    }

    @DeleteMapping("/items")
    public ApiResponse<CartResponseDto> clear(@AuthenticationPrincipal Long memberId) {
        return ApiResponse.success(cartService.clear(requireLogin(memberId)));
    }


    /** 토큰이 없으면 principal이 null입니다 */
    private Long requireLogin(Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return memberId;
    }
}
