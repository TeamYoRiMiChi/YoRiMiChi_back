package com.yorimichi.yorimichi.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.cart.dto.CartItemAddRequestDto;
import com.yorimichi.yorimichi.domain.cart.dto.CartResponseDto;
import com.yorimichi.yorimichi.domain.cart.entity.Cart;
import com.yorimichi.yorimichi.domain.cart.entity.CartItem;
import com.yorimichi.yorimichi.domain.cart.repository.CartMapper;
import com.yorimichi.yorimichi.domain.product.entity.Product;
import com.yorimichi.yorimichi.domain.product.repository.ProductMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    /** 내 장바구니 조회 */
    @Transactional(readOnly = true)
    public CartResponseDto getCart(Long memberId) {
        return cartMapper.findCartByMemberId(memberId)
                .map(cart -> new CartResponseDto(cartMapper.findItemsByCartId(cart.getCartId())))
                .orElseGet(() -> new CartResponseDto(List.of()));
    }

    /**
     * 장바구니에 담기
     *
     * 이미 담긴 상품이면 수량을 더합니다.
     * 담을 때마다 새 행을 만들면 같은 상품이 여러 줄로 보이기 때문입니다.
     */
    @Transactional
    public CartResponseDto addItem(Long memberId, CartItemAddRequestDto request) {

        Product product = productMapper.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!"ACTIVE".equals(product.getStatus())) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Cart cart = getOrCreateCart(memberId);

        // 이미 담긴 상품이면 수량만 더합니다
        CartItem existing = cartMapper.findItem(cart.getCartId(), request.getProductId())
                .orElse(null);

        int newQuantity = (existing == null)
                ? request.getQuantity()
                : existing.getQuantity() + request.getQuantity();

        validateStock(product, newQuantity);

        if (existing == null) {
            cartMapper.insertItem(CartItem.builder()
                    .cartId(cart.getCartId())
                    .productId(request.getProductId())
                    .quantity(newQuantity)
                    .build());
        } else {
            cartMapper.updateItemQuantity(existing.getCartItemId(), newQuantity);
        }

        return getCart(memberId);
    }

    /** 수량 변경 */
    @Transactional
    public CartResponseDto updateQuantity(Long memberId, Long cartItemId, int quantity) {

        CartItem item = cartMapper.findItemByIdAndMember(cartItemId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        Product product = productMapper.findById(item.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        validateStock(product, quantity);

        cartMapper.updateItemQuantity(cartItemId, quantity);

        return getCart(memberId);
    }

    /** 항목 삭제 */
    @Transactional
    public CartResponseDto removeItem(Long memberId, Long cartItemId) {

        // 다른 사람의 장바구니를 지우지 못하도록 소유자를 함께 확인합니다
        cartMapper.findItemByIdAndMember(cartItemId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartMapper.deleteItem(cartItemId);

        return getCart(memberId);
    }

    /** 장바구니 비우기 */
    @Transactional
    public CartResponseDto clear(Long memberId) {
        cartMapper.findCartByMemberId(memberId)
                .ifPresent(cart -> cartMapper.deleteAllItems(cart.getCartId()));

        return getCart(memberId);
    }


    /* ===== 내부 ===== */

    /**
     * 장바구니는 회원가입 시가 아니라 처음 담을 때 만듭니다.
     * 한 번도 담지 않은 회원의 빈 행을 남기지 않기 위해서입니다.
     */
    private Cart getOrCreateCart(Long memberId) {
        return cartMapper.findCartByMemberId(memberId)
                .orElseGet(() -> {
                    Cart cart = Cart.builder().memberId(memberId).build();
                    cartMapper.createCart(cart);
                    return cart;
                });
    }

    private void validateStock(Product product, int quantity) {
        Integer stock = product.getStock();
        if (stock == null || stock < quantity) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }
    }
}
