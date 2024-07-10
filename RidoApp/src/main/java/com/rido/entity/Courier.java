package com.rido.entity;

import java.math.BigDecimal;

import com.rido.entity.enums.ApproveStatus;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.Status;
import com.rido.entity.enums.VehicleAssignStatus;
import com.rido.entity.enums.VehicleCategory;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
//@Table(name = "courier")

@Table(name = "courier", uniqueConstraints = { @UniqueConstraint(columnNames = "vehicleNo"),
		@UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "phoneNo"), 
       
     }) 
public class Courier {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long courierId;

	private String ownerName;
	private String email;
	private String phoneNo;

	@NotBlank
	@Size(max = 120)
	private String password;

	private String vehicleNo;
	private BigDecimal price;
	private double pricePerKm;
	private double distanceFromSender;
	private double weight;
	private String address;
	private double courierDriverLatitude;
	private double courierDriverLongitude;

	// (Available, Engaged)
	@Enumerated(EnumType.STRING)
	private VehicleStatus vehicleStatus;

	@Enumerated(EnumType.STRING)
	private ApproveStatus approveStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hub_id")
	private Hub hub;

	// (ACE, GOLD, LOAD)
	@Enumerated(EnumType.STRING)
	private VehicleCategory vehicleCategory;

	@Enumerated(EnumType.STRING)
	private DriverAndVehicleType vehicleType;

	@Enumerated(EnumType.STRING)
	private VehicleAssignStatus vehicleAssignStatus;

	@OneToOne
	@JoinColumn(name = "courierdocument_id")
	private CourierDocument courierDocument;
	
	@Enumerated(EnumType.STRING)
	private Status status;

}
