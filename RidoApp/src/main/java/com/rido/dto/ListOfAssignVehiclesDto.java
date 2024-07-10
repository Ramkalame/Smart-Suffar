package com.rido.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ListOfAssignVehiclesDto {
	
	private String managerName ;
	private String location ;
	private List<Long> vehicleIds ;
	
	private List<Long> courierEbikeId ;
	private String hubName;
	private String state;
	private String city;
	

}
