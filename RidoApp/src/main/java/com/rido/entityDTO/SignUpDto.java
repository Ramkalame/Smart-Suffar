package com.rido.entityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {
//	private String firstName;
//    private String lastName;
	private String name;
	private String username;
	private String email;
	private String password;

	private String licenceNo;

	private String InsuranceNo;

	private String panNo;

	private String vehicle;

	private String phoneNo;


}
