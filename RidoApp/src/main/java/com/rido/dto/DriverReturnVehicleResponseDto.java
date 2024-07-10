package com.rido.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.rido.entity.ReturnCar.CarCondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DriverReturnVehicleResponseDto {

	private String changeReason;

	private LocalDateTime returnTime;

	private CarCondition carCondition; // Renamed from 'condition'

	private String carName;

	private String carNumber;

	private String driverName;

	private String driverId;

	private String message;
}
