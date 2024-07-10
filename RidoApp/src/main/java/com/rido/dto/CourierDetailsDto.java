package com.rido.dto;

import com.rido.entity.enums.DriverAndVehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierDetailsDto {

	private Long courierdocumentId;
	private String name;
	private String email;
	private String phoneNo;
	
    private String courierDriverImage;
    private String vehicleImage;
    private String registerCertificate;
    private String licence;
    private String insurance;
    private String passbook;
    private String accountNo;
    private String accountHolderName;
    private String aadhar;
    
    private String vehicleNo;
    private double wieght;
    private DriverAndVehicleType vehicleType;

}
