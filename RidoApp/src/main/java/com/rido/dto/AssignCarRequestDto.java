package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class AssignCarRequestDto {

	private Long driverId;
	private Long vehicleId;
//	private Long hubId;
//	private String driverName;
//	private String vehicleName;
//	private LocalDateTime openingTime;
//	private String locality;

}
