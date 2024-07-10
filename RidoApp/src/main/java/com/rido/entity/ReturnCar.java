package com.rido.entity;

import java.time.LocalDateTime;

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
@Table(name = "ReturnCar")
public class ReturnCar {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long ReturncarChangeId;

	private LocalDateTime assignTime;

	private LocalDateTime returnTime;

	@Enumerated(EnumType.STRING)
	private CarCondition carCondition;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@ManyToOne
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;

	private String message;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;

	private String VehicleName;

	private String VehicleNo;

	public enum CarCondition {
		GOOD, NORMAL, WORST

	}

}
