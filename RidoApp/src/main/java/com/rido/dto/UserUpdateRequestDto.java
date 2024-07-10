package com.rido.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserUpdateRequestDto {
	private String name;
	//private String phoneNo;
	private String alternativeNo;
	private String gender;
	private Date dob;
	//private String email;
	
}
