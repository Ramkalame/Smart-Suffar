package com.rido.dto;

import java.time.LocalDateTime;

import com.rido.entity.ReturnCar.CarCondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarMaintenanceDto {
    
    private String reason;
    private LocalDateTime returnTime;
    private String vehicleName;
    private String vehicleNo;
    private String carCondition; 

}
