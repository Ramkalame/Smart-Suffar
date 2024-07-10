package com.rido.dto;

import com.rido.entity.UserLocation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailsDto {

	private String customerName;
//	private String Distance;
//  private LocalDateTime timeOfTravel;
	private UserLocation locality;
	private String sourceOfDestination;
//	private String paymentMethod;

}
