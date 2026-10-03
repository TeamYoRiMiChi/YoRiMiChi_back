package com.yorimichi.yorimichi.domain.mypage.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyPageProfileResponseDto {
	
	private String name;
	private String email;
	private LocalDateTime createdAt;
}
