package com.rido.dto;

import org.springframework.stereotype.Component;

import com.rido.entity.enums.RideOrderStatus;

@Component
public class TotalBookingDto {
	private String driverName;
	private String vehicleNo;
	private RideOrderStatus rideOrderStatus;
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public RideOrderStatus getRideOrderStatus() {
		return rideOrderStatus;
	}
	public void setRideOrderStatus(RideOrderStatus rideOrderStatus) {
		this.rideOrderStatus = rideOrderStatus;
	}
	
	
	
	

}
