package com.yorimichi.yorimichi.domain.admin.users.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminMemberResponseDto {
	private Long memberId;
	private String email;
	private String name;
	private String phone;
	private String role;
	private String status;
	private LocalDateTime withdrawnAt; 
}
