package com.rido.payload.request;

import java.util.Set;

import com.rido.entity.enums.DriverAndVehicleType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class SignupRequest {
//    @NotBlank
	@Size(min = 3, max = 20)
	private String username;

//    @NotBlank
	@Size(max = 50)
	@Email
	private String email;

	private Set<String> role;

//    @NotBlank
	@Size(min = 6, max = 40)
	private String password;

	@Size(max = 10) // Assuming phone number is 10 digits
	private String phoneNumber;

	private String designation;

	private String hubName;

	private String managerName;

	private String city;

	private String state;

	private String name;

	private String adminId;

	private DriverAndVehicleType vehicleType;

	private DriverAndVehicleType driverType;

	public SignupRequest() {
	}

	public SignupRequest(@Size(min = 3, max = 20) String username, @Size(max = 50) @Email String email,
			Set<String> role, @Size(min = 6, max = 40) String password, @Size(max = 10) String phoneNumber,
			String designation, String hubName, String managerName, String city, String state, String name,
			String adminId, DriverAndVehicleType vehicleType, DriverAndVehicleType driverType) {
		super();
		this.username = username;
		this.email = email;
		this.role = role;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.designation = designation;
		this.hubName = hubName;
		this.managerName = managerName;
		this.city = city;
		this.state = state;
		this.name = name;
		this.adminId = adminId;
		this.vehicleType = vehicleType;
		this.driverType = driverType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<String> getRole() {
		return role;
	}

	public void setRole(Set<String> role) {
		this.role = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getHubName() {
		return hubName;
	}

	public void setHubName(String hubName) {
		this.hubName = hubName;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public DriverAndVehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(DriverAndVehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public DriverAndVehicleType getDriverType() {
		return driverType;
	}

	public void setDriverType(DriverAndVehicleType driverType) {
		this.driverType = driverType;
	}

	@Override
	public String toString() {
		return "SignupRequest [username=" + username + ", email=" + email + ", role=" + role + ", password=" + password
				+ ", phoneNumber=" + phoneNumber + ", designation=" + designation + ", hubName=" + hubName
				+ ", managerName=" + managerName + ", city=" + city + ", state=" + state + ", name=" + name
				+ ", adminId=" + adminId + ", vehicleType=" + vehicleType + ", driverType=" + driverType + "]";
	}

}
