package com.rido.dto;

import org.springframework.stereotype.Component;

import com.rido.entity.CourierEbike;
import com.rido.entity.Vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class VehicleCourierEbikeDto {

	private Vehicle vehicle;
    private CourierEbike courierEbike;
}
