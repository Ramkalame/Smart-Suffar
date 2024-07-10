package com.rido.dto;

import org.springframework.stereotype.Component;

import com.rido.entity.User;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Component
public class HistoryUpcomingRequestDto {

	private String driverName;
	
	private String vehicleName;
	
	private String status;

	@ManyToOne
    @JoinColumn(name = "user_id")
	private User userId;
	
	public HistoryUpcomingRequestDto() {
		// TODO Auto-generated constructor stub
	}

	public HistoryUpcomingRequestDto(String driverName, String vehicleName, String status, User userId) {
		super();
		this.driverName = driverName;
		this.vehicleName = vehicleName;
		this.status = status;
		this.userId = userId;
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

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "HistoryUpcomingRequestDto [driverName=" + driverName + ", vehicleName=" + vehicleName + ", status="
				+ status + ", userId=" + userId + "]";
	}


}
