package com.rido.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.rido.entity.enums.CourierEbikeDriverPaymentStatus;

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
public class CourierEbikedriverPayment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long CourierEbikedriverPaymentId;
	

	@Temporal(TemporalType.DATE)
	private LocalDate date;
	
	private String amount;
	
	@ManyToOne
	@JoinColumn(name = "courier_id")
	private Courier courier;
	
	@Enumerated(EnumType.STRING)
    private CourierEbikeDriverPaymentStatus paymentStatus;
	
	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;
	
	


	
}
