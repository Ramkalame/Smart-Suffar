package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class AdminDataDto {
    
	private Long adminId;
	private String name;

	private String password;

	private String phoneNo;

	private String email;

	private String address;

}
