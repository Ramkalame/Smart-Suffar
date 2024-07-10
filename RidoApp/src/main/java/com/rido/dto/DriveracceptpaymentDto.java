package com.rido.dto;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DriveracceptpaymentDto {

	private String driverId;
	private String vehicleNo;
	private String totalEarning;

	private String todayTotalRide;
	private List<PaymentHistoryDto> customers;

	public DriveracceptpaymentDto() {
		// TODO Auto-generated constructor stub
	}

	public DriveracceptpaymentDto(String driverId, String vehicleNo, String totalEarning, String todayTotalRide,
			List<PaymentHistoryDto> customers) {
		super();
		this.driverId = driverId;
		this.vehicleNo = vehicleNo;
		this.totalEarning = totalEarning;
		this.todayTotalRide = todayTotalRide;
		this.customers = customers;
	}

	public String getTodayTotalRide() {
		return todayTotalRide;
	}

	public void setTodayTotalRide(String todayTotalRide) {
		this.todayTotalRide = todayTotalRide;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public String getTotalEarning() {
		return totalEarning;
	}

	public void setTotalEarning(String totalEarning) {
		this.totalEarning = totalEarning;
	}

	public List<PaymentHistoryDto> getCustomers() {
		return customers;
	}

	public void setCustomers(List<PaymentHistoryDto> customers) {
		this.customers = customers;
	}

	@Override
	public String toString() {
		return "DriveracceptpaymentDto [driverId=" + driverId + ", vehicleNo=" + vehicleNo + ", totalEarning="
				+ totalEarning + ", todayTotalRide=" + todayTotalRide + ", customers=" + customers + "]";
	}

}