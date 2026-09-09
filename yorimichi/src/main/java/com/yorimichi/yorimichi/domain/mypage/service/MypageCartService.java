package com.yorimichi.yorimichi.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.mypage.dto.MypageCartResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.MypageCartMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageCartService {
	
	private final MypageCartMapper mypageCartMapper;
	
	//로그인 회원의 장바구니 상품 목록 조회
	@Transactional(readOnly = true)
	public List<MypageCartResponseDto> getMypageCart(Long memberId){
		return mypageCartMapper.findAllByMemberId(memberId)
				.stream()
				.map(MypageCartResponseDto::new)
				.toList();
	}
}
