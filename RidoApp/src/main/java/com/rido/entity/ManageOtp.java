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
@Table(name = "manage_otp")
public class ManageOtp {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long manageOtpId;

	private String registerOtp;// registerOtp

	private String forgetOtp;

	private String updateOtp;

	@OneToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne
	@JoinColumn(name = "admin_id")
	private Admin admin;

	@OneToOne
	@JoinColumn(name = "hubId")
	private Hub hub;

	@OneToOne
	@JoinColumn(name = "hubEmployeeId")
	private HubEmployee hubEmployee;

	@OneToOne
	@JoinColumn(name = "courierId")
	private Courier courier;

}
