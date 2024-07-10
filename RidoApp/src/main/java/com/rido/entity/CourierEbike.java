package com.rido.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.rido.entity.enums.VehicleStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
public class CourierEbike {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long courierEbikeId;
//	private String battery;
//	private String chargingTime;
	private String insuranceNo;
	private String vehicleName;
	private String vehicleNo;
	private double weight;
	private double topSpeed;
	private String ebikeImage;
	private String rc;
	private BigDecimal pricePerKm;
	@Column(unique = true, nullable = false)
    private String chassisNo;

    private String ebikerange;

    private Date dateOfPurchase;

    private String invoice;


	@Enumerated(EnumType.STRING)
	private VehicleStatus vehicleStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hub_id")
	private Hub hub;

	@ManyToOne(cascade = CascadeType.ALL)
	private Courier courier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	private Admin admin;
	
	private LocalDateTime assignHubDate;

}
