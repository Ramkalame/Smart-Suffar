package com.rido.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rido.Exceptions.DriverNotFoundException;
import com.rido.Exceptions.ResourceNotFoundException;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.CourierDataDto;
import com.rido.dto.CourierDocumentDto;
import com.rido.dto.CourierEbikeDataDto;
import com.rido.dto.CourierProfileDto;
import com.rido.dto.CourierRideHistoryDto;
import com.rido.dto.CourierTaskListDto;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.UserPhoneRequestDto;
import com.rido.dto.VerifyRequest;
import com.rido.entity.Courier;
import com.rido.entity.ManageOtp;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.CourierBookingRepository;
import com.rido.repository.CourierRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.service.CourierService;
import com.rido.service.LocationService;
import com.rido.service.impl.UserServiceImpl;
import com.rido.utils.ApiResponse;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/courier")
public class CourierController {

	@Autowired
	private ManageOtpRepository manageOtpRepository;

	@Autowired
	private CourierService courierService;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private CourierBookingRepository courierBookingRepository;

	@Autowired
	private LocationService locationService;

	@Value("${bucketName}")
	private String bucketName;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private CourierRepository courierRepo;

	// RAHUL
	@PostMapping("/update-phoneNumberSendOtp/{courierId}")
	public ResponseEntity<String> updatephoneNumberSendOtp(@PathVariable Long courierId,
			@RequestBody VerifyRequest request) {
		try {
			Optional<ManageOtp> manageOtp = manageOtpRepository.findByCourier_CourierId(courierId);

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

	// RAHUL
	@PutMapping("/update-verify-phoneno/{courierId}")
	public ResponseEntity<String> verifyPhoneNoOtp(@PathVariable Long courierId,
			@RequestBody Map<String, String> request) {
		Optional<ManageOtp> manageOtp = manageOtpRepository.findByCourier_CourierId(courierId);

		if (manageOtp.isPresent()) {
			ManageOtp manageOtp2 = manageOtp.get();
			String updateOtp = manageOtp2.getUpdateOtp();

			if (updateOtp.equals(request.get("updateOtp"))) {
				String newPhoneNo = request.get("newPhoneNo");
				boolean success = courierService.changePhoneNoUser(courierId, newPhoneNo);
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

	// RAHUL
	@PutMapping("/update-phone-no/{courierId}")
	public ResponseEntity<String> changePhoneNoCourier(@PathVariable Long courierId,
			@RequestBody Map<String, String> requestBody) {
		if (requestBody.containsKey("newPhoneNo")) {
			try {
				String newPhoneNo = requestBody.get("newPhoneNo");
				courierService.changePhoneNoUser(courierId, newPhoneNo);
				return ResponseEntity.ok("Phone number updated successfully.");
			} catch (RuntimeException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		} else {
			return ResponseEntity.badRequest().body("Missing 'newPhoneNo' field in request body.");
		}
	}

	// RAHUL
	@PostMapping("/update-emailSendOtp/{courierId}")
	public ResponseEntity<String> updateEmailId(@PathVariable Long courierId, @RequestBody VerifyRequest request) {
		try {
			Optional<ManageOtp> manageOtp = manageOtpRepository.findByCourier_CourierId(courierId);

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

	// RAHUL
	@PutMapping("/update-verify-email/{courierId}")
	public ResponseEntity<String> verifyEmailOtp(@PathVariable Long courierId,
			@RequestBody Map<String, String> request) {
		Optional<ManageOtp> manageOtp = manageOtpRepository.findByCourier_CourierId(courierId);

		if (manageOtp.isPresent()) {
			ManageOtp manageOtp2 = manageOtp.get();
			String updateOtp = manageOtp2.getUpdateOtp();

			if (updateOtp.equals(request.get("updateOtp"))) {
				String newEmail = request.get("newEmail");
				boolean success = courierService.changeEmailUser(courierId, newEmail);
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

	// RAHUL
	@PutMapping("/{courierId}/change-password")
	public ResponseEntity<String> changePassword(@PathVariable Long courierId,
			@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
		boolean success = courierService.changePassword(courierId, changePasswordRequestDto);
		if (success) {
			return ResponseEntity.ok("Password changed successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to change password");
		}
	}

	// RAHUL
	@PostMapping("/setpassword-phonenumber/{phoneNo}")
	public ResponseEntity<ApiResponse<String>> sendSmssetPasswordVerify(@PathVariable String phoneNo) {
	    ApiResponse<String> result = courierService.forgetPassword(phoneNo);
	    if (result.isSuccess()) {
	        return ResponseEntity.ok(result);
	    } else {
	        return ResponseEntity.status(result.getStatus()).body(result);
	    }
	}

	// RAHUL
	@PostMapping("/set-password-verify/{courierId}")
	public ResponseEntity<String> forgetPasswordVerify(@PathVariable Long courierId,
			@RequestBody UserPhoneRequestDto request) {
		boolean verifySmsOtp = courierService.forgetPasswordVerify(courierId, request.getForgotOtp());
		if (verifySmsOtp) {
			return ResponseEntity.status(HttpStatus.OK).body("OTP verified successfully");

		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");

		}

	}

	// RAHUL
	@PutMapping("/setpassword/{courierId}")
	public ResponseEntity<String> setPasswordCourier(@PathVariable Long courierId,
			@RequestBody PasswordRequestDto passwordRequest) {

		boolean setpassword = courierService.setNewPassword(courierId, passwordRequest);
		if (setpassword) {
			return new ResponseEntity<>("set Password Successfully", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Password not vaild ", HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/getprofile/{hubId}/details/{courierId}")
	public ResponseEntity<ApiResponse<Courier>> getCourierProfileDetails(@PathVariable Long hubId,
			@PathVariable Long courierId) {
		Courier courier = courierService.getCourierProfileDetails(hubId, courierId);
		if (courier != null) {
			ApiResponse<Courier> response = new ApiResponse<>(courier, HttpStatus.OK, true,
					"Courier details retrieved successfully");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<Courier> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false, "Courier not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// RAHUL
	@PostMapping("/edit-courier-DocumentUpload/{CourierId}")
	public ResponseEntity<String> courierDocumentUpload(@PathVariable Long CourierId,
			@RequestParam(name = "courierDriverImage", required = false) MultipartFile courierDriverImageFile,
			@RequestParam(name = "vehicleImage", required = false) MultipartFile vehicleImageFile,
			@RequestParam(name = "registerCertificate", required = false) MultipartFile registerCertificateFile,
			@RequestParam(name = "licence", required = false) MultipartFile licenceFile,
			@RequestParam(name = "insurance", required = false) MultipartFile insuranceFile,
			@RequestParam(name = "passbook", required = false) MultipartFile passbookFile,
			@RequestParam("courierDataJson") String courierDataJson) throws Exception {

		try {
			// Convert JSON to DriverDataDto object
			ObjectMapper objectMapper = new ObjectMapper();
			CourierDataDto CourierDataDto = objectMapper.readValue(courierDataJson, CourierDataDto.class);

			String courierDriverImageUrl = null;
			String vehicleImageUrl = null;
			String registerCertificateImgUrl = null;
			String licenceImgUrl = null;
			String insuranceImgUrl = null;
			String passbookImageUrl = null;

			// Check if profile image is provided
			if (courierDriverImageFile != null && !courierDriverImageFile.isEmpty()) {

				String courierDriverImageFileName = UUID.randomUUID().toString() + "_" + courierDriverImageFile.getOriginalFilename();
				//String courierDriverImageFileName = courierDriverImageFile.getOriginalFilename();
				// String courierDriverImageFileName =
				// courierDriverImageFile.getOriginalFilename();

				File courierDriverImageConvertedFile = convertMultiPartToFile(courierDriverImageFile,
						courierDriverImageFileName);
				courierDriverImageUrl = uploadFileToS3(courierDriverImageConvertedFile, courierDriverImageFileName);
				System.out.println("Profile Image link=" + courierDriverImageUrl);
			}

			// Check if signature image is provided
			if (vehicleImageFile != null && !vehicleImageFile.isEmpty()) {

				String vehicleImageFileName = UUID.randomUUID().toString() + "_" + vehicleImageFile.getOriginalFilename();

				File vehicleImageConvertedFile = convertMultiPartToFile(vehicleImageFile, vehicleImageFileName);
				vehicleImageUrl = uploadFileToS3(vehicleImageConvertedFile, vehicleImageFileName);
				System.out.println("Signature Image link=" + vehicleImageUrl);
			}

			// Check if passbook image is provided
			if (registerCertificateFile != null && !registerCertificateFile.isEmpty()) {

				String registerCertificateImgFileName = UUID.randomUUID().toString() + "_" + registerCertificateFile.getOriginalFilename();

				File registerCertificateImgConvertedFile = convertMultiPartToFile(registerCertificateFile,
						registerCertificateImgFileName);
				registerCertificateImgUrl = uploadFileToS3(registerCertificateImgConvertedFile,
						registerCertificateImgFileName);
				System.out.println("Passbook Image link=" + registerCertificateImgUrl);
			}

			if (licenceFile != null && !licenceFile.isEmpty()) {

				String licenceImgFileName =  UUID.randomUUID().toString() + "_" +registerCertificateFile.getOriginalFilename();

				File licenceImgConvertedFile = convertMultiPartToFile(licenceFile, licenceImgFileName);
				licenceImgUrl = uploadFileToS3(licenceImgConvertedFile, licenceImgFileName);
				System.out.println("Passbook Image link=" + licenceImgUrl);
			}

			if (insuranceFile != null && !insuranceFile.isEmpty()) {

				String insuranceImgFileName =  UUID.randomUUID().toString() + "_" +insuranceFile.getOriginalFilename();

				File insuranceImgConvertedFile = convertMultiPartToFile(insuranceFile, insuranceImgFileName);
				insuranceImgUrl = uploadFileToS3(insuranceImgConvertedFile, insuranceImgFileName);
				System.out.println("Passbook Image link=" + insuranceImgUrl);
			}

			if (passbookFile != null && !passbookFile.isEmpty()) {
				String passbookImgFileName = UUID.randomUUID().toString() + "_" + passbookFile.getOriginalFilename();
				File passbookImgConvertedFile = convertMultiPartToFile(passbookFile, passbookImgFileName);
				passbookImageUrl = uploadFileToS3(passbookImgConvertedFile, passbookImgFileName);
				System.out.println("Passbook Image link=" + passbookImageUrl);
			}

			// Call service method to update hub profile
			courierService.courierDocumentUpload(CourierId, CourierDataDto, courierDriverImageUrl, vehicleImageUrl,
					registerCertificateImgUrl, licenceImgUrl, insuranceImgUrl, passbookImageUrl);
			return ResponseEntity.ok("courierDriver uploaded successfully");
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception appropriately (e.g., log it)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading documents");
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

	// RAHUL
	@GetMapping("/get-courierdocument/{courierId}")
	public ResponseEntity<CourierDocumentDto> getCourierDocument(@PathVariable Long courierId) {

		CourierDocumentDto document = courierService.getCourierDocument(courierId);

		if (document != null) {
			return ResponseEntity.ok(document);
		} else {
			throw new EntityNotFoundException("With this courierId " + courierId + "not found");
		}

	}

	// RAHUL
	@PostMapping("/setpasswordby-emailSendOtp/{courierId}")
	public ResponseEntity<String> updatePasswordcEmailId(@PathVariable Long courierId,
			@RequestBody VerifyRequest request) {
		try {
			Optional<ManageOtp> manageOtp = manageOtpRepository.findByCourier_CourierId(courierId);

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

	// RAHUL
	@PutMapping("/update-verify-emailByOtp/{courierId}")
	public ResponseEntity<String> verifyEmailOtpForPassword(@PathVariable Long courierId,
			@RequestBody Map<String, String> request) {
		Optional<ManageOtp> manageOtp = manageOtpRepository.findByCourier_CourierId(courierId);

		if (manageOtp.isPresent()) {
			ManageOtp manageOtp2 = manageOtp.get();
			String updateOtp = manageOtp2.getUpdateOtp();

			if (updateOtp.equals(request.get("updateOtp"))) {

				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Your Otp Verify with this Email");
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your Otp is Incorrect with this Email");

	}

	// RAHUL
	@PutMapping("/setpasswordByEmail/{courierId}")
	public ResponseEntity<String> setPasswordCourierByEmail(@PathVariable Long courierId,
			@RequestBody PasswordRequestDto passwordRequest) {

		boolean setpassword = courierService.setPasswordCourierByEmail(courierId, passwordRequest);
		if (setpassword) {
			return new ResponseEntity<>("set Password Successfully", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Password not vaild ", HttpStatus.NOT_FOUND);
		}

	}

	// JYOTI
	@GetMapping("/courier-task-list/{courierId}")
	public ResponseEntity<List<CourierTaskListDto>> getCourierTasksByCourierId(@PathVariable Long courierId) {
		List<CourierTaskListDto> courierTasks = courierService.getCourierTasksByCourierId(courierId);
		return ResponseEntity.ok(courierTasks);
	}

	// JYOTI  when driver will clik in pickup button otp will get for user
	@PostMapping("/send-otp/{bookingId}")
	public ResponseEntity<String> sendOtpByCourierForUser(@PathVariable Long bookingId) {
		try {
			String verificationCode = courierService.sendOtpByCourierForUser(bookingId);
			return new ResponseEntity<>(verificationCode, HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}

	}

	// RAHUL
	@GetMapping("/getCourier-Profile/{courierId}")
	public ResponseEntity<CourierProfileDto> getCourierProfile(@PathVariable Long courierId) {

		CourierProfileDto courierprofile = courierService.getCourierProfileById(courierId);

		if (courierprofile != null) {
			return ResponseEntity.status(HttpStatus.OK).body(courierprofile);
		} else {
			throw new DriverNotFoundException("courier not Found with this Id " + courierId);
		}
	}

	@PutMapping("/getCourier-Profile/{courierId}")
	public ResponseEntity<String> editCourierProfile(@PathVariable Long courierId,
			@RequestParam(name = "CourierImage", required = false) MultipartFile CourierImage,
			@RequestParam(name = "name", required = false) String name) {

		try {
			// Upload each document to S3 bucket or any other storage service and get their
			// URLs
			String insuranceImgUrl = null;
			if (CourierImage != null && !CourierImage.isEmpty()) {
				String insuranceImgFileName = UUID.randomUUID().toString() + "_" + CourierImage.getOriginalFilename();
				File insuranceImgConvertedFile = convertMultiPartToFile(CourierImage, insuranceImgFileName);
				insuranceImgUrl = uploadFileToS3(insuranceImgConvertedFile, insuranceImgFileName);
				System.out.println("Passbook Image link=" + insuranceImgUrl);
			}

			courierService.editCourierProfile(courierId, insuranceImgUrl, name);
			return ResponseEntity.ok("CourierProfile successfully");
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception appropriately (e.g., log it)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading documents");
		}
	}

	private String uploadFileToS3(MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();

		// Set the metadata for the S3 object
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());

		// Upload the file to S3 bucket
		amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead));

		// Return the S3 URL of the uploaded file
		return amazonS3.getUrl(bucketName, fileName).toString();
	}

	@PostMapping("/goForNextRide/{CourierId}")
	public ResponseEntity<String> goForNextRide(@PathVariable Long CourierId) {

		boolean success = courierService.goForNextRide(CourierId);
		if (success) {
			return ResponseEntity.status(HttpStatus.OK).body("Go For Next Ride ");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("CourierId is Not Found ");
	}

	@GetMapping("/courier-ride-history/{CourierId}")
	public ResponseEntity<List<CourierRideHistoryDto>> getCourierRideHistory(@PathVariable Long CourierId) {
		List<CourierRideHistoryDto> listOfcourierRide = courierService.getCourierRideHistory(CourierId);
		if (listOfcourierRide.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(listOfcourierRide);
	}

	// jyoti- verify Otp For Courier when courier pickup
	@PostMapping("/verify-otp/{userId}")
	public ResponseEntity<String> verifyOtpForCourier(@PathVariable long userId,
			@RequestParam("enterOtp") String enterOtp) {
		try {
			boolean isOtpVerified = courierService.verifyOtpForCourier1(userId, enterOtp);
			if (isOtpVerified) {
				return new ResponseEntity<>("OTP verified successfully", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("OTP verification failed", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	
	
	@PostMapping("/courierdriver/return/vehicle/{courierId}")
	public ResponseEntity<Object> returnTowWheelerByCourier(@PathVariable Long courierEbikeId,
			@PathVariable Long courierId, @RequestParam String bikeCondition, @RequestParam String reason, @RequestParam String bikeNo) {
		try {
			var data = courierService.returnCourierVehicle(bikeNo, courierId, bikeCondition, reason);
			ApiResponse<Object> response = new ApiResponse<Object>(data, HttpStatus.OK, true,
					"Car returned succesfully to the hub");

			return new ResponseEntity<Object>(response, HttpStatus.OK);

		} catch (ResourceNotFoundException e) {
			ApiResponse<Object> response = new ApiResponse<Object>(null, HttpStatus.NOT_FOUND, false, e.getMessage());

			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
			// something you should do

		}

	}


	@PostMapping("/courierebike-DocumentUpload/{CourierId}")
	public ResponseEntity<String> courierebikeDocumentUpload(@PathVariable Long CourierId,
			@RequestParam(name = "courierDriverImage", required = false) MultipartFile courierDriverImageFile,
			@RequestParam(name = "licence", required = false) MultipartFile licenceFile,
			@RequestParam(name = "passbook", required = false) MultipartFile passbookFile,
			@RequestParam("courierDataJson") String courierDataJson) throws Exception {

		try {

			// Retrieve the Courier entity based on CourierId
			Courier courier = courierRepo.findById(CourierId)
					.orElseThrow(() -> new IllegalArgumentException("Invalid CourierId"));

			// Check if the vehicle type is TwoWheeler
			if (courier.getVehicleType() != com.rido.entity.enums.DriverAndVehicleType.TWO_WHEELER) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Courier's vehicle type is not TwoWheeler");
			}

			// Convert JSON to CourierEbikeDataDto object
			ObjectMapper objectMapper = new ObjectMapper();
			CourierEbikeDataDto courierEbikeDataDto = objectMapper.readValue(courierDataJson,
					CourierEbikeDataDto.class);

			String courierDriverImageUrl = null;
			String licenceImgUrl = null;
			String passbookImageUrl = null;

			// Check if courier driver image is provided
			if (courierDriverImageFile != null && !courierDriverImageFile.isEmpty()) {

				String courierDriverImageFileName = UUID.randomUUID().toString() + "_" + courierDriverImageFile.getOriginalFilename();

		

				File courierDriverImageConvertedFile = convertMultiPartToFile(courierDriverImageFile,
						courierDriverImageFileName);
				courierDriverImageUrl = uploadFileToS3(courierDriverImageConvertedFile, courierDriverImageFileName);
				System.out.println("Courier Driver Image link=" + courierDriverImageUrl);
			}

			// Check if licence image is provided
			if (licenceFile != null && !licenceFile.isEmpty()) {

				String licenceImgFileName =  UUID.randomUUID().toString() + "_" +licenceFile.getOriginalFilename();
				File licenceImgConvertedFile = convertMultiPartToFile(licenceFile, licenceImgFileName);
			
				licenceImgUrl = uploadFileToS3(licenceImgConvertedFile, licenceImgFileName);
				System.out.println("Licence Image link=" + licenceImgUrl);
			}

			// Check if passbook image is provided
			if (passbookFile != null && !passbookFile.isEmpty()) {
				String passbookImgFileName = UUID.randomUUID().toString() + "_" + passbookFile.getOriginalFilename();
				File passbookImgConvertedFile = convertMultiPartToFile(passbookFile, passbookImgFileName);
				passbookImageUrl = uploadFileToS3(passbookImgConvertedFile, passbookImgFileName);
				System.out.println("Passbook Image link=" + passbookImageUrl);
			}

			// Call service method to update courier document
			courierService.courierEbikeDocumentUpload(CourierId, courierEbikeDataDto, courierDriverImageUrl,
					licenceImgUrl, passbookImageUrl);
			return ResponseEntity.ok("Courier documents uploaded successfully");
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception appropriately (e.g., log it)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading documents");
		}

	}

	@GetMapping("/accept-parcel-booking-request/{courierBookingId}")
	public ResponseEntity<Object> acceptParcelRequest(@PathVariable Long courierBookingId) {

		try {
			Object data = courierService.acceptParcelRequest(courierBookingId);
			ApiResponse<Object> response = new ApiResponse<Object>(data, HttpStatus.OK, true,
					"parcel request accepted by the driver");
			return new ResponseEntity<Object>(response, HttpStatus.OK);

		} catch (Exception e) {
			ApiResponse<Object> response = new ApiResponse<Object>(null, HttpStatus.BAD_REQUEST, false,
					"There  is somthing went wrong");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// jyoti when driver will clik in drop button
	@GetMapping("/send_otp_for_reciver/{bookingId}")

	public ResponseEntity<String> sendOtpByCourierForReciver(@PathVariable Long bookingId) {
		String otp = courierService.sendOtpByCourierForReciver(bookingId);
		if (otp != null) {
			return ResponseEntity.ok("OTP sent successfully : " + otp);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP");
		}
	}

	// jyoti
	@PostMapping("/complete_courier_and_SendMessage/{bookingId}")
	public ResponseEntity<Map<String, String>> sendSuccessMessage(@PathVariable Long bookingId) {
        try {
            // Call the service method to send success messages
            Map<String, String> responseData = courierService.sendSuccessMessage(bookingId);
            return ResponseEntity.ok(responseData);
        } catch (EntityNotFoundException ex) {
            // Handle case where booking ID is not found
            return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            // Handle other unexpected errors
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred: " + ex.getMessage()));
        }
    }

	@PostMapping("/set-driver-ongoing/{courierId}")
	public ResponseEntity<Object> setCourierDriverOngoing(@PathVariable Long courierId) {
		try {
			var data = courierService.setCourierDriverOngoing(courierId);
			ApiResponse<Object> response = new ApiResponse<Object>(data, HttpStatus.OK, true, "Driver is ongoing now");
			return new ResponseEntity<Object>(response, HttpStatus.OK);

		} catch (DriverNotFoundException e) {
			ApiResponse<Object> response = new ApiResponse<Object>(null, HttpStatus.BAD_REQUEST, true, e.getMessage());
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);

		}


		}

	

		@GetMapping("/getCourierByPhoneNo/{phoneNo}")
	public ResponseEntity<ResponseLogin> getCourierByPhoneNo(@PathVariable String phoneNo) {
		ResponseLogin response = courierService.getCourierByPhoneNo(phoneNo);
		if (response != null) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.notFound().build();
		}

	}
}
