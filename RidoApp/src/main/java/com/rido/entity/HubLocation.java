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
@Table(name="hub_location")
public class HubLocation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long hubLocationId;
	
	private double hubLatitude;
	
	private double hubLongitude;
	
	@OneToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;
	

	

}
