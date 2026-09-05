package com.yorimichi.yorimichi.domain.cart.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yorimichi.yorimichi.domain.cart.entity.Cart;
import com.yorimichi.yorimichi.domain.cart.entity.CartItem;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CartMapper {

    /* ===== CART ===== */

    /** 회원의 장바구니 조회 */
    Optional<Cart> findCartByMemberId(@Param("memberId") Long memberId);

    /** 장바구니 생성 (회원가입 시가 아니라 처음 담을 때 만듭니다) */
    void createCart(Cart cart);


    /* ===== CART_ITEM ===== */

    /** 장바구니에 담긴 상품 목록 (상품 정보 조인) */
    List<CartItem> findItemsByCartId(@Param("cartId") Long cartId);

    /** 이미 담긴 상품인지 확인 */
    Optional<CartItem> findItem(@Param("cartId") Long cartId,
                                @Param("productId") Long productId);

    /** 소유자 확인까지 겸한 단건 조회 */
    Optional<CartItem> findItemByIdAndMember(@Param("cartItemId") Long cartItemId,
                                             @Param("memberId") Long memberId);

    void insertItem(CartItem item);

    void updateItemQuantity(@Param("cartItemId") Long cartItemId,
                            @Param("quantity") int quantity);

    void deleteItem(@Param("cartItemId") Long cartItemId);

    /** 장바구니 비우기 */
    void deleteAllItems(@Param("cartId") Long cartId);
}
