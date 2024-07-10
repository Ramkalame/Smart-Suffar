package com.rido.entity;

import java.util.Date;

import com.rido.entity.enums.AllPaymentStatus;

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
public class HubEmployeePaymentDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long employeeOrderId;

	@ManyToOne
	@JoinColumn(name = "hubEmployeeId")
	private HubEmployee hubEmployee;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;

	private String amount;

	@Temporal(TemporalType.DATE)
	private Date date;

	@Enumerated(EnumType.STRING)
	private AllPaymentStatus allPaymentStatus;

}
