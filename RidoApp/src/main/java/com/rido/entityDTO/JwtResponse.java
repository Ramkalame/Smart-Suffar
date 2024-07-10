package com.rido.entityDTO;

import java.util.List;

import com.rido.entity.enums.DriverAndVehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private Long id;
	private String name;
	private String username;
	private String phoneNo;
	private String email;
	private List<String> roles;
	private DriverAndVehicleType driverType;
	private String uniqeId;

	public JwtResponse(String accessToken, Long id, String username, String email, String phoneNo, List<String> roles) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.phoneNo = phoneNo;
		this.roles = roles;
	}

}