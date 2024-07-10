package com.rido.dto;

import java.time.LocalDateTime;

import com.rido.entity.ReturnCar.CarCondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRepairDto {
	private String driverName;
	private Long driverId;
	private String vehicleNo;
	private String vehicleName;
	// private String changeReason;
	private LocalDateTime returnTime;
	private String message;
	private CarCondition carCondition;

}
