package com.yorimichi.yorimichi.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.mypage.dto.WishlistResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.WishlistMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistMapper wishlistMapper;

    /** 로그인 회원의 찜 상품 목록 조회 */
    @Transactional(readOnly = true)
    public List<WishlistResponseDto> getWishlist(Long memberId) {
        return wishlistMapper.findAllByMemberId(memberId)
                .stream()
                .map(WishlistResponseDto::new)
                .toList();
    }
}
