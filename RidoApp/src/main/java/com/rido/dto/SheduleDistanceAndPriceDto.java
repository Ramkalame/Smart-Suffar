package com.rido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SheduleDistanceAndPriceDto {
	
	    private Long bookingId;
	    private double distance;
	    private BigDecimal totalAmount;
	    private LocalDateTime startDateTime;
	   



}
