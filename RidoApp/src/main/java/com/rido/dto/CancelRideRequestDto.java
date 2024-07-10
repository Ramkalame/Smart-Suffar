package com.rido.dto;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class CancelRideRequestDto {

	private Long bookingId;
	 private Long driverId;
	 private Long userId;
     private List<String> reason;
	public Long getDriverId() {
		return driverId;
	}
	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public List<String> getReason() {
		return reason;
	}
	public void setReason(List<String> reason) {
		this.reason = reason;
	}
	public Long getBookingId() {
		return bookingId;
	}
	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}
	    
	    

}
