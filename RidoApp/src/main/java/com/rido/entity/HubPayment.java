package com.rido.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
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
@Table(name = "hubPayment")
public class HubPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String managerName;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "hub_id")
	private Hub hub;
	private String phoneNo;
	private String hubName;

	private LocalDateTime date;
	private String status;
	private String orderId;

	private String receipt;

	private String orderStatus;

	private LocalDateTime localDatetime;

	private String amount;

	private String payementId;

//	@ManyToOne
//	@JoinColumn(name = "driver_id")
//	private Driver driver;
//
//	@ManyToOne
//	@JoinColumn(name = "employee_id")
//	private HubEmployee hubEmployee;

}