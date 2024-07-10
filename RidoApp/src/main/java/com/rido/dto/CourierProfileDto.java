package com.rido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierProfileDto {

	private String courierId;
	private String fullName;
	private String aadharNo;
	private String DlNumber;
	private String panCardNo;
	private String phoneNo;
	private String address;
	private String accountNo;
	private String accountholderName;
	private String IfscCode;
	private String branchName;
	private String profileImg;
	private String email;

}