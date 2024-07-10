package com.rido.dto;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rido.entity.DriverPaymentDetail.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class DriverPaymentDetailDto {

	private String DriverName;
	private String Address;
	private LocalDate date;
	private Long Driverid;
	private String TotalAmount;
//	@JsonProperty("PerRideAmount")
//	private double perRideAmount;
//
//	@JsonProperty("thresholdRides")
//	private double thresholdRides;
//
//	@JsonProperty("incentiveOnRide")
//	private double incentiveOnRide;

	private String emailAddress;
	private String phoneNo;
	private String profileImgLink;
	private Status status;

}
