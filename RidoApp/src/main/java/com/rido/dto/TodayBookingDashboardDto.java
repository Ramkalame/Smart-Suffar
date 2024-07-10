package com.rido.dto;

import org.springframework.stereotype.Component;

import com.rido.entity.TimeDuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class TodayBookingDashboardDto {

	private TimeDuration time;
	private LocationDto pickupLocation;
	private LocationDto dropOffLocation;
}
