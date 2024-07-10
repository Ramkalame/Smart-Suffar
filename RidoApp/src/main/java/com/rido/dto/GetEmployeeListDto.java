package com.rido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Component
public class GetEmployeeListDto {
  
	private String name ;
	private Long hubEmpId ;
	private String phoneNo;
	private String email;
}
