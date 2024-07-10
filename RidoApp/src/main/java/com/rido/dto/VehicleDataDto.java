package com.rido.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.RentalPackageType;
import com.rido.entity.enums.VehicleStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class VehicleDataDto {
	private Long vehicleId;
	private Long adminId;
	private Long hubId;
	private Long driverId;
	private String vehicleName;
	private String hubname;
	private String hubmangerName;
	private String price;
//	private String battery;
//	private String chargingTime;
	private String seatingCapacity;
	//private String transmissionTypo;
	private String vehicleNo;
	private String insuranceNo;
	private BigDecimal pricePerKm;
	//private String vehicleImgLink;
	private String vehicleImgLink;
	private double distance;
	private RentalPackageType vehicleServiceType;
	private DriverAndVehicleType vehicleType;
	private VehicleStatus vehicleStatus;
	private String chassisNo;
    private String vehiclerange;
    private Date dateOfPurchase;
    private String invoice;
//	private Hub hub;
//	private Admin admin;

}
