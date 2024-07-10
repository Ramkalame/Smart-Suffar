package com.rido.dto;
public class BookedOrderDTO {
	
	 private Long orderId;
	    private String userName;
	    private String driverName;

	    public BookedOrderDTO() {
			// TODO Auto-generated constructor stub
		}

		public BookedOrderDTO(Long orderId, String userName, String driverName) {
			super();
			this.orderId = orderId;
			this.userName = userName;
			this.driverName = driverName;
		}

		public Long getOrderId() {
			return orderId;
		}

		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getDriverName() {
			return driverName;
		}

		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}

		@Override
		public String toString() {
			return "BookedOrderDTO [orderId=" + orderId + ", userName=" + userName + ", driverName=" + driverName + "]";
		}
	    
	    
}
