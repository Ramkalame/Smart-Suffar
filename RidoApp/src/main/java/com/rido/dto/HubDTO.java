package com.rido.dto;

import org.springframework.stereotype.Component;

import com.rido.entity.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Component
public class HubDTO {
	private String managerName;
	private String location;

}
