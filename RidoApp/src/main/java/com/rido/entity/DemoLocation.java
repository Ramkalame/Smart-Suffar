package com.rido.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Entity
public class DemoLocation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long locationId;

	private String address;

	private double latitude;

	private double longitude;

}
