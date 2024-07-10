package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class HubEmployeeProfileEditDto {
   
	private String name;
	private String email;
	private String userName;
	private String phoneNumber;
	private String profilePic;
}
