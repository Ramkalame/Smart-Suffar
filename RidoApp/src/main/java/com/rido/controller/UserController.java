package com.rido.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.dto.BookingResponseDto;
import com.rido.dto.CancelRideRequestDto;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.ContactUsRequestDto;
import com.rido.dto.CourierBookingDto;
import com.rido.dto.CourierDto;
import com.rido.dto.DriverInfoDTO;
import com.rido.dto.DriverNotificationDto;
import com.rido.dto.ExtendedRental;
import com.rido.dto.HistoryDetailDto;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.PaymentActivityResponseDto;
import com.rido.dto.RentalBookingDto;
import com.rido.dto.ScheduleRideRequest;
import com.rido.dto.SenderReceiverInfoDto;
import com.rido.dto.UserAddressRequestDto;
import com.rido.dto.UserDetailsDto;
import com.rido.dto.UserEmailResponseDto;
import com.rido.dto.UserLocationChooseDto;
import com.rido.dto.UserPhoneRequestDto;
import com.rido.dto.UserSetNameRequestDto;
import com.rido.dto.UserUpdateRequestDto;
import com.rido.dto.VehicleDataDto;
import com.rido.dto.VehicleFareDTO;
import com.rido.dto.VerifyRequest;
import com.rido.entity.Booking;
import com.rido.entity.ContactUs;
import com.rido.entity.Driver;
import com.rido.entity.Feedback;
import com.rido.entity.Hub;
import com.rido.entity.HubLocation;
import com.rido.entity.ManageOtp;
import com.rido.entity.PaymentActivity;
import com.rido.entity.PaymentStatus;
import com.rido.entity.PromoCode;
import com.rido.entity.RazorPay;
import com.rido.entity.RentalBooking;
import com.rido.entity.Response;
import com.rido.entity.TimeDuration;
import com.rido.entity.User;
import com.rido.entity.UserCourierPayment;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.CourierBookingStatus;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.PromocodeType;
import com.rido.entity.enums.RideOrderStatus;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.BookingRepository;
import com.rido.repository.CancellationReasonRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubLocationRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.PaymentRepository;
import com.rido.repository.PromoCodeRepository;
import com.rido.repository.RentalBookingRepository;
import com.rido.repository.RentalUserLocationRepository;
import com.rido.repository.RoleRepository;
import com.rido.repository.UserRepository;
import com.rido.service.BookingService;
import com.rido.service.CancellationService;
import com.rido.service.ContactUsService;
import com.rido.service.CourierService;
import com.rido.service.DriverPaymentDetailService;
import com.rido.service.FeedbackService;
import com.rido.service.LocationService;
import com.rido.service.PromoCodeService;
import com.rido.service.UserService;
import com.rido.service.VehicleService;
import com.rido.service.impl.UserServiceImpl;
import com.rido.utils.ApiResponse;

@CrossOrigin(origins = { "http://10.0.2.2:8080", "http://localhost:3000" })
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CourierService courierService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private PromoCodeService promoCodeService;

	@Value("${bucketName}") // Assuming you have bucket name configured in properties file
	private String bucketName;

	@Autowired
	private AmazonS3 amazonS3;

	private static Gson gson = new Gson();

	private RazorpayClient client;

	private static final String SECRET_ID = "rzp_test_EkAN9YBYF0HgSD";
	private static final String SECRET_KEY = "1HCXIT2XLU1bQcHEKdiOiWyZ";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CancellationService cancellationService;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private ContactUsService contactUsService;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private CancellationReasonRepository cancellationReasonRepository;

	@Autowired
	private HubLocationRepository hubLocationRepository;

	@Autowired
	ManageOtpRepository manageOtpRepository;

	@Autowired
	LocationService locationService;

	@Autowired
	private DriverPaymentDetailService driverPaymentDetailService;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private RentalBookingRepository rentalBookingRepository;

	@Autowired
	private PromoCodeRepository promoCodeRepository;

	@Autowired
	private RentalUserLocationRepository rentalUserLocationRepository;

//	@Autowired
//	private HttpSession httpSession;

