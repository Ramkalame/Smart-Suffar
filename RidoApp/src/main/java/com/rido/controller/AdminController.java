package com.rido.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rido.Exceptions.BusinessException;
import com.rido.Exceptions.ErrorResponse;
import com.rido.Exceptions.HubNotFoundException;
import com.rido.Exceptions.ResourceNotFoundException;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.dto.AdminDataDto;
import com.rido.dto.AdminProfileEditDto;
import com.rido.dto.BookedOrderDTO;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.CourierBookingDto1;
import com.rido.dto.CourierEbikeDto;
import com.rido.dto.DriverApproveRequestDto;
import com.rido.dto.DriveracceptpaymentDto;
import com.rido.dto.HubDTO;
import com.rido.dto.HubListDto;
import com.rido.dto.HubManagerDto;
import com.rido.dto.HubManagerPaymentHistoryDto;
import com.rido.dto.HubPaymentHistoryDto;
import com.rido.dto.HubPaymentRequestDto;
import com.rido.dto.ListOfAssignVehiclesDto;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.ProfileDto;
import com.rido.dto.TotalBookingDto;
import com.rido.dto.UserPaymentHistoryDto;
import com.rido.dto.VehicleCourierEbikeDto;
import com.rido.dto.VehicleDataDto;
import com.rido.entity.Admin;
import com.rido.entity.Booking;
import com.rido.entity.CancellationReason;
import com.rido.entity.CarRepair;
import com.rido.entity.CourierEbike;
import com.rido.entity.Driver;
import com.rido.entity.DriverPaymentDetail;
import com.rido.entity.Hub;
import com.rido.entity.HubPayment;
import com.rido.entity.ManageOtp;
import com.rido.entity.PromoCode;
import com.rido.entity.ReturnCar;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.CarRepairStatus;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.MaintenanceApprovalStatus;
import com.rido.entity.enums.RideOrderStatus;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.AdminRepository;
import com.rido.repository.BookingRepository;
import com.rido.repository.CarRepairRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.PromoCodeRepository;
import com.rido.repository.ReturnCarRepository;
import com.rido.repository.RoleRepository;
import com.rido.repository.UserRepository;
import com.rido.repository.VehicleRepository;
import com.rido.service.AdminService;
import com.rido.service.BookingService;
import com.rido.service.CancellationService;
import com.rido.service.DriverPaymentDetailService;
import com.rido.service.DriverService;
import com.rido.service.HubPaymentService;
import com.rido.service.HubService;
import com.rido.service.UserService;
import com.rido.service.VehicleService;
import com.rido.service.impl.AdminServiceImpl;
import com.rido.service.impl.BookingServiceImpl;
import com.rido.service.impl.UserServiceImpl;
import com.rido.utils.ApiResponse;

@CrossOrigin(origins = { "http://10.0.2.2:8080", "http://localhost:3000" })
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AdminService adminService;

	@Autowired
	private CancellationService cancellationService;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private DriverService driverService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private BookingRepository r;

	@Autowired
	private ManageOtpRepository manageOtpRepository;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private HubService hubService;

	@Autowired
	private HubPaymentService hubPaymentService;

	@Autowired
	private BookingServiceImpl bookingServiceImpl;

	@Autowired
	private AdminServiceImpl adminServiceImpl;

	@Autowired
	private CarRepairRepository carRepairRepository;

	@Autowired
	private PromoCodeRepository promoCodeRepository;

	@Autowired
	private ReturnCarRepository returnCarRepository;

	@Autowired
	private DriverPaymentDetailService driverPaymentDetailService;

	@Value("${bucketName}") // Assuming you have bucket name configured in properties file
	private String bucketName;

	// Create a DTO class for verifying OTP
	public static class VerifyOtpRequest {
		private Long adminId;
		private String otp;

		// Getters and setters

		public String getOtp() {
			return otp;
		}

		public Long getAdminId() {
			return adminId;
		}

		public void setAdminId(Long adminId) {
			this.adminId = adminId;
		}

		public void setOtp(String otp) {
			this.otp = otp;
		}
	}

	@Autowired
	private BookingService bookingService;

	@GetMapping("/driver-booking-details/{bookingId}")
	public ResponseEntity<?> getBookingDriverDetails(@PathVariable Long bookingId) {
		// Fetch data from the repository based on bookingId
		Optional<Booking> optionalBooking = r.findById(bookingId);

		if (optionalBooking.isPresent()) {
			Booking booking = optionalBooking.get();
			// Return the fetched booking details as a response
			return ResponseEntity.status(HttpStatus.SC_OK).body(booking);
		} else {
			// If booking not found, return a not found response
			return ResponseEntity.notFound().build();
		}
	}

	//
	@GetMapping("/driver-booking-details")
	public ResponseEntity<?> getData() {
		// Fetch data from the repository
		Iterable<Booking> data = r.findAll();
		// Return the fetched data as a response
		return ResponseEntity.ok(data);
	}

//Abhilasha
	@GetMapping("/activeDriverRide")
	public ResponseEntity<Object> activeCabDriverRide() {
		try {
			List<Driver> activeDrivers = driverService.findOngoingDriver();
			return ResponseEntity.ok(activeDrivers);
		} catch (BusinessException ex) {
			ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(errorResponse);
		}
	}

//			JALESHWARI - refactor jyoti
	@DeleteMapping("/delete-vehicle/{adminId}/{vehicleId}")
	public ResponseEntity<String> deleteVehicleByAdminIdAndVehicleId(@PathVariable Long adminId,
			@PathVariable Long vehicleId) throws UserNotFoundException {
		Optional<Admin> adminOptional = adminRepository.findById(adminId);
		if (adminOptional.isEmpty()) {
			throw new UserNotFoundException("Admin with ID " + adminId + " not found");
		}

		Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);
		if (vehicleOptional.isEmpty()) {
			throw new UserNotFoundException("Vehicle with ID " + vehicleId + " not found");
		}

		// Additional logic to check if the admin has permission to delete the vehicle

		// If the admin has permission, proceed with deletion
		vehicleRepository.deleteById(vehicleId);

		return ResponseEntity.status(HttpStatus.SC_OK)
				.body("Vehicle with ID " + vehicleId + " deleted successfully by Admin with ID " + adminId);
	}

	@GetMapping("/canclebooking")
	public List<CancellationReason> getAllCancel() {
		return cancellationService.getAllCancellation();

	}

//	Jyoti
	// NEW BOOKING
	@GetMapping("/booked-orders")
	public ResponseEntity<List<BookedOrderDTO>> getBookedOrders() throws DataAccessException {
		List<BookedOrderDTO> bookedOrders = bookingService.getBookedOrders();

		if (bookedOrders.isEmpty()) {
			throw new DataAccessException("No booked orders found in the database.") {
			};
		}
		return ResponseEntity.ok(bookedOrders);
	}

	// COMPLETE BOOKING
