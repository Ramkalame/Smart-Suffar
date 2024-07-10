package com.rido.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.ContactUsRequestDto;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.UserAddressRequestDto;
import com.rido.dto.UserEmailResponseDto;
import com.rido.dto.UserLocationChooseDto;
import com.rido.dto.UserSetNameRequestDto;
import com.rido.dto.UserUpdateRequestDto;
import com.rido.dto.VehicleFareDTO;
import com.rido.entity.Booking;
import com.rido.entity.CancellationReason;
import com.rido.entity.Driver;
import com.rido.entity.Hub;
import com.rido.entity.ManageOtp;
import com.rido.entity.PaymentActivity;
import com.rido.entity.RentalBooking;
import com.rido.entity.User;
import com.rido.entity.UserCourierPayment;
import com.rido.entity.UserIdentity;
import com.rido.entity.UserLocation;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.RideOrderStatus;
import com.rido.entity.enums.Status;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.BookingRepository;
import com.rido.repository.CancellationReasonRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.PaymentRepository;
import com.rido.repository.RentalBookingRepository;
import com.rido.repository.UserCourierPaymentRepository;
import com.rido.repository.UserIdentityRepository;
import com.rido.repository.UserLocationRepository;
import com.rido.repository.UserRepository;
import com.rido.repository.VehicleRepository;
import com.rido.service.DriverService;
import com.rido.service.LocationService;
import com.rido.service.UserService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	
	
	@Autowired
	private UserCourierPaymentRepository userCourierPaymentRepository;

	@Autowired
	private SendGrid sendGrid;

	@Autowired
	private VehicleRepository vehicleRepository;
	@Autowired
	private CancellationReasonRepository cancellationReasonRepository;

	@Value("${project.image}")
	private String path;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ManageOtpRepository manageOtpRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private UserLocationRepository locationRepository;

	@Autowired
	private UserIdentityRepository userIdentityRepository;

//	RWI120

	@Autowired
	private LocationService locationService;

	@Autowired
	private DriverService driverService;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private LocationImpl locationImpl;

	@Autowired
	private RazorpayClient razorpayClient;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private RentalBookingRepository rentalBookingRepository;

	@Autowired
	private DriverRepository driverRepository;
	@Autowired
	private CancellationServiceImpl cancellationServiceImpl;

	@Override
	public UserEmailResponseDto signwithEmail(User user) {
		user.setEmail(user.getEmail());

		String otpEmail = generateRandomOtp();

		sendOtpByEmail(user.getEmail(), otpEmail);

		// Save user
		User savedUser = userRepository.save(user);

		ManageOtp manageOtp = new ManageOtp();

		manageOtp.setRegisterOtp(otpEmail);
		manageOtp.setUser(savedUser);

		manageOtpRepository.save(manageOtp);
		// Create UserEmailResponseDto instance
		UserEmailResponseDto responseDto = new UserEmailResponseDto();
		responseDto.setEmail(savedUser.getEmail());

		return responseDto;
	}

	@Override
	public boolean setName(Long userId, UserSetNameRequestDto userSetNameRequestDto) {

		User user = userRepository.findById(userId).orElse(null);
		if (user != null) {
//		user.setUserId(userId);
			user.setName(userSetNameRequestDto.getName());
//			user.setFirstName(userSetNameRequestDto.getFirstName());
//			user.setLastName(userSetNameRequestDto.getLastName());
		}
		userRepository.save(user);
		return true;
	}

	@Override
	public boolean changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto) {
		// Retrieve user from the database

		User user = userRepository.findById(userId).orElse(null);

		// Check if the old password matches the stored password
		System.out.println(user.getPassword() + "user old pass");

		System.out.println(user.getPhoneNo() + " phoneno");
		if (user != null && passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), user.getPassword())) {

			Optional<UserIdentity> userIdentityOptional = userIdentityRepository.findByPhoneNo(user.getPhoneNo());
			System.out.println(userIdentityOptional.get() + " user phoneno line 148");
			// Check if the new password and confirm password match
			if (userIdentityOptional.isPresent()) {
				UserIdentity userIdentity = userIdentityOptional.get();

				// Optional: You might want to log userIdentity to ensure it's retrieved
				// correctly
				System.out.println("UserIdentity: " + userIdentity);

				System.out.println(userIdentity.getPhoneNo());

				if (user.getPhoneNo().equals(userIdentity.getPhoneNo())) {
					if (changePasswordRequestDto.getNewPassword()
							.equals(changePasswordRequestDto.getConfirmPassword())) {
						String encodedPassword = passwordEncoder.encode(changePasswordRequestDto.getNewPassword());

						user.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						userRepository.save(user);
						userIdentityRepository.save(userIdentity);
						return true;
					}
				}
			}
		}
		return false;
	}