//	JALESHWARI
	@GetMapping("/getbyemail/{email}")
	public ResponseEntity<ResponseLogin> getUserByEmail(@PathVariable String email) {
		ResponseLogin response = userService.getByEmail(email);
		if (response != null) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

//        JALESHWARI
	@GetMapping("/getbyphoneno/{phoneno}")
	public ResponseEntity<ResponseLogin> getUserByPhoneNo(@PathVariable String phoneno) {
		ResponseLogin response = userService.getByPhoneno(phoneno);
		if (response != null) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// Create a DTO class for verifying OTP
	public static class VerifyOtpRequest {
		private String email;
		private String otp;
		private Long userId;

		// Getters and setters

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getOtp() {
			return otp;
		}

		public void setOtp(String otp) {
			this.otp = otp;
		}
	}

//	RISHI
	@PostMapping("/{userId}/setName")
	public ResponseEntity<String> setNames(@PathVariable Long userId,
			@RequestBody UserSetNameRequestDto userSetNameRequestDto) {
		boolean existingUser = userService.setName(userId, userSetNameRequestDto);

		if (existingUser) {
			return ResponseEntity.ok("Name set Successfully");
		} else {
			return ResponseEntity.badRequest().body("User not found");
		}
	}

//	RISHI
	@PostMapping("/{userId}/change-password")
	public ResponseEntity<String> changePassword(@PathVariable Long userId,
			@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
		boolean success = userService.changePassword(userId, changePasswordRequestDto);
		if (success) {
			return ResponseEntity.ok("Password changed successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to change password");
		}
	}

//	@PostMapping("/sign-with-phone")
//	public ResponseEntity<User> signWithPhone(@RequestBody User user) {
//
//		User signwithPhone = userService.signwithPhone(user);
//
//		// Return the saved UserPhoneResponseDto in the response
//		return ResponseEntity.ok(signwithPhone);
//	}

//  email verification
	@PostMapping("/sign-with-email")
	public UserEmailResponseDto signUpWithEmail(@RequestBody User user) {
		return userService.signwithEmail(user);
	}

	@GetMapping("/welcome")
	public String welcome() {
		return "<h1>Welcome to Rido</h1>";
	}

//JALESHWARI
	@PostMapping("/verify-email")
	public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest) {
		boolean isVerified = userService.verifyEmailOtp(verifyOtpRequest.getUserId(), verifyOtpRequest.getOtp());
		if (isVerified) {
			return new ResponseEntity<>("OTP verified successfully", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
		}
	}

//	Jaleshwari
	@PostMapping("/forget-password-phonenumber")
	public ResponseEntity<String> sendSmsforgetPasswordVerify(@RequestParam String phoneNo) {
		String result = userService.forgetPassword(phoneNo);
		return ResponseEntity.ok(result);
	}

//	JALESHWARI
	@PostMapping("/forget-password-verify/{userId}")
	public ResponseEntity<String> forgetPasswordVerify(@PathVariable Long userId,
			@RequestBody UserPhoneRequestDto request) {
		boolean verifySmsOtp = userService.forgetPasswordVerify(userId, request.getForgotOtp());
		if (verifySmsOtp) {
			return ResponseEntity.status(HttpStatus.OK).body("OTP verified successfully");

		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");

		}

	}

	@GetMapping("/getotp/{phoneNo}")
	public ResponseEntity<String> getotpByphone(@PathVariable String phoneNo) {
		String otp = userService.getotpByphone(phoneNo);
		System.out.println(otp);

		if (otp != null) {
			return new ResponseEntity<>("otp for phone number" + phoneNo + ":" + otp, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("User not found for phone number: " + phoneNo, HttpStatus.NOT_FOUND);
		}
	}

//	@PostMapping("/verify-otp")
//	public ResponseEntity<String> Verifyotp(@RequestParam String phoneNo, @RequestParam String userOTP) {
//
//		if (userService.verifyotp(phoneNo, userOTP)) {
//			return ResponseEntity.ok("OTP verification successful");
//		} else {
//			return ResponseEntity.badRequest().body("invalid otp");
//		}
//
//	}

//	RAM
	@PostMapping("/order_intialised/{userId}/{driverId}")
	@ResponseBody
	public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> data, @PathVariable Long userId,
			@PathVariable Long driverId) throws UserNotFoundException {

		int amount = Integer.parseInt(data.get("amount").toString());
		User existingUser = this.userRepository.findById(userId).orElseThrow(
				() -> new UserNotFoundException("the user with this " + userId + " id not present in the database"));

		Optional<Driver> findById = driverRepository.findById(driverId);
		Driver driver = findById.get();

		try {
			RazorpayClient client = new RazorpayClient("rzp_test_EkAN9YBYF0HgSD", "1HCXIT2XLU1bQcHEKdiOiWyZ");
			JSONObject orderRequest = new JSONObject();

			orderRequest.put("amount", amount * 100);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "receipt #1");

			Order order = client.orders.create(orderRequest);

			LocalDateTime now = LocalDateTime.now();

			// Define a DateTimeFormatter to format the date and time with AM/PM indicator
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm: a");
			String formattedDateTime = now.format(formatter);
			LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

			PaymentActivity payment = new PaymentActivity();
			payment.setAmount(order.get("amount") + "");
			payment.setReceipt(order.get("receipt"));
			payment.setOrderId(order.get("id"));
			payment.setOrderStatus(order.get("status").toString());
			payment.setPayementId(order.get(null));
			payment.setUser(existingUser);
			payment.setDriver(driver);
			payment.setLocalDatetime(parsedDateTime);
			paymentRepository.save(payment);

			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(order.toString());

		} catch (RazorpayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Something went wrong");
		}

	}

//	Ram
	@PostMapping("/payment_detail_update")
	public ResponseEntity<String> updatePaymentDetails(@RequestBody Map<String, Object> data) {

		PaymentActivity payment = this.paymentRepository.findByOrderId(data.get("orderId").toString());

		if (payment != null) {

			payment.setOrderStatus(data.get("status").toString());
			payment.setPayementId(data.get("paymentId").toString());
			this.paymentRepository.save(payment);
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(" payment details updated");
		} else {

			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Internal error ocurred");
		}

	}

	// Start Rahul Api

	// set Password
	@PostMapping("/setpassword/{userId}")
	public ResponseEntity<String> setPasswordUser(@PathVariable Long userId,
			@RequestBody PasswordRequestDto passwordRequest) {

		boolean setpassword = userService.setNewPassword(userId, passwordRequest);
		if (setpassword) {
			return new ResponseEntity<>("set Password Successfully", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Password not vaild ", HttpStatus.NOT_FOUND);
		}
	}

//	RAHUL
	// calculate price per km
	@GetMapping("/fares")
	public List<VehicleFareDTO> calculateFares(@RequestParam double distance) {
		return userService.calculateFares(distance);
	}

//	JALESHWARI - refactor done
	@PostMapping("/cancel-from-user")
	public ResponseEntity<String> cancelRideAndNotifyUser(@RequestBody CancelRideRequestDto cancelRideRequest) {
		if (cancelRideRequest.getReason() == null || cancelRideRequest.getReason().isEmpty()) {
			// Handle the case where reasons is null or empty
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reasons for cancellation are missing.");
		}

		String cancelRideAndNotifyUser = cancellationService.cancelRideAndNotifyUser(cancelRideRequest.getBookingId(),
				cancelRideRequest.getReason());

		if (cancelRideAndNotifyUser != null) {
			return ResponseEntity.status(HttpStatus.OK).body("Ride cancellation and notification sent successfully");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to cancel ride: " + cancelRideAndNotifyUser);
		}
	}

//	 RAM

	@GetMapping("/find-by-email")
	public ResponseEntity<ResponseLogin> getuserByEmailSetPhoneNo(@RequestParam String emailId,
			@RequestParam String phoneNumber) {

		User existingUser = userRepository.findByEmail(emailId).get();

		existingUser.setPhoneNo(phoneNumber);
		userRepository.save(existingUser);

		ResponseLogin reponseLogin = new ResponseLogin(emailId, phoneNumber, "Succesfully finded");

		return ResponseEntity.status(HttpStatus.ACCEPTED).body(reponseLogin);
	}

// By Furqan Sir
	@PostMapping("/calculate/{hubId}/{userId}")
	public ResponseEntity<Booking> bookRide(@PathVariable Long userId, @PathVariable Long hubId,

			@RequestBody Booking booking) {

		Booking calculatedBooking = bookingService.calculateBooking(booking, userId, hubId);

		return ResponseEntity.ok(calculatedBooking);
	}

	// Abhilasha
	@PostMapping("/feedbackpost")
	public Feedback Createfeedback(@RequestBody Feedback feedback) {

		return feedbackService.createfeedback(feedback);

	}

	// Abhilasha
	@GetMapping("/getAll")
	public List<Feedback> getAllFeedback() {
		return feedbackService.getAllFeedback();

	}

	// Abhilasha
	@GetMapping("/get/{feedbackid}")
	public Feedback getfeedbackById(@PathVariable Long feedbackid) {
		return feedbackService.getfeedbackById(feedbackid);

	}

//Rishi
	@PostMapping("/contactUs/sendMessage")
	public String saveContactUs(@RequestBody ContactUsRequestDto contactUsRequestDto) {
		contactUsService.saveContactUs(contactUsRequestDto);
		return "Message Send Successfully";
	}

//Rishi
	@GetMapping("/contactUs/allRecords")
	public List<ContactUs> getAllContactUs() {
		return contactUsService.getAllContactUs();
	}

// Jyoti
	@GetMapping("/orders/{id}/driver-info")
	public ResponseEntity<DriverInfoDTO> getDriverInfoByOrderId(@PathVariable Long id) {
		DriverInfoDTO driverInfoDTO = bookingService.getDriverInfoByOrderId(id);
		if (driverInfoDTO != null) {
			return ResponseEntity.ok(driverInfoDTO);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

// Furqan sir
	public UserController() throws RazorpayException {
		this.client = new RazorpayClient(SECRET_ID, SECRET_KEY);
	}

	@RequestMapping(value = "/createPayment", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> createOrder(@RequestBody PaymentActivity customer) {
		try {
			String orderId = createRazorPayOrder(customer.getAmount());
			RazorPay razorPay = getRazorPay(orderId, customer.getOrderId(), customer);
			return new ResponseEntity<String>(gson.toJson(razorPay), HttpStatus.OK);
		} catch (RazorpayException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Failed to create payment", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/generateQRCode", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> generateQRCode(@RequestParam String orderId) {
		try {
			String qrCodeUrl = generateQRCodeUrl(orderId);
			return new ResponseEntity<>(qrCodeUrl, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to generate QR code", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/verifyPayment", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> verifyPayment(@RequestBody RazorPay razorPay) {
		try {
			// Mock payment verification logic
			boolean paymentSuccessful = true; // Simulate payment success
			PaymentStatus paymentStatus = new PaymentStatus();
			paymentStatus.setPaymentId(razorPay.getPaymentId());
			paymentStatus.setPaid(paymentSuccessful);
			return new ResponseEntity<>(gson.toJson(paymentStatus), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to verify payment", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/paid", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> markAsPaid(@RequestParam String orderId) {
		try {
			// Update order status as paid in your system
			// Return success response
			return ResponseEntity.ok("Order marked as paid");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to mark order as paid", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/attempted", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> markAsAttempted(@RequestParam String orderId) {
		try {
			// Update order status as attempted in your system
			// Return success response
			return ResponseEntity.ok("Order marked as attempted");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to mark order as attempted", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String generateQRCodeUrl(String orderId) {
		// Logic to generate QR code URL for the given order ID
		return "" + orderId;
	}

	private Response getResponse(RazorPay razorPay, int statusCode) {
		Response response = new Response();
		response.setStatusCode(statusCode);
		response.setRazorPay(razorPay);
		return response;
	}

	private RazorPay getRazorPay(String paymentId, String orderId, PaymentActivity customer1) {

		User customer = paymentRepository.findByUser_UserId(customer1.getUser());

		RazorPay razorPay = new RazorPay();
		razorPay.setPaymentId(paymentId); // Set the paymentId
		razorPay.setRazorpayOrderId(orderId); // Set the orderId
		razorPay.setApplicationFee(convertRupeeToPaise(customer1.getAmount()));
		razorPay.setCustomerName(customer.getUsername());
		razorPay.setCustomerEmail(customer.getEmail());
		razorPay.setMerchantName("Test");
		razorPay.setPurchaseDescription("TEST PURCHASES");
		razorPay.setSecretKey(SECRET_ID);
		razorPay.setImageURL("/logo");
		razorPay.setTheme("#F37254");
		razorPay.setNotes("notes" + orderId);

		paymentRepository.save(customer1);
		return razorPay;
	}

	private String createRazorPayOrder(String amount) throws RazorpayException {
		JSONObject options = new JSONObject();
		options.put("amount", convertRupeeToPaise(amount));
		options.put("currency", "INR");
		options.put("receipt", "txn_123456");
		options.put("payment_capture", 1); // Auto Capture enabled
		return client.orders.create(options).get("id");
	}

	private String convertRupeeToPaise(String paise) {
		BigDecimal b = new BigDecimal(paise);
		BigDecimal value = b.multiply(new BigDecimal("100"));
		return value.setScale(0, RoundingMode.UP).toString();
	}

	@PutMapping("/editprofile/{id}")
	public ResponseEntity<String> updateUserProfile(@PathVariable Long id,
			@RequestParam(name = "profileimage",required = false) MultipartFile file,
			@RequestParam(name="UserUpdateRequestDto",required = false) String userDataJson) throws Exception  {
		
		try {
			// Convert JSON to UserUpdateRequestDto object
			ObjectMapper objectMapper = new ObjectMapper();
			UserUpdateRequestDto userDataDto = objectMapper.readValue(userDataJson, UserUpdateRequestDto.class);

			// Generate a unique file name for the image
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

			// Convert MultipartFile to File
			File convertedFile = convertMultiPartToFile(file, fileName);

			// Upload the file to S3 bucket
			String s3Url = uploadFileToS3(convertedFile, fileName);

			// Call service method to update user profile with S3 URL
			String response = userService.updateUserProfile(id, userDataDto, s3Url);

			System.out.println("userData=" + userDataDto);
			System.out.println("file=" + file);

			return ResponseEntity.ok(response);
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception appropriately (e.g., log it)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error in updating user profile");
		}
	}
	

	private File convertMultiPartToFile(MultipartFile file, String fileName) throws FileNotFoundException, IOException {
		File convertedFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		}
		return convertedFile;
	}

	private String uploadFileToS3(File file, String fileName) {
		// Upload the file to S3 bucket
		amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file));
		// Return the S3 URL of the uploaded file
		return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
	}

	private String getResponseJson(RazorPay razorPay, int statusCode) {
		Response response = new Response();
		response.setStatusCode(statusCode);
		response.setRazorPay(razorPay);
		return response.toString();
	}

	private RazorPay getRazorPay(String orderId, Long userId) {

		User customer = userRepository.findById(userId).get();
		PaymentActivity customerInfo = paymentRepository.findByUser_UserId(userId).get();
		RazorPay razorPay = new RazorPay();
		razorPay.setApplicationFee(convertRupeeToPaise(customerInfo.getAmount()));
		razorPay.setCustomerName(customer.getName());
		razorPay.setCustomerEmail(customer.getEmail());
		razorPay.setMerchantName("Test");
		razorPay.setPurchaseDescription("TEST PURCHASES");
		razorPay.setRazorpayOrderId(orderId);
		razorPay.setSecretKey(SECRET_ID);
		razorPay.setImageURL("/logo");
		razorPay.setTheme("#F37254");
		razorPay.setNotes("notes" + orderId);
		return razorPay;
	}

	@PostMapping("/userAddress/{userId}")
	public ResponseEntity<String> userSelectedAddress(@PathVariable Long userId,
			@RequestBody UserAddressRequestDto useraddress) {
		String useraddres = userService.userPickupAddress(userId, useraddress);
		return new ResponseEntity<>(useraddres, HttpStatus.CREATED);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserLocationChooseDto> getUserLocation(@PathVariable Long userId)
			throws UserNotFoundException {

		UserLocationChooseDto userLocation = userService.userLocation(userId);

		return ResponseEntity.ok(userLocation);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<String> updateLocation(@PathVariable Long userId,
			@RequestBody UserLocationChooseDto userlocation) throws UserNotFoundException {

		String userupdateaddress = userService.updateUserLocation(userId, userlocation);

		return ResponseEntity.ok(userupdateaddress);
	}

	@PostMapping("/book-car")
	public ResponseEntity<String> bookCar(@RequestParam("userId") Long userId,
			@RequestParam("userLatitude") double userLatitude, @RequestParam("userLongitude") double userLongitude) {
		// Find all hubs
		List<Hub> hubs = hubRepository.findAll();

		// Initialize variables for tracking the nearest hub
		Hub nearestHub = null;
		double minDistance = Double.MAX_VALUE;

		// Calculate distance from user's location to each hub
		for (Hub hub : hubs) {
			HubLocation hubLocation = hubLocationRepository.findByHub(hub);
			System.out.println(hubLocation + "location");
			System.out.println(hubLocation.getHubLatitude() + "werty");
			double distance = calculateDistance(userLatitude, userLongitude, hubLocation.getHubLatitude(),
					hubLocation.getHubLongitude());
			if (distance < minDistance) {
				minDistance = distance;
				nearestHub = hub;
			}
		}

		// Book the car and get nearby drivers from the nearest hub
		HubLocation nearestHubLocation = hubLocationRepository.findByHub(nearestHub);
//		Hub nearestHub = null;
		List<Driver> nearbyDrivers = userService.bookCar(userId, nearestHubLocation.getHubLatitude(),
				nearestHubLocation.getHubLongitude());

		if (nearbyDrivers.isEmpty()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No nearby drivers available.");
		}

		// Build the response message
		StringBuilder responseMessage = new StringBuilder("Car booking request sent to the nearest hub: ");
//		responseMessage.append(nearestHub.getHubName()).append(".\n");
		responseMessage.append(nearestHub.getManagerName()).append(".\n");
		responseMessage.append("Nearby Drivers:\n");
		for (Driver driver : nearbyDrivers) {
			responseMessage.append(driver.toString()).append("\n");
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage.toString());
	}

	private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		final int R = 6371; // Radius of the Earth in kilometers

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c; // Distance in kilometers

		return distance;
	}

	@GetMapping("/History/completed/{userId}")
	public List<HistoryDetailDto> getCompletedOrdersForUser(@PathVariable Long userId) throws UserNotFoundException {
		// Step 1: Retrieve all completed ride statuses
		List<Booking> completedOrders = bookingRepository.findByRideOrderStatus(RideOrderStatus.COMPLETE);

		// Step 2: Filter completed ride statuses for the desired user ID
		List<Booking> userCompletedOrders = completedOrders.stream()
				.filter(order -> order.getUser().getUserId().equals(userId)).collect(Collectors.toList());
		System.out.println("userCompletedOrders=" + userCompletedOrders);

		if (userCompletedOrders.isEmpty()) {
			throw new UserNotFoundException("User with ID " + userId + " not found");
		}

		Collections.reverse(userCompletedOrders);

		// Step 3: Map the filtered list to HistoryDetailDto objects
		return userCompletedOrders.stream()
				.map(order -> new HistoryDetailDto(order.getDriver().getName(),
						order.getDriver().getVehicle().getVehicleName(), null, // Assuming Order entity has startTime
																				// field
						order.getRideOrderStatus())) // Assuming status field is same as rideStatus
				.collect(Collectors.toList());
	}

	@GetMapping("/History/upcoming/{userId}")
	public List<HistoryDetailDto> getUpcomingOrdersForUser(@PathVariable Long userId) throws UserNotFoundException {
		// Step 1: Retrieve all bookings with status "Booked"
		List<Booking> bookedOrders = bookingRepository.findByRideOrderStatus(RideOrderStatus.BOOKED);

		// Step 2: Filter bookings for the desired user ID from the list of booked
		// orders
		List<Booking> userBookedOrders = bookedOrders.stream()
				.filter(order -> order.getUser().getUserId().equals(userId)).collect(Collectors.toList());
		if (userBookedOrders.isEmpty()) {
			throw new UserNotFoundException("User with ID " + userId + " not found");
		}

		Collections.reverse(userBookedOrders);

		// Step 3: Map the filtered list to HistoryDetailDto objects
		return userBookedOrders.stream().map(order -> createHistoryDetailDto(order)).collect(Collectors.toList());
	}

	private HistoryDetailDto createHistoryDetailDto(Booking booking) {
		HistoryDetailDto historyDetailDto = new HistoryDetailDto();

		Driver driver = booking.getDriver();
		if (driver != null) {
			historyDetailDto.setDriverName(driver.getName());
			if (driver.getVehicle() != null) {
				historyDetailDto.setVehicleName(driver.getVehicle().getVehicleName());
			}
		}

		TimeDuration timeDuration = booking.getTimeDuration();
		if (timeDuration != null) {
			historyDetailDto.setStartTime(timeDuration.getStartDateTime());
		}

		historyDetailDto.setRideOrderStatus(booking.getRideOrderStatus());

		return historyDetailDto;
	}



//	@GetMapping("/History/cancel/{userId}")
//	public List<HistoryDetailDto> getCamcelOrdersForUser(@PathVariable Long userId) throws UserNotFoundException {
//		// Step 1: Retrieve all cancelled ride orders
//		List<Booking> cancelledOrders = bookingRepository.findByRideOrderStatus(RideOrderStatus.CANCELLED);
//
//		// Step 2: Filter cancelled ride orders for the desired user ID
//		List<Booking> userCancelledOrders = cancelledOrders.stream()
//				.filter(order -> order.getUser().getUserId().equals(userId)).collect(Collectors.toList());
//		if (userCancelledOrders.isEmpty()) {
//			throw new UserNotFoundException("User with ID " + userId + " not found");
//		}
//
//		Collections.reverse(userCancelledOrders);
//
//		// Step 3: Map the filtered list to HistoryDetailDto objects
//		List<HistoryDetailDto> historyDetailsList = userCancelledOrders.stream()
//				.map(order -> new HistoryDetailDto(order.getDriver().getName(),
//						order.getDriver().getVehicle().getVehicleName(), null, // Assuming Order entity has startTime
//																				// field
//						order.getRideOrderStatus())) // Assuming status field is same as rideStatus
//				.collect(Collectors.toList());
//
//		return historyDetailsList;
//	}
	
	
	@GetMapping("/History/cancel/{userId}")
	public ResponseEntity<List<HistoryDetailDto>> getCancelOrdersForUser(@PathVariable Long userId) {
	    // Step 1: Retrieve all cancelled ride orders
	    List<Booking> cancelledOrders = bookingRepository.findByRideOrderStatus(RideOrderStatus.CANCELLED);

	    // Step 2: Filter cancelled ride orders for the desired user ID
	    List<Booking> userCancelledOrders = cancelledOrders.stream()
	            .filter(order -> order.getUser().getUserId().equals(userId)).collect(Collectors.toList());

	    // Check if any cancelled orders were found for the given user ID
	    if (userCancelledOrders.isEmpty()) {
	        // Handle the error scenario with 404 status
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found");
	    } else {
	        // If there are cancelled orders for the given user ID, proceed to map the filtered list to HistoryDetailDto objects
	        Collections.reverse(userCancelledOrders);

	        List<HistoryDetailDto> historyDetailsList = userCancelledOrders.stream()
	                .map(order -> new HistoryDetailDto(order.getDriver().getName(),
	                        order.getDriver().getVehicle().getVehicleName(), null, // Assuming Order entity has startTime
	                                                                               // field
	                        order.getRideOrderStatus())) // Assuming status field is same as rideStatus
	                .collect(Collectors.toList());

	        return new ResponseEntity<>(historyDetailsList, HttpStatus.OK);
	    }
	}
//  jaleshwari 31/03/24
	@GetMapping("/bookings/{bookingId}")
	public ResponseEntity<BookingResponseDto> getBookingOrder(@PathVariable Long bookingId) {
		try {
			BookingResponseDto bookingResponseDto = bookingService.getBookingOrder(bookingId);
			return ResponseEntity.ok(bookingResponseDto);
		} catch (RuntimeException e) {
			// Handle the case when booking is not found
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PostMapping("/payment_update")
	public ResponseEntity<String> updatePayment(@RequestBody Map<String, Object> data) {

		PaymentActivity payment = this.paymentRepository.findByOrderId(data.get("orderId").toString());

		if (payment != null) {

			payment.setOrderStatus(data.get("status").toString());

			this.paymentRepository.save(payment);
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(" payment details updated");
		} else {

			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Internal error ocurred");
		}

	}

//for update phone number
	@PostMapping("/update-phoneNumberSendOtp/{userId}")
	public ResponseEntity<String> updatephoneNumberSendOtp(@PathVariable Long userId,
			@RequestBody VerifyRequest request) {
		try {
			Optional<ManageOtp> manageOtp = manageOtpRepository.findByUser_UserId(userId);

			if (manageOtp.isPresent()) {
				String otp = locationService.generateRandomOtp();

				ManageOtp manageOtp2 = manageOtp.get();
				manageOtp2.setUpdateOtp(otp);

				manageOtpRepository.save(manageOtp2);

				locationService.sendVerificationCode(request.getPhoneNo(), otp);

				return ResponseEntity.status(HttpStatus.OK).body("Successfully sent OTP");
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send OTP");
		}
	}

//for update phone number
	@PutMapping("/update-verify-phoneno/{userId}")
	public ResponseEntity<String> verifyPhoneNoOtp(@PathVariable Long userId,
			@RequestBody Map<String, String> request) {
		Optional<ManageOtp> manageOtp = manageOtpRepository.findByUser_UserId(userId);

		if (manageOtp.isPresent()) {
			ManageOtp manageOtp2 = manageOtp.get();
			String updateOtp = manageOtp2.getUpdateOtp();

			if (updateOtp.equals(request.get("updateOtp"))) {
				String newPhoneNo = request.get("newPhoneNo");
				boolean success = userService.changePhoneNoUser(userId, newPhoneNo);
				if (success) {
					return ResponseEntity.ok("Phone number updated successfully.");
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body("Failed to update phone number.");
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Failed to verify phone number. Incorrect OTP.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to verify phone number. OTP not found.");
		}
	}

	@PutMapping("/update-phone-no/{userId}")
	public ResponseEntity<String> changePhoneNoDriver(@PathVariable Long userId,
			@RequestBody Map<String, String> requestBody) {
		if (requestBody.containsKey("newPhoneNo")) {
			try {
				String newPhoneNo = requestBody.get("newPhoneNo");
				userService.changePhoneNoUser(userId, newPhoneNo);
				return ResponseEntity.ok("Phone number updated successfully.");
			} catch (RuntimeException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		} else {
			return ResponseEntity.badRequest().body("Missing 'newPhoneNo' field in request body.");
		}
	}

	// jyoti
	@PostMapping("/update-emailSendOtp/{userId}")
	public ResponseEntity<String> updateEmailId(@PathVariable Long userId, @RequestBody VerifyRequest request) {
		try {
			Optional<ManageOtp> manageOtp = manageOtpRepository.findByUser_UserId(userId);

			if (manageOtp.isPresent()) {
				String otp = locationService.generateRandomOtp();

				ManageOtp manageOtp2 = manageOtp.get();
				manageOtp2.setUpdateOtp(otp);

				manageOtpRepository.save(manageOtp2);

				userServiceImpl.sendOtpByEmail(request.getEmail(), otp);

				return ResponseEntity.status(HttpStatus.OK).body("Successfully sent OTP");
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send OTP");
		}
	}

	// jyoti
	@PutMapping("/update-verify-email/{userId}")
	public ResponseEntity<String> verifyEmailOtp(@PathVariable Long userId, @RequestBody Map<String, String> request) {
		Optional<ManageOtp> manageOtp = manageOtpRepository.findByUser_UserId(userId);

		if (manageOtp.isPresent()) {
			ManageOtp manageOtp2 = manageOtp.get();
			String updateOtp = manageOtp2.getUpdateOtp();

			if (updateOtp.equals(request.get("updateOtp"))) {
				String newEmail = request.get("newEmail");
				boolean success = userService.changeEmailUser(userId, newEmail);
				if (success) {
					return ResponseEntity.ok("email updated successfully.");
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update to email.");
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to verify email. Incorrect OTP.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to verify email. OTP not found.");
		}
	}

// Rishi
	@GetMapping("/userDetails/{userId}")
	public ResponseEntity<UserDetailsDto> getUserDetails(@PathVariable Long userId) throws UserNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found for this id :: " + userId));

		UserDetailsDto userDetails = new UserDetailsDto();
		userDetails.setName(user.getName());
		userDetails.setPhoneNo(user.getPhoneNo());
		userDetails.setAlternativeNo(user.getAlternativeNo());
		userDetails.setEmail(user.getEmail());
		userDetails.setGender(user.getGender());
		userDetails.setDob(user.getDob());
		userDetails.setImageProfileLink(user.getImageProfileLink());

		return ResponseEntity.ok(userDetails);
	}

// Rishi
	@PostMapping("/rentalBooking/{userId}")
	public ResponseEntity<RentalBookingDto> bookRental(@RequestBody RentalBooking request, @PathVariable Long userId) {
		RentalBookingDto rentalBooking = bookingService.bookRental(request, userId);
		return ResponseEntity.ok(rentalBooking);
	}

	// Rishi
	@PostMapping("/rentalApplyPromocode/{rentalBookingId}")
	public ResponseEntity<RentalBooking> applyPromoCode(@PathVariable Long rentalBookingId,
			@RequestParam String promoCode) {
		RentalBooking updatedBooking = bookingService.applyPromoCode(rentalBookingId, promoCode);
		return ResponseEntity.ok(updatedBooking);
	}

	// Rishi
	@PostMapping("/rentalRemovePromocode/{rentalBookingId}")
	public ResponseEntity<RentalBooking> removePromoCode(@PathVariable Long rentalBookingId) {
		RentalBooking updatedBooking = bookingService.removePromoCode(rentalBookingId);
		return ResponseEntity.ok(updatedBooking);
	}

	// Rishi
	@PostMapping("/createPaymentOrderForRentalBooking")
	public ResponseEntity<PaymentActivity> createPaymentOrderForRentalBooking(
			@RequestBody RentalBooking rentalBooking) {
		try {
			PaymentActivity response = bookingService.paymentOrderCreateForRentalBooking(rentalBooking);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

//	Rishi
	@PostMapping("/payment_detail_update_rental")
	public ResponseEntity<String> updatePaymentDetailsForRentalBooking(@RequestBody Map<String, Object> data) {

		PaymentActivity payment = this.paymentRepository.findByOrderId(data.get("orderId").toString());

		if (payment != null) {

			Optional<RentalBooking> rentalBookingFindById = rentalBookingRepository
					.findById(payment.getRentalBooking().getRentalBookingId());
			RentalBooking rentalBooking = rentalBookingFindById.get();
			rentalBooking.setIsConfirm(CourierBookingStatus.CONFIRMED);

			payment.setOrderStatus(data.get("status").toString());
			payment.setPayementId(data.get("paymentId").toString());
			rentalBookingRepository.save(rentalBooking);
			this.paymentRepository.save(payment);
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(" payment details updated");
		} else {

			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Internal error ocurred");
		}

	}

//	Rishi
	@PostMapping("/deleteRentalBooking")
	public ResponseEntity<String> deleteRentalBooking(@RequestBody Map<String, Object> data) {

		PaymentActivity payment = paymentRepository.findByOrderId(data.get("orderId").toString());
		if (payment != null) {
			paymentRepository.delete(payment);
			rentalBookingRepository.delete(payment.getRentalBooking());
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body("Payment Failed");
		} else {
			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Internal error ocurred");
		}
	}

	// Rishi
	@PostMapping("/extendRentalBooking/{rentalBookingId}")
	public ResponseEntity<RentalBooking> extendRentalBooking(@PathVariable Long rentalBookingId,
			@RequestBody ExtendedRental extendedRental) {
		RentalBooking extendedBooking = bookingService.extendRental(rentalBookingId, extendedRental);
		return ResponseEntity.ok(extendedBooking);
	}

	// Rishi
	@PostMapping("/createPaymentOrderForExtendRentalBooking")
	public ResponseEntity<PaymentActivity> createPaymentOrderForExtendRentalBooking(
			@RequestBody RentalBooking rentalBooking) {
		try {
			PaymentActivity response = bookingService.paymentOrderCreateForExtendRentalBooking(rentalBooking);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// Rishi
	@GetMapping("/standardVehiclesList")
	public List<VehicleDataDto> getStandardVehiclesList() {
		return vehicleService.getStandardRentalVehicles();
	}

	// Rishi
	@GetMapping("/premiumVehiclesList")
	public List<VehicleDataDto> getPremiumVehiclesList() {
		return vehicleService.getPremiumRentalVehicles();
	}

	// Rishi
	@GetMapping("/getVehicleById/{vehicleId}")
	public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long vehicleId) {
		Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
		if (vehicle != null) {
			return ResponseEntity.ok(vehicle);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

//Ram
	@PostMapping("/cancelBooking/{bookingId}")
	public ResponseEntity<Object> cancelBooking(@PathVariable Long bookingId, @RequestBody String reason) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.cancelBooking(bookingId, reason));
		} catch (RazorpayException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to cancel booking: " + e.getMessage());
		}
	}

//Rishi
	@PostMapping("/cancelRentalBooking/{rentalBookingId}")
	public ResponseEntity<Object> cancelRentalBooking(@PathVariable Long rentalBookingId, @RequestBody String reason) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(userService.cancelRentalBooking(rentalBookingId, reason));
		} catch (RazorpayException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to cancel rental booking: " + e.getMessage());
		}
	}

	// AADARSH KAUSHIK
	// http://localhost:8080/user/{user-id}/select
	@PostMapping("/courier/{user-id}/select")
	public ResponseEntity<?> getVehiclesAndPrice(@PathVariable(name = "user-id") Long userId,
			@RequestBody SenderReceiverInfoDto senderReceiverInfoDto) {
		try {
			return ResponseEntity.ok(courierService.availableVehicle(userId, senderReceiverInfoDto));
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error while fetching the vehicle list");
		}
	}
	// AADARSH KAUSHIK

	// http://localhost:8080/user/courier/no-promo-code
	@PostMapping("/courier/no-promo-code")
	public ResponseEntity<?> createOrderWithoutPromoCode(@RequestBody CourierDto courierDto) {
		try {
			return new ResponseEntity<>(courierService.generateBookingInvoiceWithoutPromoCode(courierDto),
					HttpStatus.CREATED);
		} catch (Exception exception) {
			exception.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while generating invoice");
		}
	}
	// AADARSH KAUSHIK

	// http://localhost:8080/user/courier/promo-code/{courierId}/{promoCode}
	@PutMapping("/courier/promo-code/{courierBookingId}/{promoCode}")
	public ResponseEntity<?> createOrderWithPromoCode(@PathVariable String promoCode,
			@PathVariable Long courierBookingId) {
		try {
			return new ResponseEntity<>(courierService.generateBookingInvoiceWithPromoCode(promoCode, courierBookingId),
					HttpStatus.OK);
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error while applying the promocode, try again");
		}
	}
	// AADARSH KAUSHIK

	// http://localhost:8080/user/remove-promo/{id}
	@PutMapping("/courier/remove-promo/{id}")
	public ResponseEntity<CourierBookingDto> removeBookingPromoCode(@PathVariable Long id) {
		return new ResponseEntity<>(courierService.removePromoCodeFromCourierOrder(id), HttpStatus.OK);
	}

	// jyoti
	@PutMapping("/remove-Promo-code/{bookingId}")
	public ResponseEntity<Booking> removePromoCodeforbooking(@PathVariable Long bookingId) {
		Booking updatedBooking = bookingService.removePromoCodeForShedule(bookingId);
		return ResponseEntity.ok(updatedBooking);
	}

	// jyoti
	@PutMapping("/apply-promo-code/{bookingId}")
	public ResponseEntity<Booking> applyPromoCode23(@PathVariable Long bookingId, @RequestParam String promoCode) {
		Booking updatedBooking = bookingService.applyPromoCodeForShedule(bookingId, promoCode);
		return ResponseEntity.ok(updatedBooking);
	}

//Rishi
	@PostMapping("/extendRentalUpdatePayment")
	public ResponseEntity<String> extendRentalUpdatePayment(@RequestBody Map<String, Object> data) {

		PaymentActivity payment = this.paymentRepository.findByOrderId(data.get("orderId").toString());

		if (payment != null) {

			RentalBooking booking = rentalBookingRepository.findById(payment.getRentalBooking().getRentalBookingId())
					.orElseThrow(() -> new RuntimeException("Rental Booking not found"));

			booking.setHours(booking.getHours() + booking.getExtraHours());
			booking.setDistance(booking.getDistance() + booking.getExtraDistance());
			booking.setTotalAmount(booking.getTotalAmount().add(booking.getExtraAmount()));

//	        // Reset extra hours, extra distance, and extra amount
//	        booking.setExtraHours(0);
//	        booking.setExtraDistance(0);
//	        booking.setExtraAmount(BigDecimal.ZERO);

			rentalBookingRepository.save(booking);

			payment.setOrderStatus(data.get("status").toString());

			this.paymentRepository.save(payment);
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(" payment details updated");
		} else {

			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Internal error ocurred");
		}

	}

//Rishi
//	@GetMapping("/getPromoCodeList")
//	public List<PromoCode> getPromoCodeList() {
//		return promoCodeRepository.findAll();
//	}
	@GetMapping("/getPromoCodeList")
	public List<PromoCode> getRentalPromoCodeList() {
		return promoCodeRepository.findAllByPromocodeType(PromocodeType.Rental);
	}

	// jyoti
	@PostMapping("/schedule-ride/{userId}")
	public ResponseEntity<BookingResponseDto> scheduleRide(@PathVariable long userId,
			@RequestBody ScheduleRideRequest request) {
		BookingResponseDto responseDto;

		if (request.getVehicleType() == DriverAndVehicleType.TWO_WHEELER) {
			responseDto = bookingService.scheduleRide1(userId, request);
		} else if (request.getVehicleType() == DriverAndVehicleType.FOUR_WHEELER) {
			responseDto = bookingService.scheduleRide2(userId, request);
		} else {
			// Handle invalid or unsupported vehicle type
			// For example:
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(responseDto);
	}

	@GetMapping("/courier-booking-history/{userId}")
	public ResponseEntity<Object> getCourierBookingPaymentHistory(@PathVariable Long userId) {

		try {

			List<UserCourierPayment> paymentList = userService.getCourierBookingPaymentHistory(userId);
			ApiResponse<Object> response = new ApiResponse<Object>(paymentList, HttpStatus.OK, true,
					"Here is a couriere booking payment list");
			return new ResponseEntity<Object>(response, HttpStatus.OK);

		} catch (NoSuchElementException ex) {
			ApiResponse<Object> response = new ApiResponse<Object>(null, HttpStatus.NOT_FOUND, false, ex.getMessage());
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

	}

	// AADARSH KAUSHIK
	@PostMapping("/courier/{user-id}/select-two-wheeler")
	public ResponseEntity<?> getTwoWheeler(@PathVariable(name = "user-id") Long userId,
			@RequestBody SenderReceiverInfoDto senderReceiverInfoDto) {
		return ResponseEntity.ok(courierService.getAllTwoWheelers(userId, senderReceiverInfoDto));
	}

	@DeleteMapping("/delete_booking/{bookingId}")
	public ResponseEntity<String> deleteBooking(@PathVariable long bookingId) {
		try {
			bookingService.deleteBookingById(bookingId);
			return ResponseEntity.ok("Booking deleted successfully");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// jyoti - when user will payment then driver will get notification
	@PostMapping("/create-payment-order")
	public ResponseEntity<PaymentActivityResponseDto> createPaymentOrder(@RequestBody Booking booking) {
		try {
			PaymentActivityResponseDto response = bookingService.paymentOrderCreateForSheduleBooking1(booking);
			if (response != null) {
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
// Jyoti
	@PostMapping("/send_notification_to_driver")
	public DriverNotificationDto sendNotificationToDriver(@RequestParam Long userId, @RequestParam Long bookingId) {
		// Call the service method to send notification
		DriverNotificationDto responseMessage = bookingService.sendNotificationToDriver23(userId, bookingId);

		// Check if the responseMessage is null or contains an error
		if (responseMessage == null) {
			// Handle the case where responseMessage is null
			responseMessage = new DriverNotificationDto();
			responseMessage.setStatus("Failure");
			responseMessage.setMessage("Notification sending failed: response is null");
		} else if ("error".equals(responseMessage.getStatus())) {
			// Handle the case where the response indicates an error
			responseMessage.setMessage("Notification sending failed: " + responseMessage.getMessage());
		} else {
			// Handle the successful case
			responseMessage.setStatus("Success");
			responseMessage.setMessage("Notification sent successfully");
		}
		return responseMessage;
	}

// Rishi
	@PostMapping("/send_notification_to_driver_for_rental/{rentalBookingId}")
	public DriverNotificationDto sendNotificationToDriverForRental(@PathVariable Long rentalBookingId) {

		DriverNotificationDto responseMessage = bookingService.sendNotificationToDriverForRental(rentalBookingId);

		if (responseMessage == null) {
			responseMessage = new DriverNotificationDto();
			responseMessage.setStatus("Failure");
			responseMessage.setMessage("Notification sending failed: response is null");
		} else if ("error".equals(responseMessage.getStatus())) {
			responseMessage.setMessage("Notification sending failed: " + responseMessage.getMessage());
		} else {
			responseMessage.setStatus("Success");
			responseMessage.setMessage("Notification sent successfully");
		}
		return responseMessage;
	}

}