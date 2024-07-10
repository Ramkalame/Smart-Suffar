package com.rido.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFareDTO {

	private double distance;
	private String vehicleType;
	private BigDecimal totalPrice;

}
