package com.rido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.rido.entity.UserLocation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalCompleteBookingDto {
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	private Long Id;
    private String customerName;
    private UserLocation tripFrom;
    private LocalDateTime startDateTime;
    private UserLocation tripTo;
    private LocalDateTime endDateTime;
    private String driverAssigned;
    private String vehicleName;
    private BigDecimal paymentMethod;
//	private String Distance;
    
}
