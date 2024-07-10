package com.rido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class BookingResponseDto {

	
	
	private Double pickupLocation;


	private Double dropOffLocation;

	
	private LocalDateTime startTimeDuration;
	
	private BigDecimal amount;
	
	
	
	
	
	private Long bookingId;
	
    private double totalDistance;
    
    private BigDecimal totalAmount;
    
     private LocalDateTime dateTime;
    
    public BookingResponseDto(Long bookingId, double totalDistance, BigDecimal totalAmount, LocalDateTime dateTime) {
        this.bookingId = bookingId;
        this.totalDistance = totalDistance;
        this.totalAmount = totalAmount;
        this.dateTime = dateTime;
    }

}
