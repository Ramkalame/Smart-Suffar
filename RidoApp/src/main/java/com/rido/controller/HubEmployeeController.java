package com.rido.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.rido.Exceptions.EmployeePaymentNotFoundException;
import com.rido.Exceptions.HubNotFoundException;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.EmployeePaymentDto;
import com.rido.dto.HubDataDto;
import com.rido.dto.HubEmployeeDto;
import com.rido.dto.HubEmployeeProfileEditDto;
import com.rido.dto.PasswordChangeRequestDto;
import com.rido.dto.PaymentHistoryDto;
import com.rido.dto.ProfileDto;
import com.rido.entity.HubEmployee;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.HubEmployeeRepository;
import com.rido.service.BookingService;
import com.rido.service.HubEmployeeService;
import com.rido.service.VehicleService;

import jakarta.persistence.EntityNotFoundException;

@CrossOrigin(origins = { "http://10.0.2.2:8080", "http://localhost:3000" })
@RestController
@RequestMapping("/hub/employee")
public class HubEmployeeController {

	@Autowired
	private HubEmployeeService hubEmployeeService;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private HubEmployeeRepository hubEmployeeRepository;

	@Value("${bucketName}") // Assuming you have bucket name configured in properties file
	private String bucketName;
	
	@Autowired
	private BookingService bookingService;

