package com.rido.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rido.Exceptions.DriverNotFoundException;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.controller.UserController.VerifyOtpRequest;
import com.rido.dto.DriverChangePasswordRequestDto;
import com.rido.dto.DriverDataDto;
import com.rido.dto.DriverDocumentResponseDto;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.PaymentHistoryDto;
import com.rido.dto.ProfileDto;
import com.rido.dto.UserNotificationDto;
import com.rido.dto.VerifyRequest;
import com.rido.entity.BankDetails;
import com.rido.entity.Booking;
import com.rido.entity.Driver;
import com.rido.entity.ManageOtp;
import com.rido.entity.RentalBooking;
import com.rido.entity.ReturnCar.CarCondition;
import com.rido.entity.User;
import com.rido.entity.enums.RideOrderStatus;
import com.rido.entity.enums.Status;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.AdminRepository;
import com.rido.repository.BankDetailsRepository;
import com.rido.repository.BookingRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.RentalBookingRepository;
import com.rido.repository.RoleRepository;
import com.rido.repository.UserRepository;
import com.rido.repository.VehicleRepository;
import com.rido.service.BookingService;
import com.rido.service.CancellationService;
import com.rido.service.DriverService;
import com.rido.service.LocationService;
import com.rido.service.VehicleService;
import com.rido.service.impl.UserServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@CrossOrigin(origins = { "http://10.0.2.2:8080", "http://localhost:3000" })
@RestController
@RequestMapping("/driver")
public class DriverController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	AdminRepository adminRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserController userController;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private DriverService driverService;

	@Autowired
	private CancellationService cancellationService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private LocationService locationService;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private BankDetailsRepository bankDetailsRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Value("${bucketName}") // Assuming you have bucket name configured in properties file
	private String bucketName;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	ManageOtpRepository manageOtpRepository;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private RentalBookingRepository rentalBookingRepository;

//	@Autowired
//	private DriverdocumentService driverdocumentService;

	// Create a DTO class for verifying OTP
	public static class DriverVerifyOtpRequest {
		private String phoneNo;
		private String smsOtp;
		private Long driverId;

		private String forgetOtp;

		// Getters and setters

		public String getPhoneNo() {

			return phoneNo;
		}

		public String getSmsOtp() {
			return smsOtp;
		}

		public void setSmsOtp(String smsOtp) {
			this.smsOtp = smsOtp;
		}

		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}

		public Long getDriverId() {
			return driverId;
		}

		public void setDriverId(Long driverId) {
			this.driverId = driverId;
		}

		public String getForgetOtp() {
			return forgetOtp;
		}

		public void setForgetOtp(String forgetOtp) {
			this.forgetOtp = forgetOtp;
		}

	}




//	Jaleshwari
	@PostMapping("/accept-ride-request/{userId}/{driverId}")
	public ResponseEntity<String> acceptRideRequest(@PathVariable Long userId, @PathVariable Long driverId) {

		String acceptRideRequest = driverService.acceptRideRequest(userId, driverId);

		return ResponseEntity.ok(acceptRideRequest);
	}

//    Jaleshwari
	@PostMapping("/accept-ride-request-verifySmsOtpdriver")
	public ResponseEntity<String> verifySmsOtpDriver(@RequestBody DriverVerifyOtpRequest request) {
		boolean verifySmsOtp = driverService.verifySmsOtpDriver(request.getPhoneNo(), request.getSmsOtp(),
				request.getDriverId());

		if (verifySmsOtp) {
			return ResponseEntity.status(HttpStatus.SC_OK).body("OTP verified successfully");
		} else {
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Invalid OTP or Driver not found");
		}
	}

//Rishi
	@PostMapping("/{driverId}/change-password")
	public ResponseEntity<String> changePassword(@PathVariable Long driverId,
			@RequestBody DriverChangePasswordRequestDto driverChangePasswordRequestDto) {
		boolean success = driverService.driverChangePassword(driverId, driverChangePasswordRequestDto);
		if (success) {
			return ResponseEntity.ok("Password changed successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to change password");
		}
	}

//Jaleshwari
	@PostMapping("/forget-password")
	public ResponseEntity<String> sendSmsforgetPasswordVerify(@RequestParam String phoneNo) {
		String result = driverService.forgetPassword(phoneNo);
		return ResponseEntity.ok(result);
	}

