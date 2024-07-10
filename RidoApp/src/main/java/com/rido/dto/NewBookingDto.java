package com.rido.dto;

import java.time.LocalDateTime;

import com.rido.entity.UserLocation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBookingDto {

	private String customerName;
	private UserLocation tripFrom;
	private LocalDateTime startDateTime;
	private UserLocation tripTo;
	private LocalDateTime endDateTime;
//	private String Distance;
//	private String driverAssigned;
//	private String vehicleName;

}
