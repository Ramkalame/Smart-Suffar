package com.rido.dto;

public class ContactUsRequestDto {

	private String name;

	private String email;

	private String phoneNo;

	private String message;
	
	public ContactUsRequestDto() {
		// TODO Auto-generated constructor stub
	}

	public ContactUsRequestDto(String name, String email, String phoneNo, String message) {
		super();
		this.name = name;
		this.email = email;
		this.phoneNo = phoneNo;
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ContactUsRequestDto [name=" + name + ", email=" + email + ", phoneNo=" + phoneNo + ", message="
				+ message + "]";
	}
	
}
