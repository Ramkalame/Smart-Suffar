package com.rido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.rido.entity.enums.EBikeRentalType;
import com.rido.entity.enums.VehicleStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class EBikeDto {
	private String eBikeName;
	private BigDecimal pricePerKm;
	private double bikeRange;
	private String battery;
	private String chargingTime;
	private String eBikeNo;
	private BigDecimal pricePerHours;
    private BigDecimal pricePerDays;
    private BigDecimal depositAmount;
    private EBikeRentalType rentalType;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    
    private VehicleStatus vehicleStatus;
    private String weight;
    private String topSpeed;
    private String rangePerCharge;
    private String city;
   
}
