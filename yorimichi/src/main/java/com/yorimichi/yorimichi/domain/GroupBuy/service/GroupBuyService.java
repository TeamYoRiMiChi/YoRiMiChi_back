package com.yorimichi.yorimichi.domain.GroupBuy.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.repository.GroupBuyMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupBuyService {

    private final GroupBuyMapper groupBuyMapper;

    // 조회만 Controller에서 404로 응답
    @Transactional(readOnly = true)
    public Optional<GroupBuyResponseDto> getGroupBuy(Long groupBuyId) {
        // 공동구매 번호는 1 이상의 값으로
        if (groupBuyId == null || groupBuyId <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return groupBuyMapper.findById(groupBuyId);
    }
}
