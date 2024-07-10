package com.rido.entity;

import java.time.LocalDateTime;

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
@Table(name = "user_location")
public class UserLocation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userLocationId;

	private String address;
	private double userLatitude;
	private double userLongitude;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User userId;

	private String pickupNotes;

	private LocalDateTime localDateTime;

//	private  String currentLocation;
//	
//	private String destinationLocation;

}
