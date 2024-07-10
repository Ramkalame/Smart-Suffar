package com.rido.entity;

import java.math.BigDecimal;
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
public class CarRepairDetailCost {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long carRepairDetailCostId;

	private String invoice;

	private String VehicleName;

	private String VehicleNo;

	private LocalDateTime dateOfRepairing;

	private String issueDetail;

	private LocalDateTime dateOfCarRepaired;

	private BigDecimal totalCostOfRepairing;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;

}
