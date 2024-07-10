package com.rido.entity;

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
public class RentalUserLocation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long rentalUserLocationId;

	private String pickupAddress;

	private double userPickupLatitude;

	private double userPickupLongitude;

	private String dropAddress;

	private double userDropLatitude;

	private double userDropLongitude;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

}
