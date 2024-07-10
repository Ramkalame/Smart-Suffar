package com.rido.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="driver_location")
public class DriverLocation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long driverLocationId;
	
	private String driverLangitude;
	
	private String driverLongitude;
	
	@OneToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;
	
	
	

}
