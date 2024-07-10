package com.rido.dto;

import java.math.BigDecimal;

import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.VehicleCategory;
import com.rido.entity.enums.VehicleStatus;

import lombok.Data;

@Data
public class DriverNotificationDto {
	
	private Long bookingId;
	private Long driverId;
	private Long userId;
	private String Name;
	private String phoneNumber;
	private String profileImg;
	
	
	 private Double userPickupLatitude;
		
		private Double userPickupLogitude;
		
		private Double userDropLatitude;
		
		private Double userDropLogitude;
		
		
		
		private String message;
		private String status;
	

}
