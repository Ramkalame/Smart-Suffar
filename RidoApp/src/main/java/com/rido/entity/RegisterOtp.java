package com.rido.entity;

import java.time.LocalDateTime;

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
@Table(name = "registerotp")
public class RegisterOtp {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long registermanageOtpId;

	private String phoneNo;
	private String registerPhoneOtp;// registerOtp

	private String email;
	private String registerEmailOtp;
	private LocalDateTime timestamp;// timestamp

}
