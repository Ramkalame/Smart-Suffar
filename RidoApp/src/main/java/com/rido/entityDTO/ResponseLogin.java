package com.rido.entityDTO;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.rido.entity.enums.ApproveStatus;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ResponseLogin {


	private Long userId;

	private String phoneNo;

	private String email;

	private String name;

	private String userName;

	private String message;
	
	private String location;

	private Long hubId;

	private HttpStatus statusCode;

	private ApproveStatus approveStatus;

	private DriverAndVehicleType driverType;

	private Status status;

	public ResponseLogin(String emailId, String phoneNumber, String string) {
		// TODO Auto-generated constructor stub
	}

	public ResponseLogin(Long userId2, String string) {
		// TODO Auto-generated constructor stub
	}
}