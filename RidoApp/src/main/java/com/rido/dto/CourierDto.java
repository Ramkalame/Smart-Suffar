package com.rido.dto;

import java.math.BigDecimal;

import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.VehicleCategory;
import com.rido.entity.enums.VehicleStatus;

import lombok.Data;

@Data
public class CourierDto {
	private Long courierId;
	private DriverAndVehicleType vehicleType;
	private String vehicleNo;
	private BigDecimal price;
	private double distanceFromSender;
	private double weight;
	private String vehicleImage;
	// (Available, Engaged)
	private VehicleStatus vehicleStatus;
	private Long hubId;
	private SenderReceiverInfoDto senderReceiverInfoDto;
	private String timeAwayFromSender;
	private VehicleCategory vehicleCategory;
}