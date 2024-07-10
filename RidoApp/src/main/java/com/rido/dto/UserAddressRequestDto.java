package com.rido.dto;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserAddressRequestDto {

	
	private Double startingPointLatitude; 
	private Double startingPointLongitude; 
	private Double endingPointLatitude; 
	private Double endingPointLongitude; 
	private Date date;
	private Time time;
	private LocalDateTime localdatetime;
	
	
	

}
