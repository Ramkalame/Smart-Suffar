package com.rido.dto;

import org.springframework.stereotype.Component;

@Component
public class DriverRequestDto {
	
	
	private String name;
	
	 private String phoneNo;
	 
	 private String address;
	 
	 private String licenceNo;
		
		private String InsuranceNo;
		
		private String panNo;
		
		
		private String vehicle;
		
		
		

		
		

		public DriverRequestDto() {
			super();
			// TODO Auto-generated constructor stub
		}


		public DriverRequestDto(String name, String phoneNo, String address, String licenceNo, String insuranceNo,
				String panNo, String vehicle) {
			super();
			this.name = name;
			this.phoneNo = phoneNo;
			this.address = address;
			this.licenceNo = licenceNo;
			InsuranceNo = insuranceNo;
			this.panNo = panNo;
			this.vehicle = vehicle;
			
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}


		public String getPhoneNo() {
			return phoneNo;
		}


		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}


		public String getAddress() {
			return address;
		}


		public void setAddress(String address) {
			this.address = address;
		}


		public String getLicenceNo() {
			return licenceNo;
		}


		public void setLicenceNo(String licenceNo) {
			this.licenceNo = licenceNo;
		}


		public String getInsuranceNo() {
			return InsuranceNo;
		}


		public void setInsuranceNo(String insuranceNo) {
			InsuranceNo = insuranceNo;
		}


		public String getPanNo() {
			return panNo;
		}


		public void setPanNo(String panNo) {
			this.panNo = panNo;
		}


		public String getVehicle() {
			return vehicle;
		}


		public void setVehicle(String vehicle) {
			this.vehicle = vehicle;
		}


		
		
		

}
