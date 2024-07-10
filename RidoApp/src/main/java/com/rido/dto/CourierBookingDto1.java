package com.rido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import java.util.List;

import org.springframework.stereotype.Component;

import com.rido.entity.enums.RideOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CourierBookingDto1 {
	private RideOrderStatus rideOrderStatus;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BigDecimal totalAmount;

}
