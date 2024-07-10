package com.rido.payload.request;

public class LoginRequest {

//    @NotBlank
    private String usernameOrEmailOrPhoneNo;

//    @NotBlank
    private String password;

    public String getUsernameOrEmailOrPhoneNo() {
        return usernameOrEmailOrPhoneNo;
    }

    public void setUsernameOrEmailOrPhoneNumber(String usernameOrEmailOrPhoneNo) {
        this.usernameOrEmailOrPhoneNo = usernameOrEmailOrPhoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
