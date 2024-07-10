package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class HubMangerProfileEditDto {
    
	    private Long hubMangerId;
		private String fullName;
		private String email;
		private String uidNo;
		private String address;
		private String phoneNumber;
		private String profilePic;
		private String signatuePic ;
		private String passbookPic ;
}
