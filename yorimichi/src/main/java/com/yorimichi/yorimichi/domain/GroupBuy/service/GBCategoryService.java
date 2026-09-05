package com.yorimichi.yorimichi.domain.GroupBuy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GBCategoryResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.repository.GBCategoryMapper;

import java.util.List;

/**
 * 카테고리 비즈니스 로직
 *
 * 지금은 조회만 하므로 단순하지만,
 * 나중에 "상품이 없는 카테고리는 숨기기" 같은 규칙이 생기면 여기에 넣습니다.
 * Controller는 요청을 받고, Service는 판단하고, Mapper는 DB만 다루는 역할 분담입니다.
 */
@Service
@RequiredArgsConstructor
public class GBCategoryService {

    private final GBCategoryMapper gbCategoryMapper;

    /** 전체 카테고리 조회 */
    @Transactional(readOnly = true)
    public List<GBCategoryResponseDto> getCategories() {
    	System.out.println("GBCategoryService:::::::");
        return gbCategoryMapper.findAll()
                .stream()
                .map(GBCategoryResponseDto::new)
                .toList();
    }
}
