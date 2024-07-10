package com.rido.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.rido.entity.enums.DriverAndVehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ScheduleRideRequest {

	
	  private Long userId;
	    private double pickupLat;
	    private double pickupLon;
	    private double dropoffLat;
	    private double dropoffLon;
	    private LocalDateTime startDateTime;
	    private DriverAndVehicleType vehicleType;
}
