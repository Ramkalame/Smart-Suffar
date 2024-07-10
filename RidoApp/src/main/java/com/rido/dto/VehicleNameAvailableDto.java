package com.rido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleNameAvailableDto {

	private Long vehicleId;

	private String vehicleName;

}
