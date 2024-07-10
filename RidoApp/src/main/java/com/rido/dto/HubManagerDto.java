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
public class HubManagerDto {
	
	private Long hubId;
	private String managerName ;
	private String hubName;
	private String email;
	
	private String phoneNo;
	private String profileImgLink;
	
     private Status stauts;
     
     private String city;
     private String state;
	
}
