package com.rido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class HubDriverDetailsDto {
	
	

	private String driverSignature;
	
	private String address;
	
	private Long adharNo;
	private String Dpassbook;//image
	private String panNo;
	
	

}
