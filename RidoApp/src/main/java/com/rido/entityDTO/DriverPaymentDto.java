package com.rido.entityDTO;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DriverPaymentDto {
	
	private String accountNo;
	private String payableAmount;
	private String paymentMode;
	private String payOutNarration;
	private String notes;
	private String beneficiaryName;
	private String pnoneNo;
	private String email;
	private String invoiceNo;
	

}
