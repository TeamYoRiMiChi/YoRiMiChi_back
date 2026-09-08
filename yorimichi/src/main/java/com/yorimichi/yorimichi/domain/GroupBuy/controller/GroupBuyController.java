package com.yorimichi.yorimichi.domain.GroupBuy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.service.GroupBuyService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    public ResponseEntity<ApiResponse<GroupBuyResponseDto>> getGroupBuy(Long groupBuyId) {
        return groupBuyService.getGroupBuy(groupBuyId)
                .map(detail -> ResponseEntity.ok(ApiResponse.success(detail)))
              
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<GroupBuyResponseDto>fail("共同購入が見つかりません。")));
    }
}
