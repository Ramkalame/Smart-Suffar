package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class HubListDto {
	
    private Long hubId;
    private String hubName;
    private String managerName;
    private String paymentEndpoint;

}
