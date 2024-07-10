package com.rido.dto;

import com.rido.entity.enums.CourierPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private CourierPaymentStatus status;
}