package com.rido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverNameAvailableDto {
	
	private String driverName;
	
	private Long driverId;

}
