package com.rido.dto;

import java.math.BigDecimal;

import com.rido.entity.PaymentActivity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentActivityResponseDto {
	

		private PaymentActivity paymentActivity;
	private String messageNotification;
  
}
