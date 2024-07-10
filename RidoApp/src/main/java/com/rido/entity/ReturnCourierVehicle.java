package com.rido.entity;



import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "return_courier_vehicle")
public class ReturnCourierVehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long courierReturnId;

	private LocalDateTime carAssignTime;
	private LocalDateTime carReturnTime;

	private String reason;

	@ManyToOne
	@JoinColumn(name = "courier_id")
	private Courier courier;

	@ManyToOne
	@JoinColumn(name = "courierEbike_id")
	private CourierEbike courierEbike;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;
	
	@Enumerated(EnumType.STRING)
	private BikeCondition bikeCondition;

	public enum BikeCondition {
		GOOD, NORMAL, WORST

	}

}
