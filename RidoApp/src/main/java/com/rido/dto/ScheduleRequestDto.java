package com.rido.dto;

import org.springframework.stereotype.Component;

import com.rido.entity.TimeDuration;
import com.rido.entity.UserLocation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ScheduleRequestDto {

    private UserLocation pickupLocation;
    private UserLocation dropOffLocation;
    private TimeDuration timeDuration;
	
}
