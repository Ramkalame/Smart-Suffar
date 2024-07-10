package com.rido.dto;

import org.springframework.stereotype.Component;

import com.rido.entity.enums.DriverAndVehicleType;

import lombok.Data;

@Data
@Component
public class SenderReceiverInfoDto {
	private Long id;
	private String senderName;
	private Double senderLatitude;
	private Double senderLongitude;
	private String senderLocation;
	private String senderAddress;
	private String senderPhoneNumber;
	private String receiverName;
	private Double receiverLatitude;
	private Double receiverLongitude;
	private String receiverLocation;
	private String receiverAddress;
	private String receiverPhoneNumber;

	private DriverAndVehicleType vehicleType;
	private double totalDistance;
	private String expectedTime;
	private Long userId; // Refers to the User ID
	private Long courierBookingId;// Refers to the booking id
	private Long courierId; // refers to the driver id
}
