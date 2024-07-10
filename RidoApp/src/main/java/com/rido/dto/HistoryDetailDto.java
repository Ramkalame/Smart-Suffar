package com.rido.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.rido.entity.enums.RideOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class HistoryDetailDto {

	
	private String driverName;
    private String vehicleName;
    private LocalDateTime startTime;
    private RideOrderStatus rideOrderStatus;
    
    public HistoryDetailDto(String driverName2, String vehicleName2, Object object, String vehicleName3) {
		// TODO Auto-generated constructor stub
	}


    
     
}
