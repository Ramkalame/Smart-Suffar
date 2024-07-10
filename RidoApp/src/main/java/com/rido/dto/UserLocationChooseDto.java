package com.rido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserLocationChooseDto {

	private BigDecimal totalAmount;
	private Double distance;
	private Double startingPointLatitude; // Renamed for clarity
	private Double startingPointLongitude; // Renamed for clarity
	private Double endingPointLatitude; // Renamed for clarity
	private Double endingPointLongitude; // Renamed for clarity
	private LocalDateTime localDateTime;
	private String note;
	private String coupon;
	private String payment;

}
