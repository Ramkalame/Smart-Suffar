package com.rido.dto;

import org.springframework.stereotype.Component;

@Component
public class HistoryCancelledRequestDto {
	
	private String driverName;
	
	private String vehicleName;
	
	private String status;
	
	public HistoryCancelledRequestDto() {
		// TODO Auto-generated constructor stub
	}

	public HistoryCancelledRequestDto(String driverName, String vehicleName, String status) {
		super();
		this.driverName = driverName;
		this.vehicleName = vehicleName;
		this.status = status;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "HistoryCancelledRequestDto [driverName=" + driverName + ", vehicleName=" + vehicleName + ", status="
				+ status + "]";
	}

	
	

}
