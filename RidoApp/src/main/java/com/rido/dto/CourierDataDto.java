package com.rido.dto;

import com.rido.entity.enums.DriverAndVehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CourierDataDto {

	private String accountNo;
	private String ifsccode;
	private String accountHolderName;
	private String aadhaarNo;
	private DriverAndVehicleType vehicleType;
	private String vehicleNo;
	private String weight;
}
