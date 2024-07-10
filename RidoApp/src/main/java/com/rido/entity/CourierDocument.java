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
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Table(name = "courierdocument")
@Table(name = "courierdocument", uniqueConstraints = { @UniqueConstraint(columnNames = "accountNo"),

		

		@UniqueConstraint(columnNames = "panCardNo"), @UniqueConstraint(columnNames = "aadhaarNo"),	@UniqueConstraint(columnNames = "insurance"),@UniqueConstraint(columnNames = "licence"),@UniqueConstraint(columnNames = "registerCertificate"), 
       

     }) 
public class CourierDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long courierdocumentId;

	private String courierDriverImage;

	private String vehicleImage;

	private String registerCertificate;

	private String licence;

	private String insurance;

	private String passbook;

	private String accountNo;

	private String IFSCcode;

	private String accountHolderName;

	private String aadhaarNo;
	
	private String branchName;

	private String panCardNo;

}