//	email varificationP //	RWI120

	public void sendOtpByEmail(String email, String otp) {
		Email from = new Email("jaleshwarimasram@gmail.com"); // Replace with your sender email
		Email to = new Email(email);
		String subject = "OTP Verification";
		Content content = new Content("text/plain", "Your  OTP: " + otp);
		Mail mail = new Mail(from, subject, to, content);
		Request request = new Request();

		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());

			Response response = sendGrid.api(request);

			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
			System.out.println(response.getHeaders());
		} catch (Exception ex) {
			throw new RuntimeException("Error sending OTP email", ex);
		}
	}

//	RWI120

	@Override
	public ResponseLogin getByPhoneno(String phoneno) {

		User user = userRepository.findByPhoneNo(phoneno).orElseThrow();

		if (user != null) {

			ResponseLogin response = new ResponseLogin();
			response.setUserId(user.getUserId());
			response.setEmail(user.getEmail());
			response.setPhoneNo(user.getPhoneNo());
			response.setName(user.getName());
			response.setUserName(user.getUsername());

			return response;

		}

		return null;
	}

	public String generateRandomOtp() {
		return String.format("%04d", new Random().nextInt(9999));
	}

//	RWI

	public boolean verifyEmailOtp(Long userId, String otp) {

		Optional<ManageOtp> manageOtpOptional = manageOtpRepository.findByUser_UserId(userId);

		// Check if the optional contains a value
		if (manageOtpOptional.isPresent()) {
			ManageOtp manageOtp = manageOtpOptional.get();
			String smsOtp = manageOtp.getRegisterOtp();

			// Check if the forgetOtp matches smsOtp
			if (smsOtp.equals(otp)) {
				System.out.println(smsOtp + "byemail 667");
				return true;
			}
		}

		return false;

	}

	@Override
	public User signwithGoogle(User user) {

		return null;
	}

//	@Override
//	public User signwithGoogle(String email, String password) {
//		
//	 User user = userRepo.findByEmail(email).orElseThrow();
//	 
//	 if(user.getPassword()==password) {
//		 
//		user.setEmail(email);
//		user.setPassword(password);
//		
//		
//	 }
//	
//		return null;
//	}

//	@Override
//	public String getOTP(String phoneNo) {
//		User user=userRepo.findByPhoneNo(phoneNo);
//		System.out.println(user);
//		if(user!=null) {
//			return user.getOtp();
//		}else {
//			return null;
//		}
//	}

	@Override
	public String getotpByphone(String phoneNo) {
		Optional<User> userOptional = Optional.ofNullable(userRepository.findByPhoneNo(phoneNo)).orElseThrow();
		return userOptional.map(User::getOtp).orElse(null);
	}

	@Override
	public boolean verifyotp(String phoneNo, String userOTP) {
		Optional<User> storedotp = userRepository.findByPhoneNo(phoneNo);
		return false;

//		System.out.println(storedotp.getOtp());
//
//		if (storedotp != null && storedotp.getOtp().equals(userOTP)) {
//			return true;
//		} else {
//			return false;
////		}
	}

	@Override
	public User edituserprofile(Long id, User user) {
		if (id == null || !userRepository.existsById(id)) {
			return null;
		}

		User existinguser = userRepository.findById(id).orElse(null);
		if (existinguser != null) {

//			existinguser.setFirstName(user.getFirstName());
//			existinguser.setLastName(user.getLastName());
			existinguser.setName(user.getName());
//			existinguser.setPhoneNo(user.getPhoneNo());
			existinguser.setAlternativeNo(user.getAlternativeNo());
//			existinguser.setEmail(user.getEmail());
			existinguser.setGender(user.getGender());
			existinguser.setDob(user.getDob());

			userRepository.save(existinguser);
			return existinguser;

		} else {
			return null;
		}
	}

	@Override
	public String contactUs(ContactUsRequestDto contactUsRequestDto) {
		
		return null;
	}

