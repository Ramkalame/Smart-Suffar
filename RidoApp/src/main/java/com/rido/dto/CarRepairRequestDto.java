package com.rido.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.rido.entity.ReturnCar.CarCondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CarRepairRequestDto {
	
	private Long driverid;
	private String driverName;
	private String vehicleName;
	private String vehicleNo;
	private LocalDateTime repairDateTime;
	private String message;
	private CarCondition carCondition;
}
