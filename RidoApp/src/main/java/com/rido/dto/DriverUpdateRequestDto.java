package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DriverUpdateRequestDto {

	private Long id;
	private String name;
	private String phoneNo;
	private String address;
	private String email;
	private String altPhoneNumber;
	private String driverImage ;
	
	

}