// start method  RWI115
	@Override
	public String forgetPasswordgenerateOtp() {
		// TODO Auto-generated method stub
		Random randomNumber = new Random(10000);
		int newOtp = randomNumber.nextInt(99999);
		String message = "Your Verification Code is " + newOtp + ".Do not share your verification code to anyone.";
		return message;
	}

//	@Override
//	public String forgetPasswordByEmail(String email) {
//		String host = "smtp.gmail.com";
//		String extractedEmail = extractEmailFromJson(email);
//
//		Properties properties = new Properties();
//		properties.put("mail.smtp.host", host);
//		properties.put("mail.smtp.port", "587");
//		properties.put("mail.smtp.starttls.enable", "true");
//		properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
//		properties.put("mail.smtp.auth", "true");
//
//		Session session = Session.getInstance(properties, new Authenticator() {
//			@Override
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication("kalameram70@gmail.com", "faehyxydlbtobdvb");
//			}
//		});
//
//		session.setDebug(true);
//
//		try {
//			MimeMessage m = new MimeMessage(session);
//			String trimmedEmail = extractedEmail.trim();
//			m.setFrom(new InternetAddress("kalameram70@gmail.com"));
//			m.addRecipient(Message.RecipientType.TO, new InternetAddress(trimmedEmail));
//			m.setSubject("Rido App - Email Verification");
//
//			// Create the body part for OTP
//			MimeBodyPart newGeneratedOtp = new MimeBodyPart();
//			newGeneratedOtp.setText(forgetPasswordgenerateOtp());
//
//			// Create a multipart message and add the OTP body part
//			MimeMultipart mimeMultipart = new MimeMultipart();
//			mimeMultipart.addBodyPart(newGeneratedOtp);
//
//			// Set the content of the message to the multipart message
//			m.setContent(mimeMultipart);
//
//			// Send the message
//			Transport.send(m);
//			System.out.println("OTP sent Successfully......!!!");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "Error sending mail: " + e.getMessage();
//		}
//
//		return "Sent Successfully mail!!";
//	}

	private String extractEmailFromJson(String jsonString) {
		try {
			// Use Jackson ObjectMapper to parse JSON
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(jsonString);

			// Assuming the email is directly provided as a string in the JSON
			String extractedEmail = jsonNode.get("email").asText();

			// Trim the extracted email
			return extractedEmail.trim();
		} catch (Exception e) {
			// Handle parsing exceptions or log the error
			e.printStackTrace();
			return ""; // Return an empty string or handle as appropriate
		}
	}

//	end code RWI115

//	@Override
//	public String generateOtp(String otp) {
//		
//		return null;
//	}
//
//	@Override
//	public String forgetPasswordByPhoneNo(String mobileNo) {
//		
//		return null;
//	}

