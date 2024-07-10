package com.rido.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.rido.entity.Hub;
import com.rido.entity.enums.VehicleStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CourierEbikeDto {

	private Long CourierEbikeId;
	private String battery;
	private String chargingTime;
	private String insuranceNo;
	private String vehicleName;
	private String vehicleNo;
	private double weight;
	private double topSpeed;
	private String rc;
	private String ebikeImage;
	private BigDecimal pricePerKm;
	private VehicleStatus vehicleStatus;
	private String chassisNo;

    private String ebikerange;

    private Date dateOfPurchase;

    private String invoice;
	private Hub hub;

}
