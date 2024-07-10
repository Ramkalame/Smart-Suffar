package com.rido.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "driver_document")
public class DriverDocument {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long DriverDocumentid;
	private String DL;
	private String VSV;
	private String VFV;
	private String Dphoto;
	private String VRC;
	private String Dsignature;
	private String DAddressproof;

	private String driverSignature;
	
	
	private String adharCard;
	private String driverImage;

	private String Dpassbook;// image
	private String driverPanCard;

	@Column(unique = true, nullable = false)
	private String accountNo;

	@Column(unique = true, nullable = false)
	private String IFSCcode;

	private String accountHolderName;

	private String branchName;
	private boolean approved;

	private boolean rejected;

	@OneToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

}
