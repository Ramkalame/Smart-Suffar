package com.rido.dto;

import org.springframework.stereotype.Component;

@Component
public class ChangePasswordRequestDto {
	
    private String oldPassword;
    
    private String newPassword;
    
    private String confirmPassword;
    
    public ChangePasswordRequestDto() {
		// TODO Auto-generated constructor stub
	}

	public ChangePasswordRequestDto(String oldPassword, String newPassword, String confirmPassword) {
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
		return "ChangePasswordRequestDto [oldPassword=" + oldPassword + ", newPassword=" + newPassword
				+ ", confirmPassword=" + confirmPassword + "]";
	}
    
}
