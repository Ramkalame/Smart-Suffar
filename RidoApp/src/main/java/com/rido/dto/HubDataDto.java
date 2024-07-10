package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class HubDataDto {

	private Long hubMangerId;
	private String name;
	private String email;
	private String address ;
	private String uidNo;

	private String phoneNumber;
}
