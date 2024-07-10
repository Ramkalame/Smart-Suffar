package com.rido.dto;

import org.springframework.stereotype.Component;

@Component
public class UserEmailRequestDto {

    private String emailOtp;
	
	private  Boolean emailVerified;
	
	private     String emailAppliStatus;
}
