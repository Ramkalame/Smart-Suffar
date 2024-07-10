package com.rido.dto;


import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class DriverRunningVehicleResponseDto {

	private Long driverId;
	private Long vehicleId;
	private LocalDateTime openingTime;
	private String locality;
	private String driverName;
	private String vehicleName;
	private String vehicleNo;
	private String price;
	private Double distance;
	private String vehicleType;
	private String location;
	private String  seatingCapacity;

}
