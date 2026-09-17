package com.yorimichi.yorimichi.domain.postal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.domain.postal.dto.PostalAddressDto;
import com.yorimichi.yorimichi.domain.postal.service.PostalCodeService;
import com.yorimichi.yorimichi.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * 우편번호 → 주소 검색 (zipcloud 외부 API 프록시)
 *
 * 회원가입 화면(비로그인)에서도 쓰기 때문에 인증을 요구하지 않습니다.
 * (SecurityConfig에 GET /api/postal-code/** permitAll 등록)
 *
 * 프론트: 회원가입 페이지, 마이페이지 - 배송지 관리
 */
@RestController
@RequestMapping("/api/postal-code")
@RequiredArgsConstructor
public class PostalCodeController {

    private final PostalCodeService postalCodeService;

    @GetMapping("/{zipcode}")
    public ApiResponse<List<PostalAddressDto>> search(@PathVariable String zipcode) {
        return ApiResponse.success(postalCodeService.search(zipcode));
    }
}
