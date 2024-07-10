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
public class PaymentHistoryDto {
    
	
	private String name ;
	
	private String amount ;
	
	private LocalDateTime localdatetime;
	
	 private double pickupLongitude;
	    private double pickupLatitude;
	    private double dropOffLongitude;
	    private double dropOffLatitude;
	    
	    public PaymentHistoryDto(String name2, String amount2, LocalDateTime localDatetime2) {
			// TODO Auto-generated constructor stub
		}
}