//jyoti

	@GetMapping("/completed-orders")
	public ResponseEntity<List<BookedOrderDTO>> getCompletedOrders() throws DataAccessException {
		List<BookedOrderDTO> completedOrders = bookingService.getCompletedOrders();

		if (completedOrders.isEmpty()) {
			throw new DataAccessException("No completed orders found in the database.") {
			};
		}

		return ResponseEntity.ok(completedOrders);
	}

	@GetMapping("/list-of-rejected-driver")
	public ResponseEntity<List<DriverApproveRequestDto>> getRejectedDriverList() {
		return ResponseEntity.status(HttpStatus.SC_ACCEPTED).body(adminService.getRejectedDriverList());

	}

//	Abhilasha

	@GetMapping("/list-total-booking-details")
	public ResponseEntity<Object> getAllOrders() {
		try {
			List<Booking> orders = bookingService.getAllOrders();
			return ResponseEntity.ok(orders);
		} catch (BusinessException ex) {
			ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(errorResponse);
		}
	}
//	Abhilasha
//	@GetMapping("/total-numberofbooking")
//	public int getTotalNumberOfOrders() {
//		return bookingService.getTotalNumberOfOrders();
//	}

	@GetMapping("/total-number-of-booking")
	public ResponseEntity<Object> getTotalNumberOfOrders() {
		try {
			int totalOrders = bookingService.getTotalNumberOfOrders();
			return ResponseEntity.ok("Total number of bookings: " + totalOrders);
		} catch (BusinessException ex) {
			ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(errorResponse);
		}
	}

//	Abhilasha
	@GetMapping("/ongoing/total/active-ride")
	public int getTotalOngoingDrivers() {
		return driverService.getTotalOngoingDrivers();
	}

//	JYOTI
	@PostMapping("/hardcodedAdmin-email-password")
	public Admin getHardcodedAdmin() {
		return adminService.getHardcodedAdmin();
	}

//	JALESHWARI
	@PostMapping("/find-by-admin-email/{email}")
	public ResponseEntity<Admin> sendOtpByEmailForForgatePassword(@PathVariable String email,
			@RequestBody Admin admin1) {

		Admin admin = adminRepository.findByEmail(email);

		System.out.println(admin);
		admin.setName(admin1.getName());
		admin.setEmail(email);
		admin.setPhoneNo(admin1.getPhoneNo());
		admin.setPassword("");
		String generateRandomOtp = userServiceImpl.generateRandomOtp();

		Long adminId = admin.getAdminId();
		ManageOtp manageOtp = manageOtpRepository.findByAdmin_AdminId(adminId).get();

		manageOtp.setForgetOtp(generateRandomOtp);
		manageOtp.setAdmin(admin);

		manageOtpRepository.save(manageOtp);
		userServiceImpl.sendOtpByEmail(email, generateRandomOtp);

		Admin saveAdmin = adminRepository.save(admin);

		return ResponseEntity.status(HttpStatus.SC_ACCEPTED).body(saveAdmin);

	}

//	JALESHWARI
	@PostMapping("/verify-email-admin")
	public ResponseEntity<String> verifyOtpByEmailForForgatePassword(@RequestBody VerifyOtpRequest verifyOtpRequest) {
		boolean isVerified = adminService.verifyEmailOtp(verifyOtpRequest.getAdminId(), verifyOtpRequest.getOtp());

//		System.out.println("318"+email +otp);
		System.out.println();
		if (isVerified) {
			return ResponseEntity.status(HttpStatus.SC_OK).body("OTP verified successfully");

		} else {
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Invalid OTP");

		}
	}

	// JALESHWARI
	@PostMapping("/setpassword/{adminId}")
	public ResponseEntity<String> setPasswordAdmin(@PathVariable Long adminId,
			@RequestBody PasswordRequestDto passwordRequest) {

		boolean setpassword = adminService.setNewPasswordForAdmin(adminId, passwordRequest);
		if (setpassword) {
			return ResponseEntity.status(HttpStatus.SC_OK).body("set Password Successfully");

		} else {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Password not vaild ");
		}
	}

//			Rahul

	@GetMapping("/total-earning/{driverId}")
	public ResponseEntity<DriveracceptpaymentDto> fetchDataByDriverId(@PathVariable Long driverId) {

		DriveracceptpaymentDto data = adminService.customerPayment(driverId);
		if (data != null) {
			return ResponseEntity.ok(data);
		} else {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).build();
		}
	}

//       Abilasha
	@GetMapping("/totalCancelBooking")
	public List<TotalBookingDto> getTotalcancleBooking() {
		List<Booking> bookings = bookingService.getAllOrders();
		List<TotalBookingDto> bookingDtos = new ArrayList<>();

		for (Booking booking : bookings) {
			if (RideOrderStatus.CANCELLED.equals(booking.getRideOrderStatus())) { // Check if ride status is "CANCELLED"
				TotalBookingDto bookingDto = new TotalBookingDto();
				bookingDto.setRideOrderStatus(booking.getRideOrderStatus());

				if (booking.getDriver() != null) {
					bookingDto.setDriverName(booking.getDriver().getName());
					String vehicleNumber = driverService.getVehicleNumberByDriverId(booking.getDriver().getDriverId());
					bookingDto.setVehicleNo(vehicleNumber);
				}
				bookingDtos.add(bookingDto);
			}
		}
		return bookingDtos;
	}

	// get hub active list -jyoti
