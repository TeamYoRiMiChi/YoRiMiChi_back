package com.yorimichi.yorimichi.domain.mypage.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryTrackingResponseDto {
	
	private Long orderId;
	private String orderNumber;
	
	private String firstProductName;
	private Integer itemCount;
	
	private String shippingStatus;
	private String carrier;
	private String trackingNumber;
	
	private LocalDateTime createdAt;
	private LocalDateTime shippedAt;
	private LocalDateTime deliveredAt;
	private LocalDateTime cancelledAt;
}