//JALESHWARI
	@PostMapping("/forget-password-verify")
	public ResponseEntity<String> forgetPasswordVerify(@RequestBody DriverVerifyOtpRequest request) {
		boolean verifySmsOtp = driverService.forgetPasswordVerify(request.getDriverId(), request.getForgetOtp());
		if (verifySmsOtp) {
			return ResponseEntity.status(HttpStatus.SC_OK).body("OTP verified successfully");

		} else {
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Invalid OTP");

		}
	}

//RAHUL
	@GetMapping("/get-driver-profile/{id}")
	public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
		Driver driver = driverService.getDriverById(id);
		return ResponseEntity.ok(driver);
	}

//jyoti
	// used chnage acording to new table
	@GetMapping("/{driverId}/get-bank-details")
	public ResponseEntity<DriverDocumentResponseDto> getBankDetailsByDriverId(@PathVariable Long driverId) {
		Optional<BankDetails> bankDetailsOptional = bankDetailsRepository.findByDriver_DriverId(driverId);

		if (bankDetailsOptional.isPresent()) {
			BankDetails bankDetails = bankDetailsOptional.get();

			DriverDocumentResponseDto responseDto = new DriverDocumentResponseDto();
			responseDto.setAccountNo(bankDetails.getAccountNo());
			responseDto.setIFSCcode(bankDetails.getIfsc());
			responseDto.setAccountHolderName(bankDetails.getAccountHolderName());
			responseDto.setBranchName(bankDetails.getBranchName());

			return ResponseEntity.ok(responseDto);
		} else {
			throw new DataAccessException("Bank details  not found for this id - " + driverId) {
			};
		}
	}

