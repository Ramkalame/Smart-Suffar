package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class DriverDataDto {

	private Long driverId;
	private String name;
	private String phoneNo;
	private String address;
   private String altPhoneNumber;
	private String panNo;
	private String email;
	

	private String profileImgLink;
	
	private String dlNumber;
	
	private Long adharNo;
	
	private Long accountNo;

	private String ifsc;

	private String accountHolderName;

	private String branchName;
}
