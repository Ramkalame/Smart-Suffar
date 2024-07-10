package com.rido.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "register_manage_otp")
public class RegisterEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long registermanageOtpId;

	private String phoneNo;
	private String registerphoneOtp;// registerOtp
	
	private String email;
	private String registeremailOtp;

}
