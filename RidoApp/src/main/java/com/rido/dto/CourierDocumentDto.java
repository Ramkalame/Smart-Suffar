package com.rido.dto;

import com.rido.entity.Courier;
import com.rido.entity.CourierDocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierDocumentDto {
	 private Long courierdocumentId;
	    private String courierDriverImage;
	    private String vehicleImage;
	    private String registerCertificate;
	    private String licence;
	    private String insurance;
	    private String passbook;
	    private String accountNo;
	    private String accountHolderName;
	    private Long courierId;
}
