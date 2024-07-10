package com.rido.dto;

import org.springframework.stereotype.Component;

@Component
public class DriverDocumentResponseDto {
	
private Long accountNo;
	
	private String IFSCcode;
	
	private String accountHolderName;
	
	private String Dpassbook;
	
	private String branchName;
	
	private String driverSignature;
	private String adharCard;
	private String driverImage;
	private String DL;
	public Long getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(Long accountNo) {
		this.accountNo = accountNo;
	}
	public String getIFSCcode() {
		return IFSCcode;
	}
	public void setIFSCcode(String iFSCcode) {
		IFSCcode = iFSCcode;
	}
	public String getAccountHolderName() {
		return accountHolderName;
	}
	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}
	public String getDpassbook() {
		return Dpassbook;
	}
	public void setDpassbook(String dpassbook) {
		Dpassbook = dpassbook;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getDriverSignature() {
		return driverSignature;
	}
	public void setDriverSignature(String driverSignature) {
		this.driverSignature = driverSignature;
	}
	public String getAdharCard() {
		return adharCard;
	}
	public void setAdharCard(String adharCard) {
		this.adharCard = adharCard;
	}
	public String getDriverImage() {
		return driverImage;
	}
	public void setDriverImage(String driverImage) {
		this.driverImage = driverImage;
	}
	public String getDL() {
		return DL;
	}
	public void setDL(String dL) {
		DL = dL;
	}
	public DriverDocumentResponseDto(Long accountNo, String iFSCcode, String accountHolderName, String dpassbook,
			String branchName, String driverSignature, String adharCard, String driverImage, String dL) {
		super();
		this.accountNo = accountNo;
		IFSCcode = iFSCcode;
		this.accountHolderName = accountHolderName;
		Dpassbook = dpassbook;
		this.branchName = branchName;
		this.driverSignature = driverSignature;
		this.adharCard = adharCard;
		this.driverImage = driverImage;
		DL = dL;
	}
	public DriverDocumentResponseDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	
	
	
	
	

}