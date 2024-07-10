package com.rido.entity;

import java.math.BigDecimal;
import java.util.Optional;

import com.rido.entity.enums.RideOrderStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking_table")
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookingId;

	@ManyToOne
	private UserLocation pickupLocation;

	@ManyToOne
	private UserLocation dropOffLocation;

	@ManyToOne
	private TimeDuration timeDuration;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;

	@ManyToOne
	@JoinColumn(name = "huhemp_id")
	private HubEmployee hubEmployee;

//	@Enumerated(EnumType.STRING)
//	private PackageType packageType;
	
	@Enumerated(EnumType.STRING)
	private RideOrderStatus rideOrderStatus;

	private String note;
	private double freeKms;
	private double extraKmCharge;
	private double rentalCharge;
	private double gst;
	private double refundableDeposit;
	
	private String promoCode;
	private BigDecimal totalAmount;
	
	
//	@ManyToOne
//    private PromoCode promoCode;
	
	 public Booking(User user, BigDecimal totalAmount) {
	        this.user = user;
	        this.totalAmount = totalAmount;
	    }



}
