package com.yorimichi.yorimichi.domain.cart.controller;

import com.yorimichi.yorimichi.domain.cart.dto.CartItemAddRequestDto;
import com.yorimichi.yorimichi.domain.cart.dto.CartItemUpdateRequestDto;
import com.yorimichi.yorimichi.domain.cart.dto.CartResponseDto;
import com.yorimichi.yorimichi.domain.cart.service.CartService;
import com.yorimichi.yorimichi.global.auth.CurrentMemberId;
import com.yorimichi.yorimichi.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
- 장바구니 API (로그인 필요)
- 
- GET    /api/cart                  내 장바구니 조회
- POST   /api/cart/items            담기
- PATCH  /api/cart/items/{id}       수량 변경
- DELETE /api/cart/items/{id}       한 건 삭제
- DELETE /api/cart/items            전체 비우기
- 
- memberId는 CurrentMemberIdArgumentResolver가 Cognito JWT의 sub를 통해 조회해 넣어줍니다.
- 요청 본문으로 받지 않기 때문에 다른 사람의 장바구니를 건드릴 수 없습니다.
*/

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Returns the authenticated member's cart.
     */
    @GetMapping
    public ApiResponse<CartResponseDto> getCart(
            @CurrentMemberId Long memberId
    ) {
        return ApiResponse.success(
                cartService.getCart(memberId)
        );
    }

    /**
     * Adds an item to the authenticated member's cart.
     */
    @PostMapping("/items")
    public ApiResponse<CartResponseDto> addItem(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody CartItemAddRequestDto request
    ) {
        return ApiResponse.success(
                cartService.addItem(memberId, request),
                "カートに追加しました。"
        );
    }

    /**
     * Updates the quantity of a cart item.
     */
    @PatchMapping("/items/{cartItemId}")
    public ApiResponse<CartResponseDto> updateQuantity(
            @CurrentMemberId Long memberId,
            @PathVariable("cartItemId") Long cartItemId,
            @Valid @RequestBody CartItemUpdateRequestDto request
    ) {
        return ApiResponse.success(
                cartService.updateQuantity(
                        memberId,
                        cartItemId,
                        request.getQuantity()
                )
        );
    }

    /**
     * Removes an item from the authenticated member's cart.
     */
    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<CartResponseDto> removeItem(
            @CurrentMemberId Long memberId,
            @PathVariable("cartItemId") Long cartItemId
    ) {
        return ApiResponse.success(
                cartService.removeItem(memberId, cartItemId)
        );
    }

    /**
     * Removes every item from the authenticated member's cart.
     */
    @DeleteMapping("/items")
    public ApiResponse<CartResponseDto> clear(
            @CurrentMemberId Long memberId
    ) {
        return ApiResponse.success(
                cartService.clear(memberId)
        );
    }
}