	// RAHUL
	@PutMapping("/edit-hubEmployee-profile/{hubId}")
	public ResponseEntity<String> updateHubEmployeeProfile(@PathVariable Long hubId,
			@RequestParam(name = "profileImg", required = false) MultipartFile file,
			@RequestParam(name = "signatureImg", required = false) MultipartFile signatureImgFile,
			@RequestParam(name = "passbookImg", required = false) MultipartFile passbookImgFile,
			@RequestParam("hubDataJson") String hubDataJson) throws Exception {

		try {
			// Convert JSON to DriverDataDto object
			ObjectMapper objectMapper = new ObjectMapper();
			HubDataDto HubDataDto = objectMapper.readValue(hubDataJson, HubDataDto.class);

			// Generate a unique file name for the image
			if (file != null && !file.isEmpty()) {
				String fileName =  UUID.randomUUID().toString() + "_" +file.getOriginalFilename();

				// Convert MultipartFile to File
				File convertedFile = convertMultiPartToFile(file, fileName);

				// Upload the file to S3 bucket
				String s3Url = uploadFileToS3(convertedFile, fileName);
				System.out.println("imagelink=" + s3Url);
			}
			String signatureImgFileName = UUID.randomUUID().toString() + "_" + signatureImgFile.getOriginalFilename();
			File signatureImgConvertedFile = convertMultiPartToFile(signatureImgFile, signatureImgFileName);
			String signatureImageUrl = uploadFileToS3(signatureImgConvertedFile, signatureImgFileName);
			System.out.println("Signature Image link=" + signatureImageUrl);

			String passbookImgFileName = UUID.randomUUID().toString() + "_" + passbookImgFile.getOriginalFilename();
			File passbookImgConvertedFile = convertMultiPartToFile(passbookImgFile, passbookImgFileName);
			String passbookImageUrl = uploadFileToS3(passbookImgConvertedFile, passbookImgFileName);
			System.out.println("Passbook Image link=" + passbookImageUrl);

			String s3Url = null;
			if (s3Url == null) {
				// Retrieve existing driver profile
				HubEmployee existingDriver = hubEmployeeRepository.findById(hubId)
						.orElseThrow(() -> new EntityNotFoundException("Hub not found"));

				// Set s3Url from the existing profileImgLink
				s3Url = existingDriver.getProfileImgLink();
			}

			if (signatureImageUrl == null) {
				// Retrieve existing driver profile
				HubEmployee existingDriver = hubEmployeeRepository.findById(hubId)
						.orElseThrow(() -> new EntityNotFoundException("Hub not found"));

				// Set s3Url from the existing profileImgLink
				signatureImageUrl = existingDriver.getSignatuePic();
			}

			if (passbookImageUrl == null) {
				// Retrieve existing driver profile
				HubEmployee existingDriver = hubEmployeeRepository.findById(hubId)
						.orElseThrow(() -> new EntityNotFoundException("Hub not found"));

				// Set s3Url from the existing profileImgLink
				passbookImageUrl = existingDriver.getSignatuePic();
			}

			// Call service method to update driver profile
			HubEmployee response = hubEmployeeService.updateHubEmployeeProfile(hubId, HubDataDto, s3Url,
					signatureImageUrl, passbookImageUrl);
			return ResponseEntity.status(HttpStatus.SC_ACCEPTED).body("Hub Employee Profile update successfully");
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception appropriately (e.g., log it)
			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error in update hub employee ");
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

	@PostMapping("/setpassword/{PhoneNo}")
	public ResponseEntity<String> setPasswordHubEmployee(@PathVariable String PhoneNo,
			@RequestBody PasswordChangeRequestDto passwordRequest) {

		boolean setpassword = hubEmployeeService.setNewPasswordForHubEmployee(PhoneNo, passwordRequest);
		if (setpassword) {
			return ResponseEntity.status(HttpStatus.SC_OK).body("set Password Successfully");

		} else {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Password not vaild ");
		}
	}

	// RAHUL
	@GetMapping("/edit-hubemp-profile/{hubempId}")
	public ResponseEntity<HubEmployeeProfileEditDto> getHubEmployeeProfile(@PathVariable Long hubempId) {
		HubEmployeeProfileEditDto getProfile = hubEmployeeService.getHubEmployeeProfile(hubempId);
		if (getProfile == null) {
			throw new EntityNotFoundException("HubEmployee Profile not found " + hubempId);
		}
		return ResponseEntity.ok(getProfile);
	}

	@PutMapping("/change-byOldPassword/{hubId}")
	public ResponseEntity<?> setPasswordByOldPassword(@PathVariable Long hubId,
			@RequestBody ChangePasswordRequestDto changepassword) {

		try {
			boolean passwordChanged = hubEmployeeService.changePasswordByOldPassword(hubId, changepassword);
			if (passwordChanged) {
				return ResponseEntity.ok("Password changed successfully.");
			} else {
				return ResponseEntity.badRequest().body("Old password is incorrect.");
			}
		} catch (HubNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/get-by-hubemp-phoneno/{phoneno}")
	public ResponseEntity<ResponseLogin> getUserByPhoneNo(@PathVariable String phoneno) {
		ResponseLogin response = hubEmployeeService.getByPhoneno(phoneno);
		if (response != null) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// jyoti
	@PostMapping("/add-employee-information/{hubEmployeeId}")
	public ResponseEntity<String> verifyDriverDocument(@PathVariable Long hubEmployeeId,
			@RequestParam(name = "profileImgLink") MultipartFile profileImgLink,
			@RequestParam(name = "passbookImg") MultipartFile passbookImg,
			@RequestParam(name = "EmpSignature") MultipartFile EmpSignature,
			@RequestParam("HubEmployeeDto") String hubEmployeeDto) {
		try {
			String profileimgUrl = uploadFileToS3(profileImgLink);

			String EmpSignatureUrl = uploadFileToS3(EmpSignature);
			String passbookImgUrl = uploadFileToS3(passbookImg);
			// Convert JSON to DriverDataDto object
			ObjectMapper objectMapper = new ObjectMapper();
			HubEmployeeDto hubEmployee = objectMapper.readValue(hubEmployeeDto, HubEmployeeDto.class);

			// Call service method to add and verify driver document with updated
			// driverDataDto and S3 URL
			String response = hubEmployeeService.addEmployeeData(hubEmployeeId, hubEmployee, profileimgUrl,
					EmpSignatureUrl, passbookImgUrl);

			return ResponseEntity.ok(response);
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception appropriately (e.g., log it)
			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Profile not added");
		}
	}

	private String uploadFileToS3(MultipartFile file) throws IOException {
		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

		// Set the metadata for the S3 object
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());

		// Upload the file to S3 bucket
		amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead));

		// Return the S3 URL of the uploaded file
		return amazonS3.getUrl(bucketName, fileName).toString();
	}

	@GetMapping("/get-hub-employee/{hubEmployeeId}")
	public HubEmployeeDto getHubEmployeeDetails(@PathVariable Long hubEmployeeId) {
		// Retrieve the hub employee details from the repository
		HubEmployee hubEmployee = hubEmployeeRepository.findById(hubEmployeeId).orElseThrow(
				() -> new EntityNotFoundException("Hub Employee with ID " + hubEmployeeId + " not found."));

		// Map the HubEmployee entity to a DTO object
		HubEmployeeDto hubEmployeeDto = new HubEmployeeDto();
		hubEmployeeDto.setHubEmployeeId(hubEmployee.getHubEmployeeId());
		hubEmployeeDto.setName(hubEmployee.getName());
		hubEmployeeDto.setEmail(hubEmployee.getEmail());
		hubEmployeeDto.setPhoneNo(hubEmployee.getPhoneNo());
		hubEmployeeDto.setProfileImgLink(hubEmployee.getProfileImgLink());
		hubEmployeeDto.setPassbookImg(hubEmployee.getPassbookImg());
		hubEmployeeDto.setEmpSignature(hubEmployee.getEmpSignature());
		hubEmployeeDto.setAdharNo(hubEmployee.getAdharNo());
		hubEmployeeDto.setPanNo(hubEmployee.getPanNo());
		hubEmployeeDto.setAddress(hubEmployee.getAddress());

		return hubEmployeeDto;
	}

	// RAHUL
	@GetMapping("payment-history/{EmpId}")
	public ResponseEntity<List<PaymentHistoryDto>> getEmployeePayementHistoryByEmpId(@PathVariable Long EmpId)
			throws Exception {

		List<PaymentHistoryDto> EmpPayment = hubEmployeeService.getEmployeePayementHistoryByEmpId(EmpId);
		if (EmpPayment == null) {
			throw new DataAccessException("This Email is not found - " + EmpId) {
			};
		}
		return ResponseEntity.ok(EmpPayment);
	}



	// jyoti
	@GetMapping("/hub-employee-payemnt-list/{hubId}")
	public ResponseEntity<List<EmployeePaymentDto>> getEmployeePaymentDetailsByHubId(@PathVariable Long hubId)
			throws HubNotFoundException {
		List<EmployeePaymentDto> payments = hubEmployeeService.getEmployeePaymentDetailsByHubId(hubId);
		if (payments.isEmpty()) {

			throw new DataAccessException("Hub with ID " + hubId + " not found") {
			};
		}
		return ResponseEntity.ok(payments);
	}

	// jyoti
//	@GetMapping("/hub-employee-payment-details/{hubEmployeeId}")
//	public ResponseEntity<EmployeePaymentDto> getEmployeePaymentDetails(@PathVariable Long hubEmployeeId,
//			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
//
//		EmployeePaymentDto employeePaymentDto = hubEmployeeService.getEmployeePaymentDetails(hubEmployeeId, date);
//		if (employeePaymentDto == null) {
//			throw new DataAccessException(" Employee is not found for this Id - " + hubEmployeeId) {
//			};
//		}
//		return ResponseEntity.ok(employeePaymentDto);
//	}

	@GetMapping("/hub-employee-payment-details/{employeeOrderId}")
	public ResponseEntity<EmployeePaymentDto> getEmployeePaymentDetails(@PathVariable Long employeeOrderId)
			throws EmployeePaymentNotFoundException {
		EmployeePaymentDto employeePaymentDto = hubEmployeeService.getEmployeePaymentDetails(employeeOrderId);
		return ResponseEntity.ok(employeePaymentDto);
	}

//		Jaleshwari
	@GetMapping("/employeeCount/{hubId}")
	public ResponseEntity<Integer> getEmployeeCountList(@PathVariable Long hubId) {
		Integer employeeCount = hubEmployeeService.getHubEmployeeCount(hubId);

		if (employeeCount != null) {
			return ResponseEntity.ok(employeeCount);
		} else {
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(null);
		}
	}

//	Jaleshwari
	@GetMapping("/hubemployeeemail/{email}")
	public ResponseEntity<ProfileDto> getProfileByEmail(@PathVariable String email) {
		try {
			ProfileDto profileDto = hubEmployeeService.getProfileByEmail(email);
			return ResponseEntity.ok(profileDto);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(null);
		}
	}
  //  --------------------------------------Dashboard-----------------------------------------
	
	
	
}
