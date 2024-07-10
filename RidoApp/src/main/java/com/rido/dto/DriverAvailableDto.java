package com.rido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverAvailableDto {

	private String driverName;
	
	private String presentLocation;
	
	private String assignDriverEndpoint;

}