//	@Override
//	public boolean setNewPassword(Long userId, PasswordRequestDto passwordDto) {
//
//		User user = userRepo.findById(userId).orElse(null);
//
//		if (user != null) {
//			User user1 = new User();
//
//			passwordDto.setNewpassword(passwordDto.getNewpassword());
//			passwordDto.setNewpassword(passwordDto.getConfirmPassword());
//
//			String newpassword = userRepo.getNewpassword();
//			String confirmPassword = userRepo.getConfirmPassword();
//
//			// Validate passwords
//			if (newpassword == confirmPassword)
//				return true;
//			User obj = new User();
//			obj.setPassword();
//			userRepo.save(obj);
//
//		}
//		return false;
//
//	}
//
//	private boolean isValidPassword(String newPassword, String confirmPassword) {
//		// Check if passwords match
//		if (!newPassword.equals(confirmPassword)) {
//			return false;
//		}
//
//		// Check if the password contains at least one number and one special character
//		if (!newPassword.matches(".*\\d.*") || !newPassword.matches(".*[!@#$%^&*()-_=+\\[\\]{}|;:'\",.<>/?].*")) {
//			return false;
//		}
//
//		return true;
//	}

	@Override
	public boolean setNewPassword(Long userId, PasswordRequestDto passwordDto) {

		User user = userRepository.findById(userId).orElse(null);

		if (user != null) {

			if (isValidPassword(passwordDto.getNewpassword(), passwordDto.getConfirmPassword())) {

				Optional<UserIdentity> userIdentityOptional = userIdentityRepository.findByPhoneNo(user.getPhoneNo());

				if (userIdentityOptional.isPresent()) {
					UserIdentity userIdentity = userIdentityOptional.get();

					if (user.getPhoneNo().equals(userIdentity.getPhoneNo())) {

						String encodedPassword = passwordEncoder.encode(passwordDto.getNewpassword());

						user.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						userRepository.save(user);
						userIdentityRepository.save(userIdentity);
						return true;
					}
				}
			}
		}
		return false; // Password update failed
	}

	public boolean isValidPassword(String newPassword, String confirmPassword) {
		// Check if passwords match
		if (!newPassword.equals(confirmPassword)) {
			return false;
		}
		// Check if the password contains at least one number and one special character
		return newPassword.matches(".*\\d.*") && newPassword.matches(".*[!@#$%^&*()-_=+\\[\\]{}|;:'\",.<>/?].*");
	}

	@Override
	public List<VehicleFareDTO> calculateFares(double distance) {
		List<Vehicle> vehicles = vehicleRepository.findAll();
		List<VehicleFareDTO> fares = new ArrayList<>();

		BigDecimal distanceBigDecimal = BigDecimal.valueOf(distance); // Convert distance to BigDecimal

		for (Vehicle vehicle : vehicles) {
			BigDecimal fare = distanceBigDecimal.multiply(vehicle.getPricePerKm()); // Perform multiplication using
																					// BigDecimal
			VehicleFareDTO vehicleFareDTO = new VehicleFareDTO();
			vehicleFareDTO.setDistance(distanceBigDecimal.doubleValue()); // Convert back to double for setting in DTO
//			vehicleFareDTO.setVehicleType(vehicle.getVehicleType());
			vehicleFareDTO.setTotalPrice(fare);
			fares.add(vehicleFareDTO);
		}

		return fares;
	}

