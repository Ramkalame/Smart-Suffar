package com.rido.dto;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserPaymentHistoryDto {
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	private Long Id;
    private String customerName;
//	private String hubName;
    private BigDecimal amount;
    private String managerName;

}
