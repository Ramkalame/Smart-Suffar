package com.rido.service;

import org.springframework.stereotype.Service;

import com.rido.dto.UserPhoneResponseDto;
import com.rido.entity.UserLocation;
import com.rido.entity.User;

@Service
public interface LocationService {

	

	    public UserLocation saveLocation(Long Id);

		public void saveLocation(Long userId, UserLocation location);
		
		public UserPhoneResponseDto signwithPhone(User user);

		public String getDistanceAndTime(String origin, String destination);

		public boolean verifySmsOtp(Long userId, String otp);
		public void sendVerificationCode(String contactNo, String verificationCode);

		String generateRandomOtp();

		boolean verifyRegisterEmailOtp(String email, String emailOtp);

		String generateRandomEmailOtp();

		boolean verifyRegisterPhoneNoOtp(String phoneNo, String smsOtp);
}
