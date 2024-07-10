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
public class AssignCar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long assignCarId;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driverId;
	@ManyToOne
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicleId;
	private LocalDateTime openingTime;
	private String locality;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hubId;

}
