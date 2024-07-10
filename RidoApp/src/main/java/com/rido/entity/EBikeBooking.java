package com.rido.entity;

import java.math.BigDecimal;

import com.rido.entity.enums.EBikeRentalType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EBikeBooking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ebikeBookingId;
	private String city;
	private BigDecimal totalAmount;
	@Enumerated(EnumType.STRING)
	private EBikeRentalType rentalType;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private TimeDuration timeDuration;

	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;
	
	@ManyToOne
    @JoinColumn(name = "ebike_id") 
    private EBike ebike; 
	

	
	private String userImage;
	private String drivingLicence;
	private String aadharCard;
	private String vehicleFrontViewImage;
	private String vehicleSideViewImage;
	private String kmImage;



}
