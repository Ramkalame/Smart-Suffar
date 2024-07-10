package com.rido.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TimeDuration {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	
	 public TimeDuration(LocalDateTime startDateTime, LocalDateTime endDateTime) {
	        this.startDateTime = startDateTime;
	        this.endDateTime = endDateTime;
	    }
	public TimeDuration(String startDateTime2, Object endDateTime2) {
		// TODO Auto-generated constructor stub
	}
	
}