//	@GetMapping("/available-hub")
//	public List<HubManagerDto> getActiveHubs() throws DataAccessException {
//		List<HubManagerDto> activeHubs = hubService.getActiveHubs();
//
//		if (activeHubs.isEmpty()) {
//			throw new DataAccessException("There are no active hubs.") {
//			};
//		}
//
//		return activeHubs;
//	}

	// jyoti
    @GetMapping("/hub-manager-details/{hubId}")

	public ResponseEntity<HubManagerDto> getHubDetails(@PathVariable Long hubId) throws DataAccessException {
		HubManagerDto hubDetails = hubService.getHubDetails(hubId);

		if (hubDetails != null) {
			return ResponseEntity.ok(hubDetails);
		} else {
			throw new DataAccessException("Hub details not found for hub ID: " + hubId) {
			};
		}
	}

	// jyoti
	@GetMapping("/payemnt-history/{hubId}")

	public ResponseEntity<List<HubManagerPaymentHistoryDto>> getPaymentHistoryByHubId(@PathVariable Long hubId)
			throws DataAccessException {
		List<HubManagerPaymentHistoryDto> paymentHistory = hubService.getPaymentHistoryByHubId(hubId);

		if (paymentHistory == null || paymentHistory.isEmpty()) {
			throw new DataAccessException("Payment history not found for hub ID: " + hubId) {
			};
		}

		return ResponseEntity.ok(paymentHistory);
	}

	// jyoti
	@GetMapping("/all-hub-list")
	public ResponseEntity<List<HubManagerDto>> getAllHubs() {
		List<HubManagerDto> allHubs = hubService.getAllHubs();

		if (!allHubs.isEmpty()) {
			return ResponseEntity.ok(allHubs);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

// Rishi
// hubList
	@GetMapping("/hubs")
	public ResponseEntity<List<HubListDto>> getHubListWithPaymentEndpoint() {
		List<Hub> hubs = hubService.getHubList();
		if (hubs.isEmpty()) {
			throw new BusinessException("601", "Hub List is Empty");
		}
		List<HubListDto> hubLists = hubs.stream().map(hub -> {
			HubListDto hubList = new HubListDto();
			hubList.setHubId(hub.getHubId());
//			hubList.setHubName(hub.getHubName());
			hubList.setManagerName(hub.getManagerName());
			hubList.setManagerName(hub.getManagerName());
			// Set payment endpoint URL
			hubList.setPaymentEndpoint("/hubs/" + hub.getHubId() + "/payment");
			return hubList;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(hubLists);
	}

	@PostMapping("/hubs/{hubId}/payment")
	public ResponseEntity<String> hubPayment(@PathVariable Long hubId, @RequestBody HubPaymentRequestDto request) {
		try {
			hubPaymentService.hubPayment(hubId, request);
			return ResponseEntity.ok("Payment made successfully.");
		} catch (HubNotFoundException e) {
			return ResponseEntity.badRequest().body("Hub with Id " + hubId + " not found.");
		}
	}

//Rishi
	@GetMapping("/hubPaymentHistory")
	public ResponseEntity<List<HubPaymentHistoryDto>> getHubPaymentHistory() {
		List<HubPayment> hubPayments = hubPaymentService.getHubPaymentHistory();
		if (hubPayments.isEmpty()) {
			throw new BusinessException("601", "Hub Payment History is Empty");
		}
		List<HubPaymentHistoryDto> hubPaymentHistoryLists = hubPayments.stream().map(hubPayment -> {
			HubPaymentHistoryDto hubPaymentHistoryList = new HubPaymentHistoryDto();
			hubPaymentHistoryList.setHubName(hubPayment.getHubName());
			hubPaymentHistoryList.setManagerName(hubPayment.getManagerName());
			hubPaymentHistoryList.setAmount(hubPayment.getAmount());
			hubPaymentHistoryList.setDate(hubPayment.getDate());
			hubPaymentHistoryList.setStatus(hubPayment.getStatus());
			return hubPaymentHistoryList;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(hubPaymentHistoryLists);
	}

	@GetMapping("/get-total-vehicle")
	public ResponseEntity<?> getTotalVehicle() {

		List<Vehicle> listOfVehicle = vehicleRepository.findAll();

		if (listOfVehicle.size() == 0) {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("There is no Vehicle present");
		} else {

			return ResponseEntity.status(HttpStatus.SC_OK).body(listOfVehicle);
		}
	}

	// jyoti
	@GetMapping("/admin-profile-by-email")

	public ResponseEntity<ProfileDto> getAdminProfileByEmail(@RequestParam String email) throws DataAccessException {
		ProfileDto profileDto = adminService.getProfileByEmail(email);

		if (profileDto == null) {

			throw new DataAccessException("This Email is not found - " + email) {
			};
		}

		return ResponseEntity.ok(profileDto);
	}

	// RAHUL
	@GetMapping("/edit-admin-profile/{AdminId}")
	public ResponseEntity<AdminProfileEditDto> getHubMangerProfile(@PathVariable Long AdminId) throws Exception {
		AdminProfileEditDto getProfile = adminService.getAdminProfile(AdminId);
		if (getProfile == null) {
			throw new DataAccessException(" Admin Profile not found with this " + AdminId) {
			};
		}
		return ResponseEntity.ok(getProfile);

	}

	// RAHUL
	@PutMapping("/edit-admin-profile")
	public ResponseEntity<String> updateAdminProfile(
			@RequestParam(value = "profileImg", required = false) MultipartFile file,
			@RequestParam("AdminDataJson") String AdminDataJson) throws Exception {

		try {
			// Convert JSON to DriverDataDto object
			ObjectMapper objectMapper = new ObjectMapper();
			AdminDataDto adminDataJson = objectMapper.readValue(AdminDataJson, AdminDataDto.class);

			String s3Url = null;
			// Check if profile image is provided
			if (file != null && !file.isEmpty()) {
				String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
				System.out.println("uniqueFileName=" + uniqueFileName);
				// Generate a unique file name for the image
				// String fileName = file.getOriginalFilename();
				// Convert MultipartFile to File
				File convertedFile = convertMultiPartToFile(file, uniqueFileName);
				// Upload the file to S3 bucket
				s3Url = uploadFileToS3(convertedFile, uniqueFileName);
				System.out.println("Image link=" + s3Url);
			}

			// If s3Url is still null, set it from the existing profileImgLink
			if (s3Url == null) {
				// Retrieve existing admin profile
				Admin existingAdmin = adminRepository.findById(adminDataJson.getAdminId()).get();

				// Set s3Url from the existing profileImgLink
				s3Url = existingAdmin.getProfileImgLink();
			}

			// Call service method to update admin profile
			adminService.updateAdminProfile(adminDataJson, s3Url);
			return ResponseEntity.ok("Admin Profile uploaded successfully");
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception appropriately (e.g., log it)
			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error uploading documents");
		}
	}

	private File convertMultiPartToFile(MultipartFile file, String fileName) throws IOException {

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

	@GetMapping("/get-by-admin-phoneno/{phoneno}")
	public ResponseEntity<ResponseLogin> getUserByPhoneNo(@PathVariable String phoneno) {
		ResponseLogin response = adminService.getByPhoneno(phoneno);
		if (response != null) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

//Rishi
	@GetMapping("/getUserPaymentHistory")
	public ResponseEntity<List<UserPaymentHistoryDto>> getUserPaymentHistory() {
		List<Booking> bookings = bookingService.getUserPayments();
		if (bookings.isEmpty()) {
			throw new BusinessException("601", "User Payment History is Empty");
		}
		List<UserPaymentHistoryDto> userPayments = new ArrayList<>();
		for (Booking booking : bookings) {
			UserPaymentHistoryDto userPaymentHistory = new UserPaymentHistoryDto();
			userPaymentHistory.setCustomerName(booking.getUser().getName());
//			userPaymentHistory.setHubName(booking.getHub().getHubName());
			userPaymentHistory.setManagerName(booking.getHub().getManagerName());
			userPaymentHistory.setAmount(booking.getTotalAmount());
			userPayments.add(userPaymentHistory);
		}
		return ResponseEntity.ok(userPayments);
	}

	@GetMapping("/getTotalEarningOfHubs")
	public ResponseEntity<?> getTotalEarning(@RequestParam LocalDate startDate) {
		return ResponseEntity.status(HttpStatus.SC_OK).body(adminService.getRevenueOfRido(startDate));
	}

//Rishi
	@GetMapping("/totalExpensesForCurrentMonth")
	public ResponseEntity<BigDecimal> getTotalExpensesForCurrentMonth() {
		BigDecimal totalExpenses = adminService.getTotalExpensesForCurrentMonth();
		return ResponseEntity.ok(totalExpenses);
	}

//Rishi
	@GetMapping("/totalExpensesForCurrentMonth/{hubId}")
	public ResponseEntity<BigDecimal> getTotalExpensesForCurrentMonthByHub(@PathVariable Long hubId) {
		BigDecimal totalExpenses = adminService.getTotalExpensesForCurrentMonthByHub(hubId);
		return ResponseEntity.ok(totalExpenses);
	}

//Rishi
	@GetMapping("/availableAmountForCurrentMonth")
	public ResponseEntity<BigDecimal> getAvailableAmountForCurrentMonth() {
		BigDecimal availableAmount = adminService.getAvailableAmountForCurrentMonth();
		return ResponseEntity.ok(availableAmount);
	}

//Rishi	
	@GetMapping("/availableAmountForCurrentMonth/{hubId}")
	public ResponseEntity<BigDecimal> getAvailableAmountForCurrentMonthByHub(@PathVariable Long hubId) {
		BigDecimal availableAmount = adminService.getAvailableAmountForCurrentMonthByHub(hubId);
		return ResponseEntity.ok(availableAmount);
	}

//Rishi	
	@GetMapping("/totalAmountOfPreviousMonth")
	public ResponseEntity<BigDecimal> getTotalAmountForPreviousMonth() {
		BigDecimal totalAmount = bookingService.getTotalAmountForPreviousMonth();
		return ResponseEntity.ok(totalAmount);
	}

	@GetMapping("/totalBookingAmount")
	public ResponseEntity<BigDecimal> getTotalBookingAmount() {
		BigDecimal totalAmountSum = bookingService.getTotalAmountSum();
		return ResponseEntity.ok(totalAmountSum);
	}

	@GetMapping("/daily")
	public ResponseEntity<Map<String, BigDecimal>> getDailyEarningsAndExpenses(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		Map<String, BigDecimal> result = bookingService.calculateDailyEarningsAndExpenses(date);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/weekly")
	public ResponseEntity<Map<String, BigDecimal>> getWeeklyEarningsAndExpenses(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
		Map<String, BigDecimal> result = bookingService.calculateWeeklyEarningsAndExpenses(startDate, endDate);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/monthly")
	public ResponseEntity<Map<String, BigDecimal>> getMonthlyEarningsAndExpenses(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM") Date month) {
		Map<String, BigDecimal> result = bookingService.calculateMonthlyEarningsAndExpenses(month);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/chart")
	public ResponseEntity<String> generateChart() {
		String chartData = "{ \"chart\": { \"type\": \"bar\", \"data\": { \"labels\": [\"January\", \"February\", \"March\", \"April\", \"May\"], \"datasets\": [{ \"label\": \"Earnings\", \"data\": [65, 59, 80, 81, 56] }, { \"label\": \"Expenses\", \"data\": [28, 48, 40, 19, 86] }] } } }";
		return ResponseEntity.ok(chartData);
	}

//Rishi	
	@GetMapping("/monthlyGraph")
	public ResponseEntity<Map<String, BigDecimal>> getMonthlyBookingGraph() {
		Map<String, BigDecimal> graphData = bookingService.generateMonthlyBookingGraph();
		return ResponseEntity.ok(graphData);
	}

//Rishi	
	@GetMapping("/yearlyGraph")
	public ResponseEntity<Map<Integer, BigDecimal>> getYearlyBookingGraph() {
		Map<Integer, BigDecimal> graphData = bookingService.generateYearlyBookingGraph();
		return ResponseEntity.ok(graphData);
	}

//Rishi	
	@GetMapping("/weeklyTotalEarningGraph")
	public ResponseEntity<Map<String, BigDecimal>> getweeklyTotalEarningGraph() {
		Map<String, BigDecimal> weeklyTotalAmount = bookingService.getWeeklyTotalEarningGraph();
		return ResponseEntity.status(HttpStatus.SC_OK).body(weeklyTotalAmount);
	}

//Rishi	
	@GetMapping("/monthlyTotalEarningGraph")
	public ResponseEntity<Map<String, BigDecimal>> getMonthlyTotalEarningGraph() {
		Map<String, BigDecimal> monthlyTotalAmount = bookingService.getMonthlyTotalEarningGraph();
		return ResponseEntity.status(HttpStatus.SC_OK).body(monthlyTotalAmount);
	}

//Rishi	
	@GetMapping("/monthlyTotalExpensesForYear")
	public ResponseEntity<Map<String, BigDecimal>> getMonthlyTotalExpensesForYear() {
		int currentYear = Year.now().getValue();
		Map<String, BigDecimal> monthlyTotalExpenses = adminService.getMonthlyTotalExpensesForYear(currentYear);
		return ResponseEntity.ok(monthlyTotalExpenses);
	}

	@GetMapping("/get-by-driver-phoneno/{phoneno}")
	public ResponseEntity<ResponseLogin> getAdminByPhoneNo(@PathVariable String phoneno) {
		ResponseLogin response = adminService.getByPhoneno(phoneno);
		if (response != null) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// Rishi
	@GetMapping("/get-schedule-booking-details")
	public ResponseEntity<Object> getScheduleBookingDetails() {

		Object listOfBooking = hubService.getRentalBookingDetails();
		try {
			ApiResponse<Object> response = new ApiResponse<Object>(listOfBooking,
					org.springframework.http.HttpStatus.OK, true, "Here is a list of rental booking details");
			return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(response);
		} catch (Exception ex) {
			ApiResponse<Object> response = new ApiResponse<Object>(null,
					org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, false, "There is something wrong");
			return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);

		}

	}

	// Rishi
	@GetMapping("/CarRepairsList")
	public List<CarRepair> getCarRepairsList() {
		return carRepairRepository.findAll();
	}

	// Rishi
	@GetMapping("/CarRepairById/{carRepairId}")
	public CarRepair getCarRepairById(@PathVariable Long carRepairId) {
		return carRepairRepository.findByCarRepairId(carRepairId);
	}

	// Rishi refactor -jyoti
	@PostMapping("/carRepairAccepted/{carRepairId}")
	public CarRepair carRepairAccepted(@PathVariable Long carRepairId,
			@RequestParam MaintenanceApprovalStatus maintenanceApprovalStatus) {
		CarRepair carRepairApproval = adminService.carRepairAccepted(carRepairId, maintenanceApprovalStatus);
		return carRepairApproval;
	}

	// Rishi refactor -jyoti
	@PostMapping("/carRepairRejected/{carRepairId}")
	public CarRepair carRepairRejected(@PathVariable Long carRepairId,
			@RequestParam MaintenanceApprovalStatus maintenanceApprovalStatus) {
		CarRepair carRepairApproval = adminService.carRepairRejected(carRepairId, maintenanceApprovalStatus);
		return carRepairApproval;
	}

	// Rishi
	@GetMapping("/acceptedCarRepairsList")
	public List<CarRepair> getAcceptedCarRepairsList() {
		return carRepairRepository.findByMaintenanceApprovalStatus(MaintenanceApprovalStatus.ACCEPTED);
	}

	// Rishi
	@GetMapping("/rejectedCarRepairsList")
	public List<CarRepair> getRejectedCarRepairs() {
		return carRepairRepository.findByMaintenanceApprovalStatus(MaintenanceApprovalStatus.REJECTED);
	}

	@GetMapping("/get-rental-booking-details")
	public ResponseEntity<Object> getRentalBookingDetails() {

		try {
			Object listOfBooking = hubService.getRentalBookingDetails();
			ApiResponse<Object> response = new ApiResponse<Object>(listOfBooking,
					org.springframework.http.HttpStatus.OK, true, "Here is a list of rental booking details");
			return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(response);
		} catch (Exception ex) {
			ApiResponse<Object> response = new ApiResponse<Object>(null,
					org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, false, "There is something wrong");
			return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);

		}

	}

	@GetMapping("/get-courier-booking-details")
	public ResponseEntity<Object> getCourierBookingDetails() {
		Object listOfBooking = hubService.getCourierBookingDetails();
		try {
			ApiResponse<Object> response = new ApiResponse<>(listOfBooking, org.springframework.http.HttpStatus.OK,
					true, "Here is a list of rental booking details");
			return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(response);
		} catch (Exception ex) {
			ApiResponse<Object> response = new ApiResponse<>(null,
					org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, false, "There is something wrong");
			return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// RAHUL
	@PostMapping("/createPromoCode/{adminId}")
	public ResponseEntity<PromoCode> createPromoCode(@RequestBody PromoCode promoCodeRequest,
			@PathVariable Long adminId) {
		Admin admin = adminRepository.findById(adminId).get();
		PromoCode promoCode = new PromoCode();
		promoCode.setCode(promoCodeRequest.getCode());
		promoCode.setCodeDescription(promoCodeRequest.getCodeDescription());
		promoCode.setDiscountPercentage(promoCodeRequest.getDiscountPercentage());
		promoCode.setExpirationDate(promoCodeRequest.getExpirationDate());
		promoCode.setAdmin(admin);
		promoCode.setPromocodeType(promoCodeRequest.getPromocodeType());
		promoCodeRepository.save(promoCode);
		return ResponseEntity.ok(promoCode);

	}

	// RAHUL
	@GetMapping("/getAllPromoCode")
	public ResponseEntity<?> getAllPromoCode() {
		List<PromoCode> promoCodes = promoCodeRepository.findAll();
		if (promoCodes.size() == 0) {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("There is no Promocode  present");
		} else {
			return ResponseEntity.ok(promoCodes);
		}
	}

	// RAHUL
	@GetMapping("/getPromoCodeById/{promocodeId}")
	public ResponseEntity<?> getPromoCodeById(@PathVariable Long promocodeId) {
		Optional<PromoCode> promocode = promoCodeRepository.findById(promocodeId);
		if (promocode.isPresent()) {
			PromoCode promocode1 = promocode.get();
			return ResponseEntity.ok(promocode1);
		} else {
			throw new ResourceNotFoundException("Resource Not Found with this " + promocodeId, "");
		}

	}

	// RAHUL
	@DeleteMapping("deleteby-promocodeId/{promocodeId}")
	public ResponseEntity<?> deletePromoCodeById(@PathVariable Long promocodeId) {

		Optional<PromoCode> check = promoCodeRepository.findById(promocodeId);
		if (check.isPresent()) {
			promoCodeRepository.deleteById(promocodeId);
			return ResponseEntity.ok("The Promocode Is Delete");
		} else {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("There is no Promocode Id present");
		}
	}

	// RAHUL
	@DeleteMapping("delete-allpromocode")
	public ResponseEntity<?> deleteallpromode() {
		promoCodeRepository.deleteAll();
		return ResponseEntity.ok("All Promocode is delete");

	}

	@GetMapping("/driverPaymentpending-list/hub/{hubId}")
	public List<DriverPaymentDetail> getPendingPaymentsByHubId(@PathVariable Long hubId) {
		return adminService.getPendingPaymentsByHubId(hubId);
	}

	@GetMapping("/hub/{hubId}/driver/{driverId}")
	public ResponseEntity<DriverPaymentDetail> getPaymentDetailByHubIdAndDriverId(@PathVariable Long hubId,
			@PathVariable Long driverId) {
		Optional<DriverPaymentDetail> paymentDetail = adminService.getPaymentDetailByHubIdAndDriverId(hubId, driverId);
		return paymentDetail.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

//	// RAHUL
//	@PostMapping("/add-new-Vehicle/{adminId}")
//	public ResponseEntity<String> addNewVehicle(@RequestParam(name = "vehicleImg") MultipartFile file,
//	                                            @RequestParam("VehicleDataDto") String vehicleDataJson, 
//	                                            @PathVariable("adminId") Long adminId) {
//	    try {
//	        // Upload file to S3 and get the S3 URL
//	        String s3Url = vehicleService.uploadFile(file);
//	        System.out.println("s3Url=" + s3Url);
//
//	        // Convert JSON to VehicleDataDto object
//	        ObjectMapper objectMapper = new ObjectMapper();
//	        VehicleDataDto vehicleDataDto = objectMapper.readValue(vehicleDataJson, VehicleDataDto.class);
//
//	        // Call service method to add new vehicle with updated vehicleDataDto, S3 URL, and adminId
//	        String response = adminService.addVehicle(vehicleDataDto, s3Url, adminId);
//
//	        if (response.equals("New vehicle added successfully!")) {
//	            return ResponseEntity.ok(response);
//	        } else {
//	            return ResponseEntity.status(HttpStatus.SC_CONFLICT).body(response);
//	        }
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error adding vehicle");
//	    }
//	}

	// RAHUL vehicle details
	@GetMapping("details-of-vehicle/{vehicleId}")
	public ResponseEntity<VehicleDataDto> getVehicleDetails(@PathVariable Long vehicleId) {
		VehicleDataDto vehicleDataDto = vehicleService.getVehicleDetails(vehicleId);
		if (vehicleDataDto != null) {
			return ResponseEntity.ok(vehicleDataDto);
		} else {
			throw new ResourceNotFoundException("Resource Not Found with this Id" + vehicleId, "");
		}

	}

	// RAHUL List Of vehicle
	@GetMapping("/list-of-vehicle/{adminId}")
	public ResponseEntity<List<VehicleDataDto>> getVehiclesListByAdminId(@PathVariable Long adminId) {
		List<VehicleDataDto> vehicles = vehicleService.getVehiclesByAdminId(adminId);

		if (vehicles.isEmpty()) {
			throw new DataAccessException("No vehicle found in the database.") {
			};
		}

		return ResponseEntity.ok(vehicles);
	}

//	// Rahul refactor- jyoti
	@PostMapping("/vehicles/assign-hub")
	public ResponseEntity<List<Vehicle>> assignHubToVehicles(
			@RequestBody List<ListOfAssignVehiclesDto> assignVehicles) {

		List<Vehicle> assignedVehicles = adminService.assignHubToVehicles(assignVehicles);
		if (assignedVehicles.isEmpty()) {
			throw new DataAccessException("No vehicle found in the database.") {
			};
		}
		return ResponseEntity.ok(assignedVehicles);
	}

	// jyoti
	@GetMapping("/get-driver-list/{adminId}/{hubId}")
	public ResponseEntity<List<Driver>> getDriverListByAdminIdAndHubId(@PathVariable Long adminId,
			@PathVariable Long hubId) {
		List<Driver> drivers = driverRepository.findByAdminIdAndHubId(adminId, hubId);
		if (drivers.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(drivers);
	}

	@GetMapping("/get-All-Hub/{adminId}")
	public ResponseEntity<List<HubDTO>> getAllHubList(@PathVariable Long adminId) throws UserNotFoundException {
		List<Hub> hubList = hubRepository.findByAdmin_AdminId(adminId);
		if (!hubList.isEmpty()) {
			List<HubDTO> hubDTOList = hubList.stream().map(hub -> new HubDTO(hub.getManagerName(), hub.getHubName()))
					.collect(Collectors.toList());
			return ResponseEntity.ok(hubDTOList);
		} else {
			throw new UserNotFoundException("Admin not found with this id " + adminId);
		}
	}

//	// Abhilasha
//	@PostMapping("/add-new-courierEbike/{adminId}")
//	public ResponseEntity<String> courierEbike(@RequestParam(name = "ebikeImage") MultipartFile file,
//			@RequestParam("CourierEbikeDto") String VehicleDataJson, @PathVariable("adminId") Long adminId) {
//		try {
//			// Upload file to S3 and get the S3 URL
//			String s3Url = vehicleService.uploadFile(file);
//			System.out.println("s3Url=" + s3Url);
//
//			// Convert JSON to VehicleDataDto object
//			ObjectMapper objectMapper = new ObjectMapper();
//			CourierEbikeDto ebikeDataDto = objectMapper.readValue(VehicleDataJson, CourierEbikeDto.class);
//
//			// Call service method to add new eBike with updated ebikeDataDto, S3 URL, and
//			// hubId
//			String response = vehicleService.addebikeVehicle(ebikeDataDto, s3Url, adminId);
//
//			return ResponseEntity.ok(response);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error Adding Car");
//		}
//
//	}

//Abhilasha
	@GetMapping("/list-all-assign-ebikeTohub/{adminId}")
	public List<CourierEbike> getEbikeListByAdminId(@PathVariable Long adminId) {
		return vehicleService.getEbikeListByAdminId(adminId);
	}

//Abhilasha
	@GetMapping("/list-all-non-assign-ebikeTohub/{adminId}")
	public List<CourierEbike> getEbikeListNotAssignByAdminId(@PathVariable Long adminId) {
		return vehicleService.getnonAssignEbikeListByAdminId(adminId);
	}

	// RAHUL - refactor jyoti
	@PostMapping("/courierEbike/assign-hub")
	public ResponseEntity<List<CourierEbike>> assignHubToCourierVehicles(
			@RequestBody List<ListOfAssignVehiclesDto> assignVehicles) {

		List<CourierEbike> assignedVehicles = adminService.assignHubToCourierVehicles(assignVehicles);
		if (assignedVehicles.isEmpty()) {
			throw new DataAccessException("No vehicle found in the database.") {
			};
		}
		return ResponseEntity.ok(assignedVehicles);
	}

	// RAHUL
	@GetMapping("/list-ofvehicles/{hubId}/{adminId}")
	public ResponseEntity<List<Vehicle>> getVehicles(@PathVariable Long hubId, @PathVariable Long adminId) {
		try {
			List<Vehicle> vehicles = adminService.getVehiclesByHubIdAndAdminId(hubId, adminId);
			return ResponseEntity.ok(vehicles);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	// RAHUL
	@GetMapping("/vehicles/no-hub/{adminId}")
	public ResponseEntity<List<Vehicle>> getVehiclesWithNoHub(@PathVariable Long adminId) {
		List<Vehicle> vehicles = adminService.getVehiclesWithNoHub(adminId);
		if (vehicles != null) {
			return ResponseEntity.ok(vehicles);
		} else {
			throw new DataAccessException("Hub Id is present there") {
			};
		}
	}

	@GetMapping("/excel")
	public ResponseEntity<Resource> downloadDriverPaymentDetails() throws IOException {
		String filename = "driverPaymentDetails.xlsx";
		ByteArrayInputStream actualData = adminService.getDriverPaymentExcelData(LocalDate.now());
		InputStreamResource file = new InputStreamResource(actualData);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(file);
	}

	// JALESHWARI
	@GetMapping("/vehicles/available-hub/{adminId}")
	public ResponseEntity<List<Vehicle>> getVehiclesWithAvailableHub(@PathVariable Long adminId) {
		List<Vehicle> vehicles = adminService.getVehiclesWithAvailableHub(adminId);
		if (vehicles != null) {
			return ResponseEntity.ok(vehicles);
		} else {
			throw new DataAccessException("Hub Id is present there") {
			};
		}
	}

	// JALESHWARI
	@GetMapping("/couriers/available-hub/{adminId}")
	public ResponseEntity<List<CourierEbike>> getCourierWithAvailableHub(@PathVariable Long adminId) {
		List<CourierEbike> vehicles = adminService.getCourierEbikeWithAvailableHub(adminId);
		if (vehicles != null) {
			return ResponseEntity.ok(vehicles);
		} else {
			throw new DataAccessException("Hub Id is present there") {
			};
		}
	}

//		JALESHWARI 
	@GetMapping("/vehiclesandcourier/available-hub/{adminId}")
	public ResponseEntity<List<VehicleCourierEbikeDto>> getVehiclesandCourierEbikeWithAvailableHub(
			@PathVariable Long adminId) {
		List<VehicleCourierEbikeDto> vehicleCourierEbikeDtos = adminService.getVehiclesEbikesWithAvailableHub(adminId);
		if (vehicleCourierEbikeDtos != null && !vehicleCourierEbikeDtos.isEmpty()) {
			return ResponseEntity.ok(vehicleCourierEbikeDtos);
		} else {
			throw new DataAccessException("No available vehicles or courier ebikes for the given admin ID") {
			};
		}
	}

	// Abhilasha
	@GetMapping("/courier-ebike-driverpayments/{hubId}")
	public List<DriverPaymentDetail> getebikedriverPaymentsByHubAndDate(@PathVariable("hubId") Long hubId,
			@RequestParam("date") LocalDate date) {
		return driverPaymentDetailService.getcourierebikePaymentsByHubAndDate(hubId, date);
	}

	// Abhilasha
	@GetMapping("/list-all-courier-BookingGraph")
	public ResponseEntity<List<CourierBookingDto1>> getAllCourierBookings() {
		List<CourierBookingDto1> courierBookings = hubService.getAllCourierBookings();
		return ResponseEntity.ok(courierBookings);

	}

	// Abhilasha new DriverPayment
	@PostMapping("/{adminId}/driver-Payment")
	public ResponseEntity<Map<Long, Map<Long, Map<String, Object>>>> calculateAdminDriverPayments(
			@PathVariable Long adminId, @RequestParam LocalDate date) {
		// Fetch all hubs for the given adminId
		List<Hub> hubs = hubRepository.findByAdminAdminId(adminId);
		Map<Long, Map<Long, Map<String, Object>>> allHubDriverPayments = new HashMap<>();
		for (Hub hub : hubs) {
			Long hubId = hub.getHubId();
			List<Driver> drivers = driverRepository.findByHubHubId(hubId);

			// Calculate start date 6 days back from the given date
			LocalDate startDate1 = date.minusDays(7);
			// Calculate end date which is the day before the given date
			LocalDate endDate2 = date.minusDays(1);

			LocalDateTime startDate = startDate1.atStartOfDay();
			LocalDateTime endDate = endDate2.atTime(LocalTime.MAX);

			Map<Long, Map<String, Object>> driverPayments = new HashMap<>();

			for (Driver driver : drivers) {
				Long driverId = driver.getDriverId();
				int totalCompletedRides1 = bookingService.getTotalCompletedRidesForDriverinweek(driverId, startDate,
						endDate);
				int totalCompletedRides2 = bookingService.getTotalCompletedrentalRidesForDriverinweek(driverId,
						startDate, endDate);
				int totalride = totalCompletedRides1 + totalCompletedRides2;

				List<ReturnCar> assignCars = returnCarRepository.findByDriverIdAndAssignTimeBetween(driverId, startDate,
						endDate);
				List<ReturnCar> returnCars = returnCarRepository.findByDriverIdAndReturnTimeBetween(driverId, startDate,
						endDate);

				// Calculate the total working hours per day
				Map<LocalDate, Integer> workingHoursMap = bookingService.calculateWorkingHours(assignCars, returnCars);

				DriverAndVehicleType driverType = bookingService.determineDriverType(driverId);

				int totalPayment = bookingService.calculateTotalPayment(workingHoursMap, totalride, driverType);
				bookingService.saveTotalPayment(driverId, totalPayment, date);

				Map<String, Object> driverResponse = new HashMap<>();
				driverResponse.put("dailyPayments", workingHoursMap);
				driverResponse.put("totalPayment", totalPayment);

				driverPayments.put(driverId, driverResponse);
			}

			allHubDriverPayments.put(hubId, driverPayments);
		}

		return ResponseEntity.ok(allHubDriverPayments);
	}

	// Abhilasha
	@GetMapping("/list-all-BookingGraph")
	public ResponseEntity<List<CourierBookingDto1>> getAllBookings() {
		List<CourierBookingDto1> bookings = bookingService.getAllBookings();
		return ResponseEntity.ok(bookings);
	}

	// Abhilasha
	@GetMapping("/list-all-RentalGraph")
	public ResponseEntity<List<CourierBookingDto1>> getAllrentalBooking() {
		List<CourierBookingDto1> rentalbookings = bookingService.getRentalBooking();
		return ResponseEntity.ok(rentalbookings);
	}

	// Abhilasha
	@GetMapping("/driverpayments/{hubId}")
	public List<DriverPaymentDetail> getPaymentsByHubAndDate(@PathVariable("hubId") Long hubId,
			@RequestParam("date") LocalDate date) {
		return driverPaymentDetailService.getPaymentsByHubAndDate(hubId, date);
	}

	// Abhilasha
	@GetMapping("/driver-payment-pending-list")
	public List<DriverPaymentDetail> getAllPendingPayments() {
		return driverPaymentDetailService.getPendingPayments();
	}

	// Abhilasha
	@GetMapping("/pending-driver/{driverPaymentDetailId}")
	public ResponseEntity<DriverPaymentDetail> getPendingDriverPaymentDetailById(
			@PathVariable Long driverPaymentDetailId) {
		Optional<DriverPaymentDetail> driverPaymentDetail = driverPaymentDetailService
				.getPendingDriverPaymentDetailById(driverPaymentDetailId);
		if (driverPaymentDetail.isPresent()) {
			return ResponseEntity.ok(driverPaymentDetail.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// Abhilasha
	@PostMapping("/{adminId}/hub-payments")
	public ResponseEntity<String> hubPaymentForAdmin(@PathVariable Long adminId,
			@RequestBody HubPaymentRequestDto request) {
		hubPaymentService.hubPaymentForAdmin(adminId, request);
		return ResponseEntity.ok("Payments made successfully for all hubs under admin Id " + adminId);
	}

	@PostMapping("/{adminId}/change-password")
	public ResponseEntity<String> changePassword(@PathVariable Long adminId,
			@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
		boolean success = adminService.changePassword(adminId, changePasswordRequestDto);
		if (success) {
			return ResponseEntity.ok("Password changed successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to change password");
		}
	}

////Abhilasha
//	@PostMapping("/add-new-Vehicle/{adminId}")
//	public ResponseEntity<String> addNewVehicle(
//	        @RequestParam(name = "vehicleImg", required = true) MultipartFile vehicleImg,
//	        @RequestParam(name = "invoice", required = true) MultipartFile invoiceFile,
//	        @RequestParam("VehicleDataDto") String vehicleDataJson,
//	        @PathVariable("adminId") Long adminId) {
//	    try {
//	        // Set maximum file size for vehicle image (e.g., 2MB)
//	        if (vehicleImg.getSize() > 2 * 1024 * 1024) {
//	        	System.out.println(123);// 5MB in bytes
//	            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
//	                    .body("Vehicle image file size exceeds the limit of 5MB.");
//	        }
//
//
//	        // Set maximum file size for invoice (e.g., 2MB)
//	        if (invoiceFile.getSize() > 2 * 1024 * 1024) { // 2MB in bytes
//	        	System.out.println(123);
//	            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
//	                    .body("Invoice file size exceeds the limit of 2MB.");
//	        }
//
//	        // Upload vehicle image to S3 and get the S3 URL
//	        String vehicleImgUrl = vehicleService.uploadFile(vehicleImg);
//	        System.out.println("Vehicle Image URL: " + vehicleImgUrl);
//
//	        // Upload invoice file to S3 and get the S3 URL
//	        String invoiceUrl = vehicleService.uploadFile(invoiceFile);
//	        System.out.println("Invoice URL: " + invoiceUrl);
//
//	        // Convert JSON to VehicleDataDto object
//	        ObjectMapper objectMapper = new ObjectMapper();
//	        VehicleDataDto vehicleDataDto = objectMapper.readValue(vehicleDataJson, VehicleDataDto.class);
//
//	        // Call service method to add a new vehicle with updated vehicleDataDto, S3 URLs, and adminId
//	        String response = adminService.addVehicle(vehicleDataDto, vehicleImgUrl, invoiceUrl, adminId);
//
//	        if (response.equals("New vehicle added successfully!")) {
//	            return ResponseEntity.ok(response);
//	        } else {
//	            return ResponseEntity.status(HttpStatus.SC_CONFLICT).body(response);
//	        }
//	    } catch (IOException e) {
//	        e.printStackTrace();
//        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error adding vehicle");
//      }
//	}
	// jyoti
	@GetMapping("/acceptedCarRepairs/{carRepairId}")
	public ResponseEntity<CarRepair> getAcceptedCarRepairById(@PathVariable Long carRepairId) {
		Optional<CarRepair> carRepairOptional = carRepairRepository.findById(carRepairId);
		if (carRepairOptional.isPresent()
				&& carRepairOptional.get().getMaintenanceApprovalStatus() == MaintenanceApprovalStatus.ACCEPTED) {
			return ResponseEntity.ok(carRepairOptional.get());
		} else {
			return ResponseEntity.notFound().build();
		}

	}
//Abhilasha
	@PostMapping("/add-new-Vehicle/{adminId}")
	public ResponseEntity<String> addNewVehicle(
			@RequestParam(name = "vehicleImg", required = true) MultipartFile[] vehicleImg,
			@RequestParam(name = "invoice", required = true) MultipartFile invoiceFile,
			@RequestParam("VehicleDataDto") String vehicleDataJson, @PathVariable("adminId") Long adminId) {
		try {
			// Set maximum file size for each vehicle image and for invoice (20MB)
			long maxFileSize = 20L * 1024L * 1024L; // 20MB in bytes

			// Check each vehicle image file size
			for (MultipartFile img : vehicleImg) {
				if (img.getSize() > maxFileSize) {
					return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
							.body("One of the vehicle image files exceeds the limit of 20MB.");
				}
			}

			// Check invoice file size
			if (invoiceFile.getSize() > maxFileSize) {
				return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
						.body("Invoice file size exceeds the limit of 20MB.");
			}

			// Upload each vehicle image to S3 and collect their URLs
			List<String> vehicleImgUrls = new ArrayList<>();
			for (MultipartFile img : vehicleImg) {
				String vehicleImgUrl = vehicleService.uploadFile(img);
				vehicleImgUrls.add(vehicleImgUrl);
				System.out.println("Vehicle Image URL: " + vehicleImgUrl);
			}

			// Upload invoice file to S3 and get the S3 URL
			String invoiceUrl = vehicleService.uploadFile(invoiceFile);
			System.out.println("Invoice URL: " + invoiceUrl);

			// Convert JSON to VehicleDataDto object
			ObjectMapper objectMapper = new ObjectMapper();
			VehicleDataDto vehicleDataDto = objectMapper.readValue(vehicleDataJson, VehicleDataDto.class);

			// Call service method to add a new vehicle with updated vehicleDataDto, S3
			// URLs, and adminId
			String response = adminService.addVehicle(vehicleDataDto, vehicleImgUrls, invoiceUrl, adminId);

			if (response.equals("New vehicle added successfully!")) {
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.status(HttpStatus.SC_CONFLICT).body(response);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error adding vehicle");
		}
	}

	@GetMapping("/get-all-hublist/{adminId}")
	public ResponseEntity<List<Hub>> getAllHubsList(@PathVariable Long adminId) {
		List<Hub> hubList = adminService.getAllHubList(adminId);
		System.out.println(hubList);

		if (hubList != null && !hubList.isEmpty()) {

			return ResponseEntity.ok(hubList);
		} else {
			return ResponseEntity.ok(null);

		}

	}

	//abhilasha

	@GetMapping("/admin-profile-ById/{adminId}")
    public ResponseEntity<Admin> getAdminById(@PathVariable("adminId") Long adminId) {
        Optional<Admin> admin = adminService.getAdminById(adminId);
        if (admin.isPresent()) {
            return ResponseEntity.ok(admin.get());
        } else {
            return ResponseEntity.notFound().build();
        }    
    
	
	}
	//Abhilasha
	@PostMapping("/add-new-courierEbike/{adminId}")
	public ResponseEntity<String> AddcourierEbike(
	        @RequestParam(name = "ebikeImage") List<MultipartFile> ebikeImage,
	        @RequestParam(name = "invoice", required = true) MultipartFile invoiceFile,
	        @RequestParam("CourierEbikeDto") String vehicleDataJson,
	        @PathVariable("adminId") Long adminId) {
	    try {
	        // List to store S3 URLs of uploaded files
	        List<String> s3Urls = new ArrayList<>();

	        // Upload each file to S3 and store the URLs
	        for (MultipartFile file : ebikeImage) {
	            String s3Url = vehicleService.uploadFile(file);
	            System.out.println("s3Url=" + s3Url);
	            s3Urls.add(s3Url);
	        }

	        // Upload invoice file to S3 and get the S3 URL
	        String invoiceUrl = vehicleService.uploadFile(invoiceFile);
	        System.out.println("Invoice URL: " + invoiceUrl);

	        // Convert JSON to VehicleDataDto object
	        ObjectMapper objectMapper = new ObjectMapper();
	        CourierEbikeDto ebikeDataDto = objectMapper.readValue(vehicleDataJson, CourierEbikeDto.class);

	        // Call service method to add new eBike with updated Vehicle entity and adminId
	        String response = adminService.addCourierEbikeVehicle(ebikeDataDto, s3Urls, invoiceUrl, adminId);

	        return ResponseEntity.ok(response);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error Adding eBike");
	    }
	}

    
	
	//jyoti
	@GetMapping("/PendingCarRepairsList")
    public List<CarRepair> getPendingCarRepairsList() {
        return carRepairRepository.findByCarRepairStatus(CarRepairStatus.PENDING);
    }


}