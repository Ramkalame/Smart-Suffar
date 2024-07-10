package com.rido.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.RentalPackageType;
import com.rido.entity.enums.VehicleStatus;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "vehicle", uniqueConstraints = {  @UniqueConstraint(columnNames = "vehicleNo"), 
	    @UniqueConstraint(columnNames = "insuranceNo"),@UniqueConstraint(columnNames = "chassisNo")
	  })
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long vehicleId;
	
	@Column(unique = true, nullable = false)
	private String vehicleNo;
	private String price;
	private double distance;
	private BigDecimal pricePerKm;

	private String vehicleName;

//	private String battery;
//	private String chargingTime;
	private String seatingCapacity;
	//private String transmissionTypo;
    
	@Column(unique = true, nullable = false)
	private String insuranceNo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hub_id")
	private Hub hub;

	@Column(name = "vehicle_img_link", length = 10000)
	private String vehicleImgLink;

	// (Available, Engaged)
	@Enumerated(EnumType.STRING)
	private VehicleStatus vehicleStatus;

	@Enumerated(EnumType.STRING)
	private RentalPackageType vehicleServiceType;

	@Enumerated(EnumType.STRING)
	private DriverAndVehicleType vehicleType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	private Admin admin;
	
	private LocalDateTime assignHubDate;
	
	@Column(unique = true, nullable = false)
    private String chassisNo;

    private String Vehiclerange;

    private Date dateOfPurchase;

    private String invoice;

	
	

	

}