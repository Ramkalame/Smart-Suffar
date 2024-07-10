package com.rido.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class HubPaymentHistoryDto {

	private String hubName;
	private String managerName;
//	private String phoneNo;
	private String amount;
	private LocalDateTime date;
	private String status;

}
