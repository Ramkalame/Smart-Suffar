package com.rido.dto;

import org.springframework.stereotype.Component;

import com.rido.entity.UserLocation;

import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CourierTaskListDto {
	
	private Long userId;
	private Long courierBookingId;

	private String curierDriverLocation;

	private String reciverName; // courier reciver name
	private String reciverLocation;
	private String reciverPhoneNumber;
	
	private String senderName; //user
	private String senderLocation;
    private String senderPhoneNumber;
    
    private double totalDistance;
    private String expectedTime;
}
