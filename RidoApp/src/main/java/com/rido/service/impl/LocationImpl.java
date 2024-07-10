package com.rido.service.impl;

import java.util.Optional;
import java.util.Random;

//import javax.mail.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.rido.config.TwilioConfig;
import com.rido.dto.UserPhoneResponseDto;
import com.rido.entity.ManageOtp;
import com.rido.entity.RegisterOtp;
import com.rido.entity.User;
import com.rido.entity.UserLocation;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.RegisterOtpRepository;
import com.rido.repository.UserLocationRepository;
import com.rido.repository.UserRepository;
import com.rido.service.LocationService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class LocationImpl implements LocationService {

	@Autowired
	private UserLocationRepository locationRepository;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private TwilioConfig twilioConfig;

	@Autowired
	private ManageOtpRepository manageOtpRepository;
	
	@Autowired
	private RegisterOtpRepository registerOtpRepository;

	@Override
	public UserLocation saveLocation(Long Id) {
		User user = null;
		try {
			user = userRepo.findById(Id).orElseThrow(() -> new NotFoundException());
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		UserLocation location = new UserLocation();

		location.setUserLatitude(location.getUserLatitude());
		location.setUserLongitude(location.getUserLongitude());

		// Assuming you have getters for currentLocation and destinationLocation in your
		// LocationRequest

		location.setUserId(user);
		locationRepository.save(location);
		return location;
//	    }
	}

	@Override
	public void saveLocation(Long userId, UserLocation location) {
		User user = null;
		try {
			user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException());

			if (user != null) {
				System.out.println(user + "line 55");

				location.setUserLatitude(location.getUserLatitude());
				location.setUserLongitude(location.getUserLongitude());

				location.setUserId(user);
				locationRepository.save(location);

//			        return location;
			}

		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		        Location location = new Location();
		// Assuming you have getters for currentLocation and destinationLocation in your
		// LocationRequest

//		    }		
	}

	@Value("${google.maps.api.key}")
	private String googleMapsApiKey;

	private final String DISTANCE_MATRIX_API_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

	public String getDistanceAndTime(String origin, String destination) {
		String url = String.format("%s?origins=%s&destinations=%s&key=%s", DISTANCE_MATRIX_API_URL, origin, destination,
				googleMapsApiKey);

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(url, String.class);
	}

	@Override
	public UserPhoneResponseDto signwithPhone(User user) {

		String otpSms = generateVerificationCode();
		sendVerificationCode(user.getPhoneNo(), otpSms);

		User savedUser = userRepo.save(user);
		ManageOtp manageOtp = new ManageOtp();
		manageOtp.setUser(savedUser);
		manageOtp.setRegisterOtp(otpSms);
		manageOtpRepository.save(manageOtp);

		UserPhoneResponseDto userPhoneResponseDto = new UserPhoneResponseDto();
		userPhoneResponseDto.setPhoneNo(savedUser.getPhoneNo());

		return userPhoneResponseDto;

	}

	@Override
	public void sendVerificationCode(String contactNo, String verificationCode) {
		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());

		// Send the verification code via SMS using Twilio
		Message message = Message.creator(new com.twilio.type.PhoneNumber(contactNo),
				new com.twilio.type.PhoneNumber(twilioConfig.getTrailNumber()), // Use your Twilio phone number
																				// here"+16562230668"
				"Your verification code: " + verificationCode).create();

	}

	public String generateVerificationCode() {
		// Generate a random 6-digit verification code
		Random random = new Random();
		int code = 1000 + random.nextInt(9000);
		return String.valueOf(code);
	}

	
	
	@Override
	public boolean verifySmsOtp(Long userId, String otp) {
//    	     Optional<ManageOtp> manageOtps = manageOtpRepository.findByUser_UserId(userId);

//    	     for (ManageOtp manageOtp : manageOtps) {
//    	         String registerOtp = manageOtp.getRegisterOtp();
//    	         System.out.println(registerOtp +" line 167");
//    	         if (registerOtp != null && registerOtp.equals(otp)) {
//    	             return true;
//    	         }
//    	     }

		return false; // Return null when OTP doesn't match any stored OTP
	}

	@Override
	public boolean verifyRegisterPhoneNoOtp(String phoneNo, String smsOtp) {
		Optional<RegisterOtp> manageOtps = registerOtpRepository.findByPhoneNo(phoneNo);

		// Check if the optional contains a value
		if (manageOtps.isPresent()) {
			RegisterOtp manageOtp = manageOtps.get();
			String storedOtp = manageOtp.getRegisterPhoneOtp(); // Assuming OTP is stored in the RegisterOtp object
			System.out.println(storedOtp + "byemail 185");

			// Check if the storedOtp matches smsOtp
			if (storedOtp.equals(smsOtp)) {
				System.out.println(smsOtp + "byemail 190");
				return true;
			}
		}

		return false;
	}

	@Override
	public String generateRandomOtp() {
		Random random = new Random();
		int code = 1000 + random.nextInt(9000);
		return String.valueOf(code);
	}

	@Override
	public boolean verifyRegisterEmailOtp(String email, String emailOtp) {
		Optional<RegisterOtp> manageOtps = registerOtpRepository.findByEmail(email);

		// Check if the optional contains a value
		if (manageOtps.isPresent()) {
			RegisterOtp manageOtp = manageOtps.get();
			String storedOtp = manageOtp.getRegisterEmailOtp(); // Assuming OTP is stored in the RegisterOtp object
			System.out.println(storedOtp + "byemail 185");

			// Check if the storedOtp matches smsOtp
			if (storedOtp.equals(emailOtp)) {
				System.out.println(emailOtp + "byemail 190");
				return true;
			}
		}

		return false;
	}

	@Override
	public String generateRandomEmailOtp() {
		Random random = new Random();
		int code = 1000 + random.nextInt(9000);
		return String.valueOf(code);
	}

}
