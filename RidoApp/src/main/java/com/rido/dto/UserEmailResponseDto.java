package com.rido.dto;

import org.springframework.stereotype.Component;

@Component
public class UserEmailResponseDto {

	
	private String email;
	
   private String emailOtp;
	
	private  Boolean emailVerified;
	
	private     String emailAppliStatus;

	public UserEmailResponseDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserEmailResponseDto(String email, String emailOtp, Boolean emailVerified, String emailAppliStatus) {
		super();
		this.email = email;
		this.emailOtp = emailOtp;
		this.emailVerified = emailVerified;
		this.emailAppliStatus = emailAppliStatus;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailOtp() {
		return emailOtp;
	}

	public void setEmailOtp(String emailOtp) {
		this.emailOtp = emailOtp;
	}

	public Boolean getEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getEmailAppliStatus() {
		return emailAppliStatus;
	}

	public void setEmailAppliStatus(String emailAppliStatus) {
		this.emailAppliStatus = emailAppliStatus;
	}

	
	
	
}