//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		User user = userRepo.findByUsernameOrEmail(username, username);
//		if (user == null) {
//			throw new UsernameNotFoundException("User not exists by Username");
//		}
//
//		Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
//				.map((role) -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
//		return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
//	}

	@Override
	public ResponseLogin getByEmail(String email) {
		User user = userRepository.findByEmail(email).orElseThrow();
		if (user != null) {
			ResponseLogin response = new ResponseLogin();
			response.setUserId(user.getUserId());
			response.setEmail(user.getEmail());
			response.setPhoneNo(user.getPhoneNo());
			response.setUserName(user.getUsername());
			response.setName(user.getName());

			return response;
		} else {
			return null;
		}
	}

	@Override
	public String updateUserProfile(Long id, UserUpdateRequestDto userDataDto, String s3Url)
			throws UserNotFoundException {
		// Fetch user from the database
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		// Update user's profile data
		user.setName(userDataDto.getName());
		//user.setPhoneNo(userDataDto.getPhoneNo());
		user.setAlternativeNo(userDataDto.getAlternativeNo());
		user.setGender(userDataDto.getGender());
		user.setDob(userDataDto.getDob());
		//user.setEmail(userDataDto.getEmail());
		user.setImageProfileLink(s3Url);
		userRepository.save(user);

		// Return success message
		return "User profile updated successfully";
	}

	@Override
	public String userPickupAddress(Long userId, UserAddressRequestDto userpickupaddress) {
		

		Optional<User> user = userRepository.findById(userId);

		if (user.isPresent()) {
			UserLocation location = new UserLocation();

			location.setUserLatitude(userpickupaddress.getStartingPointLatitude());
			location.setUserLongitude(userpickupaddress.getStartingPointLongitude());
			location.setUserLatitude(userpickupaddress.getEndingPointLatitude());
			location.setUserLongitude(userpickupaddress.getEndingPointLongitude());
			location.setLocalDateTime(userpickupaddress.getLocaldatetime());
			location.setUserId(user.get());
			System.out.println("line 618" + location);
			locationRepository.save(location);
			return "User pickup address saved successfully";
		}

		return "User pickup address not found";
	}

	@Override
	public UserLocationChooseDto userLocation(Long userId) throws UserNotFoundException {
		UserLocation locationDetails = locationRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User with this id " + userId + " not found"));

		UserLocationChooseDto userLocation = new UserLocationChooseDto();
		userLocation.setStartingPointLatitude(locationDetails.getUserLatitude());
		userLocation.setStartingPointLongitude(locationDetails.getUserLongitude());
		userLocation.setEndingPointLatitude(locationDetails.getUserLatitude());
		userLocation.setEndingPointLongitude(locationDetails.getUserLatitude());
		userLocation.setLocalDateTime(locationDetails.getLocalDateTime());
		userLocation.setNote(locationDetails.getPickupNotes());

		Booking booking = new Booking();
		booking.setPickupLocation(locationDetails); // Set the user location as the pickup location
		// Set other booking details as needed
		// Calculate distance
		System.out.println(locationDetails.getUserLatitude());
		System.out.println(locationDetails.getUserLongitude());
		System.out.println(locationDetails.getUserLatitude());
		System.out.println(locationDetails.getUserLongitude());

		double distance = calculateDistance(locationDetails.getUserLatitude(), locationDetails.getUserLongitude(),
				locationDetails.getUserLatitude(), locationDetails.getUserLongitude());
		// Calculate total amount based on distance (you need to define your pricing
		// logic)
		System.out.println("distance" + distance);
		double totalAmount = calculateTotalAmount(distance);
		System.out.println(totalAmount);
		// Get promo code from the booking
		String promoCode = booking.getPromoCode();
		if (promoCode != null && !promoCode.isEmpty()) {
			// Apply discount based on promo code (you need to define your discount logic)
			double discountAmount = calculateDiscount(promoCode, totalAmount);
			// Subtract discount amount from total amount
			totalAmount -= discountAmount;
		}

		// 5 % GST Adding
		double additionalFee = totalAmount * 0.05;
		totalAmount += additionalFee;

		booking.setTotalAmount(BigDecimal.valueOf(totalAmount));
		// Save the booking entity to persist it in the database
		Booking savedBooking = bookingRepository.save(booking);
		// If there is a saved booking, set the total amount and other booking details
		// in the DTO
		if (savedBooking != null) {
			userLocation.setCoupon(savedBooking.getPromoCode());
			userLocation.setTotalAmount(savedBooking.getTotalAmount());
			// Set distance
			userLocation.setDistance(distance);
			// Set payment if needed
			userLocation.setPayment(userLocation.getPayment());
		}
		return userLocation;
	}

	// Define your pricing logic here (for examp
