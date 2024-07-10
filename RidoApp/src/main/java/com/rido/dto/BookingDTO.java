package com.rido.dto;

import java.time.LocalDateTime;
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
public class BookingDTO {
	private LocalDateTime time;
	private String driverName;
	private List<BookingDataDto> bookingDataList;
	private LocalDateTime bookingDate;
	private RideOrderStatus status;
}