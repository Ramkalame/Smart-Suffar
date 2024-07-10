package com.rido.dto;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class HubEmployeeDto {
	
	private Long hubEmployeeId;

	private String name;
	private String email;
    private String phoneNo;
    private String profileImgLink;
	
	private String passbookImg;
	
	private String EmpSignature;
	
	private Long adharNo;
	
	private String panNo;
	
	private String address;

}
