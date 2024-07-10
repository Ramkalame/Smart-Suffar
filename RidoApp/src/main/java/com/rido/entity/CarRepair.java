package com.rido.entity;

import java.time.LocalDateTime;

import com.rido.entity.enums.CarRepairStatus;
import com.rido.entity.enums.MaintenanceApprovalStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class CarRepair {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long carRepairId;

	private String message;

	private LocalDateTime returnTime;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

	private String VehicleName;
	private String VehicleNo;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;

//	private BigDecimal approximateAmount;

	
	private String damageCarImg;
	
	private String damageCarVideo;
	
	private String hubMessage;
	
	private String carRepairUniqueKey;
	
	@Enumerated(EnumType.STRING)
    @Column(length = 20) 
    private CarRepairStatus carRepairStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 20) 
    private MaintenanceApprovalStatus maintenanceApprovalStatus;
    

}
