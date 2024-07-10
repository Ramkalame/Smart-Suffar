package com.rido.dto;

public class DriverInfoDTO {
	private String name;
    private String phoneNo;
    private String vehicleNo;
    private String price;
    private Double distance;
    private String vehicleType;

           public DriverInfoDTO() {
			// TODO Auto-generated constructor stub
		}

		public DriverInfoDTO(String name, String phoneNo, String vehicleNo, String price, Double distance,
				String vehicleType) {
			super();
			this.name = name;
			this.phoneNo = phoneNo;
			this.vehicleNo = vehicleNo;
			this.price = price;
			this.distance = distance;
			this.vehicleType = vehicleType;
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

		public String getVehicleNo() {
			return vehicleNo;
		}

		public void setVehicleNo(String vehicleNo) {
			this.vehicleNo = vehicleNo;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public Double getDistance() {
			return distance;
		}

		public void setDistance(Double distance) {
			this.distance = distance;
		}

		public String getVehicleType() {
			return vehicleType;
		}

		public void setVehicleType(String vehicleType) {
			this.vehicleType = vehicleType;
		}

		@Override
		public String toString() {
			return "DriverInfoDTO [name=" + name + ", phoneNo=" + phoneNo + ", vehicleNo=" + vehicleNo + ", price="
					+ price + ", distance=" + distance + ", vehicleType=" + vehicleType + "]";
		}
           
           
}
