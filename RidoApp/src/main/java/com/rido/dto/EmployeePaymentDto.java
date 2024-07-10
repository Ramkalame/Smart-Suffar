package com.rido.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.rido.entity.Hub;
import com.rido.entity.HubEmployee;
import com.rido.entity.enums.AllPaymentStatus;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class EmployeePaymentDto {

	private Long employeeOrderId;
	private String employeeName;
	private String Address;
	private Date date;
	private Long hubEmployeeId;
	private String TotalAmount;
	private AllPaymentStatus allPaymentStatus;
	private String emailAddress;
	private String phoneNo;
	private String profileImgLink;
	private HubEmployee hubEmployee;
	private Hub hub;

}