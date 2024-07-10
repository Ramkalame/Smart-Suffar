package com.rido.payload.response;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
//    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

//    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> role;

//    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @Size(max = 10) // Assuming phone number is 10 digits
    private String phoneNumber;

    private String designation;

    public SignupRequest() {
    }

    public SignupRequest(@NotBlank @Size(min = 3, max = 20) String username, @NotBlank @Size(max = 50) @Email String email,
            Set<String> role, @NotBlank @Size(min = 6, max = 40) String password, @Size(max = 10) String phoneNumber) {
        super();
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
    
//
//    public SignupRequest(@NotBlank @Size(max = 50) @Email String email,
//			@NotBlank @Size(min = 6, max = 40) String password, String designation) {
//		super();
//		this.email = email;
//		this.password = password;
//		this.designation = designation;
//	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String toString() {
        return "SignupRequest [username=" + username + ", email=" + email + ", role=" + role + ", password=" + password
                + ", phoneNumber=" + phoneNumber + "]";
    }

}
