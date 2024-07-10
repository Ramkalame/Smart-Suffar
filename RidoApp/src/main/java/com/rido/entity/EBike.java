package com.rido.entity;

import java.math.BigDecimal;

import com.rido.entity.enums.EbikeUsageType;
import com.rido.entity.enums.VehicleStatus;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EBike {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eBikeId;
	private String eBikeName;
	private BigDecimal pricePerKm;
	private double bikeRange;
	private String battery;
	private String chargingTime;
	private String eBikeImg;
	private String eBikeNo;
	private String city;
	private BigDecimal pricePerHours;
    private BigDecimal pricePerDays;
    private BigDecimal depositAmount;
    private String weight;
    private String topSpeed;
    private String rangePerCharge;
    
	@Enumerated(EnumType.STRING)
    private EbikeUsageType usageType;
    
	@Enumerated(EnumType.STRING)
    private VehicleStatus vehicleStatus;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hub_id")
	private Hub hub;

}
