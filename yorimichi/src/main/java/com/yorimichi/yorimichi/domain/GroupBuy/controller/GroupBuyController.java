package com.yorimichi.yorimichi.domain.GroupBuy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yorimichi.yorimichi.domain.GroupBuy.dto.GroupBuyResponseDto;
import com.yorimichi.yorimichi.domain.GroupBuy.service.GroupBuyService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/group-buys")
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    @GetMapping("/{groupBuyId}")
    public ResponseEntity<ApiResponse<GroupBuyResponseDto>> getGroupBuy(
            @PathVariable("groupBuyId") Long groupBuyId) {
        return groupBuyService.getGroupBuy(groupBuyId)
                .map(detail -> ResponseEntity.ok(ApiResponse.success(detail)))
              
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<GroupBuyResponseDto>fail("共同購入が見つかりません。")));
    }
}
