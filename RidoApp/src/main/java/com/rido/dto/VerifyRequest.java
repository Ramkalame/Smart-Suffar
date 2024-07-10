package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class VerifyRequest {

	private String phoneNo;
	private String smsOtp;

	private String email;
	private String emailOtp;
}