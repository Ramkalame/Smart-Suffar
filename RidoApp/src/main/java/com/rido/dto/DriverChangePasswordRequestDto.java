package com.rido.dto;

import org.springframework.stereotype.Component;

@Component
public class DriverChangePasswordRequestDto {

    private String oldPassword;
    
    private String newPassword;
    
    private String confirmPassword;
    
    public DriverChangePasswordRequestDto() {
		// TODO Auto-generated constructor stub
	}

	public DriverChangePasswordRequestDto(String oldPassword, String newPassword, String confirmPassword) {
		super();
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.confirmPassword = confirmPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	@Override
	public String toString() {
		return "DriverChangePasswordRequestDto [oldPassword=" + oldPassword + ", newPassword=" + newPassword
				+ ", confirmPassword=" + confirmPassword + "]";
	}
    
}
