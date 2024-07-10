package com.rido.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DriverPaymentDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long driverPaymentDetailId;
	@Temporal(TemporalType.DATE)
	private LocalDate date;

	private String amount;

	@Enumerated(EnumType.STRING)
	private Status status;

	@ManyToOne
	private TimeDuration timeDuration;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;

	public enum Status {
		PAID, PENDING
	}

}
