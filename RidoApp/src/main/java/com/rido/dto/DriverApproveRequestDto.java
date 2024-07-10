package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class DriverApproveRequestDto {
	
	private String name;
	private String address;
	private String phoneNo;
	private String altPhoneNumber;
	private String email;
	
	private String Dpassbook;
	private String Dsignature;
	private String driverPanCard;
	private String DAddressproof;
	private String branchName;
	
	private String vehicleNo;
	
	

}
