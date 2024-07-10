package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component

public class AdminProfileEditDto {
   
	private Long adminId;
	private String profileImgLink;

	private String name;

	private String email;

	private String phoneNo;

	private String address;

}