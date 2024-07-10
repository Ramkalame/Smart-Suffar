package com.rido.dto;

import org.springframework.stereotype.Component;

import com.rido.entity.UserLocation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class BookingDataDto {
	private String userName;
    private double pickuplatituelocation;
    private double pickuplonglocation;
    private double dropuplatitueLocation;
    private double dropuplongLocation;
    private String vehicleType;
    private String phoneNo;
    private String distance;
}