//	le, a simple pricing strategy based on distance)
	private double calculateTotalAmount(double distance) {
		// Define your pricing logic here
		// For example, $1 per kilometer
		double pricePerKm = 15.0;
		return pricePerKm * distance;
	}

	private double calculateDiscount(String promoCode, double totalAmount) {
		// Define your discount calculation logic based on the promo code
		// For example, if promo code is "DISCOUNT10", apply a 10% discount
		if ("DISCOUNT10".equals(promoCode)) {
			return totalAmount * 0.1; // 10% discount
		} else {
			return 0.0; // No discount
		}
	}

	public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		// Convert latitude and longitude from degrees to radians
		final int R = 6371;
		double lat1Rad = Math.toRadians(lat1);
		double lon1Rad = Math.toRadians(lon1);
		double lat2Rad = Math.toRadians(lat2);
		double lon2Rad = Math.toRadians(lon2);
		// Calculate the differences between latitudes and longitudes
		double latDiff = lat2Rad - lat1Rad;
		double lonDiff = lon2Rad - lon1Rad;
		// Haversine formula
		double a = Math.pow(Math.sin(latDiff / 2), 2)
				+ Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(lonDiff / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		// Calculate the distance
		double distance = R * c;

		return distance;
	}

	@Override
	public String updateUserLocation(Long userId, UserLocationChooseDto userlocation) throws UserNotFoundException {
		

		UserLocation locationdeatils = locationRepository.findById(userId).get();
		if (locationdeatils.equals(null)) {
			throw new UserNotFoundException("User with this is id " + userId + " not found");
		} else {

			UserLocationChooseDto userlocation1 = new UserLocationChooseDto();
			Optional<Booking> booking = bookingRepository.findById(userId);
			locationdeatils.setUserLocationId(locationdeatils.getUserLocationId());
			locationdeatils.setUserLatitude(locationdeatils.getUserLatitude());
			;

			locationdeatils.setUserLongitude(locationdeatils.getUserLongitude());
			;
			locationdeatils.setPickupNotes(userlocation1.getNote());
			userlocation1.setCoupon(userlocation1.getCoupon());
			userlocation1.setTotalAmount(booking.get().getTotalAmount());
			userlocation1.setPayment(null);
			return "Your Booking has been placed";
		}

	}

	@Override
	public String forgetPassword(String phoneNo) {
		String otp = generateVerificationCode();

		System.out.println("line 680: " + phoneNo);
		Optional<User> userOptional = userRepository.findByPhoneNo(phoneNo);

		System.out.println("line 680: " + userOptional);

		if (userOptional.isEmpty()) {
			return "User not found";
		}

		User user = userOptional.get();

		Optional<ManageOtp> manageOtpOptional = manageOtpRepository.findByUser_UserId(user.getUserId());

		if (manageOtpOptional.isEmpty()) {
			return "ManageOtp not found";
		}

		ManageOtp manageOtp = manageOtpOptional.get();

		manageOtp.setForgetOtp(otp);
		manageOtpRepository.save(manageOtp);

		locationService.sendVerificationCode(manageOtp.getUser().getPhoneNo(), otp);
		return "Verification code sent successfully";
	}

	public String generateVerificationCode() {
		// Generate a random 6-digit verification code
		Random random = new Random();
		int code = 1000 + random.nextInt(9000);
		return String.valueOf(code);
	}

	@Override
	public List<Driver> bookCar(Long userId, double latitude, double longitude) {
		// Find nearest hub based on user's location
		Hub nearestHub = findNearestHub(latitude, longitude);

		if (nearestHub != null) {
			// Logic to send car booking request to the nearest hub
			// For demonstration purposes, let's assume it's just a console log
			System.out.println("Car booking request sent to the nearest hub: " + nearestHub.getManagerName());

			// Assign nearby drivers to the nearest hub
			return driverService.assignDriversToHub(nearestHub.getHubId(), latitude, longitude, 5); // Assuming a radius
																									// of 5 for
																									// demonstration
		} else {
			System.out.println("No hub found nearby for car booking.");
		}
		return null;

	}

	private Hub findNearestHub(double latitude, double longitude) {
		// Implement logic to find the nearest hub based on latitude and longitude
		// For simplicity, let's assume we return the first hub found
		return hubRepository.findAll().stream().findFirst().orElse(null);
	}

	@Override
	public boolean forgetPasswordVerify(Long userId, String forgotOtp) {

		Optional<ManageOtp> manageOtpOptional = manageOtpRepository.findByUser_UserId(userId);

		// Check if the optional contains a value
		if (manageOtpOptional.isPresent()) {
			ManageOtp manageOtp = manageOtpOptional.get();
			String smsOtp = manageOtp.getForgetOtp();

			// Check if the forgetOtp matches smsOtp
			if (smsOtp.equals(forgotOtp)) {
				System.out.println(smsOtp + "byemail 667");
				return true;
			}
		}

		return false;
	}

	@Override
	public void changePhoneNo(Long userId, String newPhoneNo, String email) {
		Optional<User> optionalUser = userRepository.findById(userId);

		if (optionalUser.isPresent()) {
			User existingUser = optionalUser.get();

			// Check if the new phone number is different from the old phone number
			if (!existingUser.getPhoneNo().equals(newPhoneNo)) {
				// Generate OTP
				String otp = locationImpl.generateVerificationCode();
				System.out.println("OTP: " + otp);

				Optional<ManageOtp> existingManageOtp = manageOtpRepository.findByUser_UserId(userId);
				if (existingManageOtp.isPresent()) {
					ManageOtp manageOtp = existingManageOtp.get();
					System.out.println("manageOtp=" + manageOtp);
					manageOtp.setRegisterOtp(otp);
					manageOtpRepository.save(manageOtp);
				} else {
					throw new RuntimeException("ManageOtp not found for user with ID: " + userId);
				}
				// Update user's phone number
				existingUser.setPhoneNo(newPhoneNo);
				userRepository.save(existingUser);

				// Update user identity's phone number
				Optional<UserIdentity> optionalUserIdentity = userIdentityRepository.findByEmail(email);
				if (optionalUserIdentity.isPresent()) {
					UserIdentity existingUserIdentity = optionalUserIdentity.get();
					existingUserIdentity.setPhoneNo(newPhoneNo);
					userIdentityRepository.save(existingUserIdentity);
				} else {
					throw new RuntimeException("UserIdentity not found with email: " + email);
				}
			} else {
				throw new RuntimeException("New phone number is the same as the existing phone number: " + newPhoneNo);
			}
		} else {
			throw new RuntimeException("User not found with ID: " + userId);
		}

	}

	@Override
	public boolean changePhoneNoUser(Long userId, String newPhoneNo) {
		Optional<User> optionalUser = userRepository.findById(userId);

		if (optionalUser.isPresent()) {
			User existingUser = optionalUser.get();

			if (!existingUser.getPhoneNo().equals(newPhoneNo)) {
				existingUser.setPhoneNo(newPhoneNo);

				// Update the phone number in UserIdentity entity if it exists
				Optional<UserIdentity> optionalUserIdentity = userIdentityRepository
						.findByEmail(existingUser.getEmail());

				if (optionalUserIdentity.isPresent()) {
					UserIdentity existingUserIdentity = optionalUserIdentity.get();
					existingUserIdentity.setPhoneNo(newPhoneNo);
					userIdentityRepository.save(existingUserIdentity); // Save the updated UserIdentity entity
					// Optionally, you can log here to confirm if the UserIdentity is being updated
					// properly
					System.out.println("UserIdentity phone number updated for user with ID: 965}"
							+ existingUserIdentity.getPhoneNo());
					userRepository.save(existingUser); // Save the updated user entity
					// Optionally, you can log here to confirm if the user is being updated properly
					System.out.println("Phone number updated for user with ID: 955" + existingUser.getPhoneNo());

				} else {
					// Log an error if UserIdentity is not found for the user
					System.out.println("UserIdentity not found with email: {}" + existingUser.getEmail());
				}

				return true; // Phone number successfully updated
			} else {
				// Log an error if the new phone number is the same as the existing one

				System.out.println("New phone number is the same as the existing phone number: {}" + newPhoneNo);

				System.out.println("New phone number is the same as the existing phone number: " + newPhoneNo);

				return false; // Phone number not updated
			}
		} else {
			// Log an error if the user is not found
			System.out.println("User not found with ID: {}" + userId);
			return false; // Phone number not updated
		}
	}

	@Override
	public boolean changeEmailUser(Long userId, String newEmail) {
		Optional<User> optionalUser = userRepository.findById(userId);

		if (optionalUser.isPresent()) {
			User existingUser = optionalUser.get();

			if (!existingUser.getPhoneNo().equals(newEmail)) {
				existingUser.setEmail(newEmail);

				// Update the phone number in UserIdentity entity if it exists
				Optional<UserIdentity> optionalUserIdentity = userIdentityRepository
						.findByPhoneNo(existingUser.getPhoneNo());

				if (optionalUserIdentity.isPresent()) {
					UserIdentity existingUserIdentity = optionalUserIdentity.get();
					existingUserIdentity.setEmail(newEmail);
					userIdentityRepository.save(existingUserIdentity); // Save the updated UserIdentity entity
					// Optionally, you can log here to confirm if the UserIdentity is being updated
					// properly
					System.out.println(
							"UserIdentity email updated for user with ID: 965}" + existingUserIdentity.getEmail());
					userRepository.save(existingUser); // Save the updated user entity
					// Optionally, you can log here to confirm if the user is being updated properly
					System.out.println("email updated for user with ID: 955" + existingUser.getEmail());

				} else {
					// Log an error if UserIdentity is not found for the user
					System.out.println("UserIdentity not found with phone number: {}" + existingUser.getPhoneNo());
				}

				return true; // Phone number successfully updated
			} else {
				// Log an error if the new phone number is the same as the existing one
				System.out.println("New phone number is the same as the existing phone number: {}" + newEmail);
				return false; // Phone number not updated
			}
		} else {
			// Log an error if the user is not found
			System.out.println("User not found with ID: {}" + userId);
			return false; // Phone number not updated
		}
	}

	// only cancel after Driver Accept the ride
	@Override
	@Transactional
	public String cancelBooking(Long bookingId, String reason) throws RazorpayException {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));
		PaymentActivity paymentActivity = paymentRepository.findByBookingBookingId(bookingId);

		Driver driver = driverRepository.findById(booking.getDriver().getDriverId())
				.orElseThrow(() -> new RuntimeException("Driver not found"));
		
		String driverMobileNumber = booking.getDriver().getPhoneNo();
		User user = booking.getUser();
		CancellationReason cancellationReason = new CancellationReason();
		cancellationReason.setReason(reason);
		cancellationReason.setUser(user);
		cancellationReason.setDriver(driver);
		cancellationReasonRepository.save(cancellationReason);
		cancellationServiceImpl.sendReasonDriver(driverMobileNumber, reason);

		if ("refunded".equals(paymentActivity.getOrderStatus())) {
			return "Payment already refunded";
		}
		String paymentId = paymentActivity.getPayementId();

		if (paymentId == null) {
			return "There is no payment with the provided ID";
		}

		com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(paymentId);
		int amount = razorpayPayment.get("amount");

		double charge = amount * 0.05;
		double refundableAmount = amount - charge;

		// Log important information like booking ID, payment ID, etc.

		Map<String, Object> options = new HashMap<>();
		options.put("amount", refundableAmount);
		JSONObject optionJson = new JSONObject(options);
		Refund payment = razorpayClient.payments.refund(paymentId, optionJson);

		booking.setRideOrderStatus(RideOrderStatus.CANCELLED);
		bookingRepository.save(booking);
		paymentActivity.setOrderStatus("refunded");
		paymentRepository.save(paymentActivity);
		driver.setStatus(Status.AVAILABLE);
		driverRepository.save(driver);

		return "amount refunded successully";
	}

	// only cancel after Driver Accept the ride
	@Override
	@Transactional
	public String cancelRentalBooking(Long rentalBookingId, String reason) throws RazorpayException {
		RentalBooking booking = rentalBookingRepository.findById(rentalBookingId)
				.orElseThrow(() -> new RuntimeException("Rental Booking not found"));
		PaymentActivity paymentActivity = paymentRepository.findByRentalBookingRentalBookingId(rentalBookingId);

		Driver driver = driverRepository.findById(booking.getDriver().getDriverId())
				.orElseThrow(() -> new RuntimeException("Driver not found"));
		
		String driverMobileNumber = booking.getDriver().getPhoneNo();
		User user = booking.getUser();
		CancellationReason cancellationReason = new CancellationReason();
		cancellationReason.setReason(reason);
		cancellationReason.setUser(user);
		cancellationReason.setDriver(driver);
		cancellationReasonRepository.save(cancellationReason);
		cancellationServiceImpl.sendReasonDriver(driverMobileNumber, reason);
		
		if ("refunded".equals(paymentActivity.getOrderStatus())) {

			return "Payment already refunded";
		} else {

			String paymentId = paymentActivity.getPayementId(); // Get payment ID associated with the booking

			if (paymentId == null) {
				return "There is no payment with the provided ID";
			}

			com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(paymentId);
			int amount = razorpayPayment.get("amount");
			double charge = amount * 0.05;
			double refundableAmount = amount - charge;

			Map<String, Object> option = new HashMap<>();

			option.put("amount", refundableAmount);
			JSONObject optionJson = new JSONObject(option);

			Refund payment = razorpayClient.payments.refund(paymentId, optionJson);

			booking.setRideOrderStatus(RideOrderStatus.CANCELLED);
			rentalBookingRepository.save(booking);

			driver.setStatus(Status.AVAILABLE);
			driverRepository.save(driver);

			paymentActivity.setOrderStatus("refunded");
			paymentRepository.save(paymentActivity);
			return "amount refunded successully";
		}

	}

	@Override
	public List<UserCourierPayment> getCourierBookingPaymentHistory(Long userId) {
		
		List<UserCourierPayment> paymentList = userCourierPaymentRepository.findByUser_UserId(userId);
		if(paymentList.isEmpty()) {
			throw new NoSuchElementException("No courier booking payment are available");
		}
		
		return paymentList;
	}

}
