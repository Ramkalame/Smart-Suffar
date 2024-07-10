package com.rido.dto;

import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class PasswordRequestDto {
    
	@Override
	public int hashCode() {
		return Objects.hash(confirmPassword, newpassword);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PasswordRequestDto other = (PasswordRequestDto) obj;
		return Objects.equals(confirmPassword, other.confirmPassword) && Objects.equals(newpassword, other.newpassword);
	}

	private String newpassword ;
	
	private String confirmPassword ;
	
	public PasswordRequestDto() {
		// TODO Auto-generated constructor stub
	}

	public PasswordRequestDto(String newpassword, String confirmPassword) {
		super();
		this.newpassword = newpassword;
		this.confirmPassword = confirmPassword;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	@Override
	public String toString() {
		return "PasswordRequestDto [newpassword=" + newpassword + ", confirmPassword=" + confirmPassword + "]";
	}
	
	
}
