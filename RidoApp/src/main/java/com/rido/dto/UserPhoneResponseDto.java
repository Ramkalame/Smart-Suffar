package com.rido.dto;

import org.springframework.stereotype.Component;

@Component
public class UserPhoneResponseDto {

	private String phoneNo;
	

	
	private String phoneNoOtp;

	private Boolean phoneNoVerified;

	private String phoneNoAppliStatus;


	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getPhoneNoOtp() {
		return phoneNoOtp;
	}

	public void setPhoneNoOtp(String phoneNoOtp) {
		this.phoneNoOtp = phoneNoOtp;
	}

	public Boolean getPhoneNoVerified() {
		return phoneNoVerified;
	}

	public void setPhoneNoVerified(Boolean phoneNoVerified) {
		this.phoneNoVerified = phoneNoVerified;
	}

	public String getPhoneNoAppliStatus() {
		return phoneNoAppliStatus;
	}

	public void setPhoneNoAppliStatus(String phoneNoAppliStatus) {
		this.phoneNoAppliStatus = phoneNoAppliStatus;
	}
	
	
}