//	Rahul
	@GetMapping("/get-by-driver-phoneno/{phoneno}")
	public ResponseEntity<ResponseLogin> getUserByPhoneNo(@PathVariable String phoneno) {
		ResponseLogin response = driverService.getByPhoneno(phoneno);
		if (response != null) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

//	RAHUL
	@PostMapping("/setpassword/{driverId}")
	public ResponseEntity<String> setPasswordDriver(@PathVariable Long driverId,
			@RequestBody PasswordRequestDto passwordRequest) {

		boolean setPassword = driverService.setNewPassword(driverId, passwordRequest);

		if (setPassword) {
			return ResponseEntity.ok("Password set successfully"); // Using ResponseEntity.ok()
		} else {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Password not valid"); // Using
																								// ResponseEntity.notFound()
		}
	}

	//jyoti
	@PutMapping("/update-bank-details/{driverId}")
	public ResponseEntity<String> updateDriverBankDetails(@PathVariable Long driverId, @RequestParam Long accountNo,
			@RequestParam String IFSCcode, @RequestParam String accountHolderName, @RequestParam String branchName ) {

		String result = driverService.updateDriverBankDetails(driverId, accountNo, IFSCcode, accountHolderName, branchName);

		if (result != null) {
			return ResponseEntity.ok("bank details update successfully"); // Using ResponseEntity.ok()
		} else {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("driver Id is not valid"); // Using

		}
	}

	@PostMapping("/add-driver-information/{driverId}")
	public ResponseEntity<String> verifyDriverDocument(@PathVariable Long driverId,
			@RequestParam(name = "profileImg") MultipartFile file,
			@RequestParam("DriverDataDto") String driverDataJson) {
		try {
			// Upload file to S3
			String s3Url = vehicleService.uploadFile(file);

			// Convert JSON to DriverDataDto object
			ObjectMapper objectMapper = new ObjectMapper();
			DriverDataDto driverDataDto = objectMapper.readValue(driverDataJson, DriverDataDto.class);

			// Call service method to add and verify driver document with updated
			// driverDataDto and S3 URL
			String response = driverService.addVerifyDriverDocument(driverId, driverDataDto, s3Url);

			return ResponseEntity.ok(response);
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception appropriately (e.g., log it)
			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Profile not added");
		}
	}



//ram
	@PostMapping("/go-for-next-ride/{driverId}/{bookingId}")
	public ResponseEntity<?> goForNextRideForSheduleBooking(@PathVariable Long driverId, @PathVariable Long bookingId) {

		Driver existingDriver = driverRepository.findById(driverId).get();
		Booking order = bookingRepository.findById(bookingId).get();
		order.setRideOrderStatus(RideOrderStatus.COMPLETE);
		existingDriver.setStatus(Status.AVAILABLE);
		Driver availabelDriver = driverRepository.save(existingDriver);

		return ResponseEntity.status(HttpStatus.SC_OK).body(availabelDriver);

	}

	@PostMapping("/goForNextRideForRentalBooking/{driverId}/{rentalBookingId}")
	public ResponseEntity<?> goForNextRideForRentalBooking(@PathVariable Long driverId,
			@PathVariable Long rentalBookingId) {

		Driver existingDriver = driverRepository.findById(driverId).get();
		RentalBooking order = rentalBookingRepository.findById(rentalBookingId).get();
		order.setRideOrderStatus(RideOrderStatus.COMPLETE);
		existingDriver.setStatus(Status.AVAILABLE);
		Driver availabelDriver = driverRepository.save(existingDriver);

		return ResponseEntity.status(HttpStatus.SC_OK).body(availabelDriver);

	}


//	abhilasha
	@PutMapping("/edit-driver-profile/{driverId}")
	public ResponseEntity<Driver> updateDriverProfile(
	        @PathVariable Long driverId,
	        @RequestParam(value = "profileImg", required = false) MultipartFile file,
	        @RequestParam(value = "DriverDataJson", required = false) String driverDataJson) throws Exception {

	    // Initialize variables
	    DriverDataDto driverDataDto = null;
	    String s3Url = null;

	    // Convert JSON to DriverDataDto object if provided
	    if (driverDataJson != null) {
	        ObjectMapper objectMapper = new ObjectMapper();
	        driverDataDto = objectMapper.readValue(driverDataJson, DriverDataDto.class);
	    }

	    // Check if profile image is provided
	    if (file != null && !file.isEmpty()) {
	        s3Url = vehicleService.uploadFile(file);
	        System.out.println("Image link=" + s3Url);
	    }

	    // Retrieve existing driver profile if needed
	    Driver existingDriver = driverRepository.findById(driverId)
	            .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

	    //If s3Url is still null, set it from the existing profileImgLink
	    if (s3Url == null) {
	        s3Url = existingDriver.getProfileImgLink();
	    }

	    // Call service method to update driver profile
	    Driver updatedDriver = driverService.updateDriverProfile(driverId, driverDataDto, s3Url);

	    return ResponseEntity.ok(updatedDriver);
	}

	// jyoti
	@PostMapping("/{driverId}/upload-documents")
	 public ResponseEntity<String> uploadDriverDocuments(@PathVariable Long driverId,
             @RequestParam("driverImage") MultipartFile driverImage,
             @RequestParam("DL") MultipartFile DL,
             @RequestParam("driverSignature") MultipartFile driverSignature,
             @RequestParam("adharCard") MultipartFile adharCard,
             @RequestParam("dpassbook") MultipartFile dpassbook) {
         try {
         // Upload each document to S3 bucket and get their URLs
               String driverImageUrl = vehicleService.uploadFile(driverImage);
               String dlUrl = vehicleService.uploadFile(DL);
               String driverSignatureUrl = vehicleService.uploadFile(driverSignature);
               String adharCardUrl = vehicleService.uploadFile(adharCard);
               String dpassbookUrl = vehicleService.uploadFile(dpassbook);

                   // Call the service method to save the document URLs
                    driverService.addDriverDocument(driverId, driverImageUrl, dlUrl, driverSignatureUrl, adharCardUrl, dpassbookUrl);

              return ResponseEntity.ok("Documents uploaded successfully");
         } catch (IOException e) {
        	 
             e.printStackTrace(); // Handle the exception appropriately (e.g., log it)
             return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error uploading documents");
         
         } catch (DriverNotFoundException ex) {
        	 
               return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Driver not found: " + ex.getMessage());
         }
      }  
	
	
//  Abhilasha
	@PostMapping("/returnCar/{driverId}")
	public ResponseEntity<String> returnCar(@PathVariable Long driverId, @RequestParam CarCondition carCondition,
			@RequestParam(required = false) String message) {
		try {
			if ((carCondition == CarCondition.WORST || carCondition == CarCondition.NORMAL)
					&& (message == null || message.isEmpty())) {
				// Check if the car condition is worst or normal and message is not provided
				return ResponseEntity.badRequest().body("Message is required for condition");
			}

			String result = driverService.returnCar(driverId, carCondition, message);
			if (result != null) {
				return ResponseEntity.ok(result);
			}
			return ResponseEntity.ok("Car return submitted successfully");
		} catch (DriverNotFoundException e) {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Driver not found: " + e.getMessage());
		}
	}

	@PostMapping("/repairVehicle/{driverId}")
	public ResponseEntity<String> repairVehicle(@PathVariable Long driverId, @RequestParam String message) {
		try {
			String result = driverService.repairVehicle(driverId, message);
			return ResponseEntity.ok(result);
		} catch (DriverNotFoundException e) {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(e.getMessage());
		}
	}

	

	@GetMapping("/drivers/nearby")
	public ResponseEntity<List<Driver>> getNearbyDrivers(@RequestParam("latitude") double latitude,
			@RequestParam("longitude") double longitude, @RequestParam("radius") double radius) {
		List<Driver> nearbyDrivers = driverService.findNearbyDrivers(latitude, longitude, radius);
		return ResponseEntity.ok().body(nearbyDrivers);
	}

	// jyoti
	@GetMapping("/get-driver-email-name-img")
	public ResponseEntity<ProfileDto> getDriverProfileByPhoneNo(@RequestParam String phoneNo) {
		// Retrieve driver information from the database using findByPhoneNo
		Driver driver = driverRepository.findByPhoneNo(phoneNo);

		if (driver != null) {
			// Create a ProfileDto object and populate it with driver information
			ProfileDto profileDto = new ProfileDto();
			profileDto.setName(driver.getName());
			profileDto.setProfileImgLink(driver.getProfileImgLink());
			profileDto.setEmail(driver.getEmail());
			profileDto.setId(driver.getDriverId());

			// Return the ProfileDto in the ResponseEntity with status OK
			return ResponseEntity.ok(profileDto);
		} else {
			throw new DataAccessException("This phone number is not found  ") {
			};
		}
	}

	// for update phone number
	@PostMapping("/update-phoneNumberSendOtp/{driverId}")
	public ResponseEntity<String> updatephoneNumberSendOtp(@PathVariable Long driverId,
			@RequestBody VerifyRequest request) {
		try {
			Optional<ManageOtp> manageOtp = manageOtpRepository.findByDriver_DriverId(driverId);

			if (manageOtp.isPresent()) {
				String otp = locationService.generateRandomOtp();
				System.out.println("otp=" + otp);

				ManageOtp manageOtp2 = manageOtp.get();
				manageOtp2.setUpdateOtp(otp);

				manageOtpRepository.save(manageOtp2);

				locationService.sendVerificationCode(request.getPhoneNo(), otp);

				return ResponseEntity.status(HttpStatus.SC_OK).body("Successfully sent OTP");
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Failed to send OTP");
		}
	}

	// for update phone number
	@PutMapping("/update-verify-phoneno/{driverId}")
	public ResponseEntity<String> verifyPhoneNoOtp(@PathVariable Long driverId,
			@RequestBody Map<String, String> request) {
		Optional<ManageOtp> manageOtp = manageOtpRepository.findByDriver_DriverId(driverId);

		if (manageOtp.isPresent()) {
			ManageOtp manageOtp2 = manageOtp.get();
			String updateOtp = manageOtp2.getUpdateOtp();

			if (updateOtp.equals(request.get("updateOtp"))) {
				String newPhoneNo = request.get("newPhoneNo");
				boolean success = driverService.changePhoneNoDriver(driverId, newPhoneNo);
				if (success) {
					return ResponseEntity.ok("Phone number updated successfully.");
				} else {
					return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
							.body("Failed to update phone number.");
				}
			} else {
				return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
						.body("Failed to verify phone number. Incorrect OTP.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
					.body("Failed to verify phone number. OTP not found.");
		}
	}

	// jyoti
	@PostMapping("/update-emailSendOtp/{driverId}")
	public ResponseEntity<String> updateEmailId(@PathVariable Long driverId, @RequestBody VerifyRequest request) {
		try {
			Optional<ManageOtp> manageOtp = manageOtpRepository.findByDriver_DriverId(driverId);

			if (manageOtp.isPresent()) {
				String otp = locationService.generateRandomOtp();

				ManageOtp manageOtp2 = manageOtp.get();
				manageOtp2.setUpdateOtp(otp);

				manageOtpRepository.save(manageOtp2);

				userServiceImpl.sendOtpByEmail(request.getEmail(), otp);

				return ResponseEntity.status(HttpStatus.SC_OK).body("Successfully sent OTP");
			} else {
				return null;
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Failed to send OTP");
		}
	}

	// jyoti
	@PutMapping("/update-verify-email/{driverId}")
	public ResponseEntity<String> verifyEmailOtp(@PathVariable Long driverId,
			@RequestBody Map<String, String> request) {
		Optional<ManageOtp> manageOtp = manageOtpRepository.findByDriver_DriverId(driverId);

		if (manageOtp.isPresent()) {
			ManageOtp manageOtp2 = manageOtp.get();
			String updateOtp = manageOtp2.getUpdateOtp();

			if (updateOtp.equals(request.get("updateOtp"))) {
				String newEmail = request.get("newEmail");
				boolean success = driverService.changeEmailDriver(driverId, newEmail);
				if (success) {
					return ResponseEntity.ok("email updated successfully.");
				} else {
					return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
							.body("Failed to update to email.");
				}
			} else {
				return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Failed to verify email. Incorrect OTP.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Failed to verify email. OTP not found.");
		}
	}

	// Rishi
	@GetMapping("/getRentalBookingById/{rentalBookingId}")
	public ResponseEntity<RentalBooking> getRentalBookingById(@PathVariable Long rentalBookingId) {
		RentalBooking rentalBooking = bookingService.getRentalBookingById(rentalBookingId);
		if (rentalBooking != null) {
			return ResponseEntity.ok(rentalBooking);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// Rishi
	@PutMapping("/driverAcceptRentalBooking/{rentalBookingId}/{driverId}")
	public ResponseEntity<String> driverAcceptRentalBooking(@PathVariable Long driverId,
			@PathVariable Long rentalBookingId) {
		Driver driver = driverService.getDriverById(driverId);
		RentalBooking rentalBooking = bookingService.getRentalBookingById(rentalBookingId);

		if (driver == null || rentalBooking == null) {
			return ResponseEntity.notFound().build();
		}

		if (driver.getStatus() == Status.AVAILABLE) {
			rentalBooking.setDriver(driver);
			rentalBooking.setRideOrderStatus(RideOrderStatus.APPROVED);
			rentalBooking.setHub(driver.getHub());
			rentalBookingRepository.save(rentalBooking);
			return ResponseEntity.ok("Driver Assigned Successfully");
		} else {
			return ResponseEntity.badRequest().body("Driver is ONGOING. Cannot engage another booking.");
		}
	}

	// Rishi
	@GetMapping("/getRentalBookingList")
	public List<RentalBooking> getRentalBookingList() {
		return rentalBookingRepository.findAll();
	}

	
	
	

// accept shedule ride - when driver will accept ride user will get notification
    @PostMapping("/accept-ride/{driverId}/{bookingId}")
	public ResponseEntity<UserNotificationDto> acceptRide(
	        @PathVariable Long driverId,
	        @PathVariable long bookingId) {
	    try {
	        // Call service method to save driver ID in booking table
	        UserNotificationDto notificationMessage = bookingService.saveDriverIdInBooking(driverId, bookingId);
	        return ResponseEntity.ok(notificationMessage);
	    } catch (NotFoundException e) {
	        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(null);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(null);
	    }
	}




	//jyoti - otp send when driver is arrive user location for pickup user 
    @PostMapping("/send-otp-for-user-by-driver/{bookingId}")
    public ResponseEntity<Map<String, String>> sendOTPViaSMS(@PathVariable Long bookingId) {
        Map<String, String> response = bookingService.sendVerificationCodeToUser(bookingId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("payments/{driverId}")
    public List<PaymentHistoryDto> getDriverPaymentHistory(@PathVariable Long driverId) {
        return driverService.getDriverPaymentHistory1(driverId);
    }
}
