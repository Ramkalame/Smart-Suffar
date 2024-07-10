package com.rido.entity;

import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.Status;
import com.rido.entity.enums.VehicleAssignStatus;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "driver", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "phoneNo"), 
        @UniqueConstraint(columnNames = "panNo"),  @UniqueConstraint(columnNames = "dlNumber"),
        @UniqueConstraint(columnNames = "adharNo")
     }) 
public class Driver {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long driverId;

	private String name;
	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@Size(max = 15)
	@Column(name = "phoneNo")
	private String phoneNo;

	@NotBlank
	@Size(max = 120)
	private String password;

	private String gender;

	private String address;

	//private String image;

	private String altPhoneNumber;

	private String licenceNo;

	private String InsuranceNo;

	private String panNo;

	private String driverUniqeId;

	private String profileImgLink;

	// AVAILABLE, ONGOING
	@Enumerated(EnumType.STRING)
	private Status status;

	// CHECKIN, CHECKOUT
	@Enumerated(EnumType.STRING)
	private VehicleAssignStatus vehicleAssignStatus;

	@Enumerated(EnumType.STRING)
	private DriverAndVehicleType driverType;

	private String userPhoneNoOtp;

	private String dlNumber;

	private Long adharNo;

	@OneToOne(cascade = CascadeType.ALL)
	private Vehicle vehicle;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;

	private double driverLongitude;
	private double driverLatitude;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	private Admin admin;

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "driver_roles", joinColumns = @JoinColumn(name = "driver_driverId"), inverseJoinColumns = @JoinColumn(name = "role_id"))
//	private Set<Role> roles = new HashSet<>();

	public Driver(String name, String username, String email, String phoneNo, String encode) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.phoneNo = phoneNo;
		this.password = password;
	}

	public Driver(String username2, String email2, String phoneNo, String encode) {
		this.name = name;
		this.username = username2;
		this.email = email2;
		this.phoneNo = phoneNo;
		this.password = encode;
	}

}
