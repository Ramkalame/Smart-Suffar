package com.rido.dto;

import com.rido.entity.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubDriverDataDto {

	private Long driverId;

	private String name;

	private String email;

	private String phoneNo;

	private Status status;

	private String totalEarning;

	private Long todayTotalRide;

}
