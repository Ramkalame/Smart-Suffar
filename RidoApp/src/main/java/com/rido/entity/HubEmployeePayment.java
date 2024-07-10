package com.rido.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
public class HubEmployeePayment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long employeeOrderId;

	private String orderId;

	private String amount;

	private String receipt;

	private String orderStatus;
                
	private LocalDateTime localDatetime;

	@ManyToOne
	@JoinColumn(name = "hubEmployeeId")
	private HubEmployee hubEmployee;

	private String paymentId;

	@ManyToOne
	@JoinColumn(name = "hubId")
	private Hub hub;

}
