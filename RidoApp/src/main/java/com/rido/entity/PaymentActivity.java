package com.rido.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
@Table(name = "payment")
public class PaymentActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long myOrderId;

	private String orderId;
	private String amount;
	private String receipt;

	// status should be failed, attempted and paid
	// orderStatus = PAID (payment successfully done)
	// orderStatus = ATTEMPTED (payment failed)
	// orderStatus = CREATED (payment is initialized)
	private String orderStatus;

	private LocalDateTime localDatetime;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private String payementId;

	@ManyToOne
	@JoinColumn(name = "rentalBooking_id")
	private RentalBooking rentalBooking;

	@ManyToOne
	@JoinColumn(name = "booking_id")
	private Booking booking;
	
	
	
}