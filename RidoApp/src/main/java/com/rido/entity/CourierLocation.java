package com.rido.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "courierLocation")
public class CourierLocation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long courierLocationId;
	
	private double  courierLatitude;
	
	private double courierLongitude;
	
//	@OneToOne
//	@JoinColumn(name = "courier_id")
//	private Courier courierId;

}
