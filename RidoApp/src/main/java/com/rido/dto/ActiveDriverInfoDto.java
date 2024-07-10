package com.rido.dto;

import java.time.LocalDateTime;

import com.rido.entity.ReturnCar.CarCondition;
import com.rido.entity.enums.RideOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveDriverInfoDto {
	
	
	
	private String driverName;
   // private RideOrderStatus rideOrderStatus;
	private String rideStatus;
    private double pickupLatitude;
    private double pickupLongitude;
    private double DropUpLatitude;
    private double DropUpLongitude;

}
