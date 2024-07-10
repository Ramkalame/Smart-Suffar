package com.rido.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "bank_details", uniqueConstraints = { @UniqueConstraint(columnNames = "accountNo")})

public class BankDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long bankDetailId;
	
	private Long accountNo;

	private String ifsc;

	private String accountHolderName;

	private String branchName;
	
	@OneToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
	
	
	
	
	
	

}
