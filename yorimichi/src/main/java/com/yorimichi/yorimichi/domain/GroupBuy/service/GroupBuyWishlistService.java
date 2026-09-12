package com.yorimichi.yorimichi.domain.GroupBuy.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.GroupBuy.entity.GroupBuyWishlist;
import com.yorimichi.yorimichi.domain.GroupBuy.repository.GroupBuyWishlistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupBuyWishlistService {

    private final GroupBuyWishlistRepository groupBuyWishlistRepository;

    @Transactional
    public boolean toggleWishlist(Long memberId, Long productId) {

        boolean exists =
                groupBuyWishlistRepository
                        .existsByMemberIdAndProductId(memberId, productId);

        if (exists) {
            groupBuyWishlistRepository
                    .deleteByMemberIdAndProductId(memberId, productId);

            return false;
        }

        GroupBuyWishlist wishlist =
                new GroupBuyWishlist(memberId, productId);

        groupBuyWishlistRepository.save(wishlist);

        return true;
    }
    @Transactional(readOnly = true)
    public boolean isWishlisted(Long memberId, Long productId) {
        return groupBuyWishlistRepository
                .existsByMemberIdAndProductId(memberId, productId);
    }
}