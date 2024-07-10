package com.rido.dto;

import lombok.Data;
import java.math.BigDecimal;

import com.rido.entity.enums.RideOrderStatus;

@Data
public class CourierBookingDto {
    private Long courierBookingId;
    private Long senderReceiverInfoId;
    private Long timeDurationId;
    private Long courierId;
    private Long userId;
    private RideOrderStatus rideOrderStatus;
    private double gst;
    private double pricePerKm;
    private String senderName;
    private String receiverName;
    private String promoCode;
    private BigDecimal baseAmount;
    private BigDecimal totalAmount;
}