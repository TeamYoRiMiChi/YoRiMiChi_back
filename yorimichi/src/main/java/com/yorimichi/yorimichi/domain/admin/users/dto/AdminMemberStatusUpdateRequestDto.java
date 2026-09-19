package com.yorimichi.yorimichi.domain.admin.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminMemberStatusUpdateRequestDto {
	
	@NotBlank
	private String status;
}
