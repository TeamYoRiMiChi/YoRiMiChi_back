package com.yorimichi.yorimichi.domain.mypage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.mypage.dto.MyPageProfileResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.MyPageProfileMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageProfileService {

	private final MyPageProfileMapper myPageProfileMapper;

	@Transactional(readOnly = true)
	public MyPageProfileResponseDto getMyPageProfile(Long memberId) {
		MyPageProfileResponseDto profile = 
				myPageProfileMapper.findByMemberId(memberId);

		if(profile == null) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
		
		return profile;
	}
}
