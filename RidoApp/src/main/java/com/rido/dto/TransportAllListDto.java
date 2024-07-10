package com.rido.dto;

import java.time.LocalDateTime;

import com.rido.entity.TimeDuration;
import com.rido.entity.enums.CourierBookingStatus;
import com.rido.entity.enums.DriverAndVehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportAllListDto {
	private String DriverName;
	 private String senderName;
	  private String senderAddress;
	  private String receiverAddress;
	  private String senderPhoneNumber;
	  private double totalDistance;
	    private String expectedTime;
	  private DriverAndVehicleType vehicleType;
	  private LocalDateTime startDateTime;
	  private CourierBookingStatus isConfirm;
	
	

}
