package com.yorimichi.yorimichi.domain.mypage.controller;

import java.awt.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yorimichi.yorimichi.global.response.ApiResponse;


@RestController
@RequestMapping("/api/orderhistory")
public class OrderHistoryController {

	
	@GetMapping
	public void getOrderHistories() {
		System.out.println("졸려");
	}
}
