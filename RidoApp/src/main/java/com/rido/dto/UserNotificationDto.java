package com.rido.dto;

import lombok.Data;

@Data
public class UserNotificationDto {
	
	private Long driverId;
	private Long userId;
	private String name;
	private String phonenumber;
	private String profileImg;
	private Long bookingId;
		

}
