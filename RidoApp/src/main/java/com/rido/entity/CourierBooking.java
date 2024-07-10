package com.rido.entity;

import java.math.BigDecimal;

import com.rido.entity.enums.CourierBookingStatus;
import com.rido.entity.enums.RideOrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courier_booking")
public class CourierBooking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long courierBookingId;

	@OneToOne(cascade = CascadeType.ALL)
	private SenderReceiverInfo senderReceiverInfo;

	@ManyToOne
	@JoinColumn(name = "timeDuration_id")
	private TimeDuration timeDuration;

	@ManyToOne
	@JoinColumn(name = "courierDriver_id")
	private Courier courierDriver;
	

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	// BOOKED, COMPLETE, IN_COMPLETE, CANCELLED, APPROVED
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(255)")
	private RideOrderStatus rideOrderStatus;

	private double gst;
	private double pricePerKm;
	private String senderName;
	private String receiverName;
	private String promoCode;
	private BigDecimal baseAmount;
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(255)")
	private CourierBookingStatus isConfirm;
	
}
