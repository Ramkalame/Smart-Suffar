package com.rido.entity;

import java.math.BigDecimal;

import com.rido.entity.enums.CourierBookingStatus;
import com.rido.entity.enums.RentalPackageType;
import com.rido.entity.enums.RideOrderStatus;

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
public class RentalBooking {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long rentalBookingId;

	@ManyToOne(cascade = CascadeType.ALL)
	private RentalUserLocation travelLocation;

	@ManyToOne(cascade = CascadeType.ALL)
	private TimeDuration timeDuration;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;

	@Enumerated(EnumType.STRING)
	private RentalPackageType rentalPackageType;

	@Enumerated(EnumType.STRING)
	private RideOrderStatus rideOrderStatus;

	private String promoCode;

	private BigDecimal amount;

	private BigDecimal totalAmount;

	private int hours;

	private int distance;

	private int extraHours;

	private int extraDistance;

	private BigDecimal extraAmount;

	private String gst;

	private String note;

	@Enumerated(EnumType.STRING)
//	@Column(columnDefinition = "varchar(255)")
	private CourierBookingStatus isConfirm;

}
