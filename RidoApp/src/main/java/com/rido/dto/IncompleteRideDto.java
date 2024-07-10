package com.rido.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class IncompleteRideDto {
	 private String username;
	    private double tripFromBookingLongitude;
	    private double tripFromBookingLatitude;
	    private LocalDateTime bookingTime;
	    private double tripToReachedLatitude;
	    private double tripToReachedLongitude;
	    private LocalDateTime reachedTime;
}
