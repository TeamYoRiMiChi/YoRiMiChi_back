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

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<GroupBuyResponseDto>> getGroupBuy(
            @PathVariable("productId") Long productId) {
        return groupBuyService.getGroupBuyByProductId(productId)
                .map(detail -> ResponseEntity.ok(ApiResponse.success(detail)))
              
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<GroupBuyResponseDto>fail("共同購入が見つかりません。")));
    }
}
