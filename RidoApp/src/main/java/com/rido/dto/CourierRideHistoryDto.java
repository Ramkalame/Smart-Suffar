package com.rido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.rido.entity.enums.RideOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierRideHistoryDto {
    
	private Long id;
	private LocalDateTime startTime ;
	private LocalDateTime endTime ;
	private String senderAddress ;
	private String receiveraddress ;
	private RideOrderStatus status ;
	private BigDecimal price ;
}
