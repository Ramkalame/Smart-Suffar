package com.rido.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rido.Exceptions.BusinessException;
import com.rido.Exceptions.DriverNotFoundException;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.dto.CarMaintenanceDto;
import com.rido.dto.CarRepairDto;
import com.rido.dto.DriverChangePasswordRequestDto;
import com.rido.dto.DriverDataDto;
import com.rido.dto.DriverDocumentResponseDto;
import com.rido.dto.HubAddressDTO;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.PaymentHistoryDto;
import com.rido.entity.BankDetails;
import com.rido.entity.Booking;
import com.rido.entity.CarRepair;
import com.rido.entity.Driver;
import com.rido.entity.DriverDocument;
import com.rido.entity.DriverLocation;
import com.rido.entity.Hub;
import com.rido.entity.HubLocation;
import com.rido.entity.ManageOtp;
import com.rido.entity.ReturnCar;
import com.rido.entity.ReturnCar.CarCondition;
import com.rido.entity.User;
import com.rido.entity.UserIdentity;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.Status;
import com.rido.entity.enums.VehicleAssignStatus;
import com.rido.entity.enums.VehicleStatus;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.BankDetailsRepository;
import com.rido.repository.BookingRepository;
import com.rido.repository.CarRepairRepository;
import com.rido.repository.CourierBookingRepository;
import com.rido.repository.DriverDocumentRepository;
import com.rido.repository.DriverLocationRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubLocationRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.PaymentRepository;
import com.rido.repository.ReturnCarRepository;
import com.rido.repository.UserIdentityRepository;
import com.rido.repository.UserRepository;
import com.rido.repository.VehicleRepository;
import com.rido.service.DriverService;
import com.rido.service.LocationService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DriverServiceImpl implements DriverService {

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private LocationService locationService;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	public ManageOtpRepository manageOtpRepository;

	@Autowired
	DriverDocumentRepository driverDocumentRepository;

	@Autowired
	public UserServiceImpl userServiceImple;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	private LocationImpl locationImpl;

//	@Value("${project.image}")
//	private String path;

	@Autowired
	private HubLocationRepository hubLocationRepository;

	@Autowired
	private CarRepairRepository carRepairRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private UserIdentityRepository userIdentityRepository;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private DriverLocationRepository driverLocationRepository;

	@Autowired
	private ReturnCarRepository returnCarRepository;

	@Autowired
	private CourierBookingRepository courierBookingRepository;

	@Override
	public List<Driver> getAvailableDrivers() {
		return driverRepository.findByStatus(Status.AVAILABLE);
	}

	@Autowired
	private BookingRepository bookingRepository;

	@Override
	public String acceptRideRequest(Long userId, Long driverId) {

		Driver driver = driverRepository.findById(driverId).orElseThrow();

		Optional<User> findById = userRepo.findById(userId);

		if (findById != null) {
			User user = findById.get();

			String phoneNo = user.getPhoneNo();

			String generateVerificationCode = locationImpl.generateVerificationCode();

			locationImpl.sendVerificationCode(phoneNo, generateVerificationCode);

			driver.setUserPhoneNoOtp(generateVerificationCode);

//			ManageOtp manageOtp= new ManageOtp();
//		    manageOtp.setSmsOtp(generateVerificationCode);
//		    manageOtp.setPhoneNoVerified(false);
//		    manageOtp.setUserId(user);
//		    manageOtpRepository.save(manageOtp);
			driver.setStatus(Status.ONGOING);
			driverRepository.save(driver);

			return "Accept Request";

		}
		return "Reject Request";
	}

	@Override
	public boolean verifySmsOtpDriver(String contactNo, String smsOtp, Long driverId) {
		User byContact = userRepo.findByPhoneNo(contactNo).orElseThrow();

		Driver driver = driverRepository.findById(driverId).orElseThrow();

		if (byContact != null) {
			// Check if the provided OTP matches the OTP associated with the user

			if (driver.getUserPhoneNoOtp().equals(smsOtp))

			{
				System.out.println("line 111");
// 		        	driver.setUserPhoneNoAppliStatus("smsS
				driverRepository.save(driver);
				return true;
			}
		}

		return false;
	}

	@Override
	public Driver editCabDriverProfile(Driver driver) throws UserNotFoundException {

		Driver existingDriver = driverRepository.findById(driver.getDriverId()).get();
		System.out.println(existingDriver + "line 167");
		if (existingDriver.equals(null)) {
			throw new UserNotFoundException("User with this is id " + driver.getDriverId() + " not found");
		} else {
			existingDriver.setName(driver.getName());
			existingDriver.setEmail(driver.getEmail());
			existingDriver.setPhoneNo(driver.getPhoneNo());
			existingDriver.setAddress(driver.getAddress());
			existingDriver.setUsername(driver.getUsername());

			System.out.println(existingDriver + "line177");
			Driver updatedDriver = driverRepository.save(existingDriver);
			return updatedDriver;
		}
	}

	@Override
	public Driver findById(Long id) {
		return driverRepository.findById(id).orElse(null);
	}

	@Override
	public String forgetPassword(String phoneNo) {

		String otp = locationImpl.generateVerificationCode();
		Driver driver = driverRepository.findByPhoneNo(phoneNo);

		Long driverId = driver.getDriverId();
		Optional<ManageOtp> driverId1 = manageOtpRepository.findByDriver_DriverId(driverId);

		if (driverId1.isEmpty()) {
			return "User not found";
		} else {
			ManageOtp manageOtp = driverId1.get();

			manageOtp.setForgetOtp(otp);
			manageOtp.setDriver(manageOtp.getDriver());
			manageOtpRepository.save(manageOtp);

			locationImpl.sendVerificationCode(phoneNo, otp);
			return "Verification code sent successfully";
		}

	}

	// get profile 106
	@Override
	public Driver getDriverById(Long id) {
		return driverRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + id));
	}

	// 106 chnaged acording to new table

	@Override
	public String addVerifyDriverDocument(Long driverId, DriverDataDto driverDataDto, String s3Url) throws IOException {
		Optional<Driver> optionalDriver = driverRepository.findById(driverId);

		if (optionalDriver.isPresent()) {
			Driver existingDriver = optionalDriver.get();

			// Update driver details
			existingDriver.setName(driverDataDto.getName());
			existingDriver.setAdharNo(driverDataDto.getAdharNo());
			existingDriver.setDlNumber(driverDataDto.getDlNumber());
			existingDriver.setPanNo(driverDataDto.getPanNo());
			existingDriver.setPhoneNo(driverDataDto.getPhoneNo());
			existingDriver.setAltPhoneNumber(driverDataDto.getAltPhoneNumber());
			existingDriver.setAddress(driverDataDto.getAddress());
			existingDriver.setProfileImgLink(s3Url);

			// Save updated driver details
			Driver savedDriver = driverRepository.save(existingDriver);

			UserIdentity driverUserIdentity = userIdentityRepository.findByEmail(existingDriver.getEmail())
					.orElseThrow();

			driverUserIdentity.setPhoneNo(driverDataDto.getPhoneNo());
			driverUserIdentity.setName(driverDataDto.getName());

			// Create and save driver bank details
			BankDetails bankDetails = new BankDetails();
			bankDetails.setAccountHolderName(driverDataDto.getAccountHolderName());
			bankDetails.setAccountNo(driverDataDto.getAccountNo());
			bankDetails.setIfsc(driverDataDto.getIfsc());
			bankDetails.setBranchName(driverDataDto.getBranchName());
			bankDetails.setDriver(savedDriver); // Set driver id

			// Save driver bank details
			bankDetailsRepository.save(bankDetails);

			return "Data saved successfully";
		} else {
			// Handle the case where the driver with the given ID is not found
			throw new EntityNotFoundException("Driver with ID " + driverId + " not found.");
		}
	}

	@Override
	public Driver signWithPhoneDriver(Driver driver) {

		if (driverRepository.existsByPhoneNo(driver.getPhoneNo())) {
			// Return a message indicating that the phone number is already registered
			throw new RuntimeException("Phone number already exists");
		}

		System.out.println("line 477");
		// Generate OTP
		String otpSms = locationImpl.generateVerificationCode();
		System.out.println("otpSms=" + otpSms);

		// Send OTP to the provided phone number
		locationImpl.sendVerificationCode(driver.getPhoneNo(), otpSms);

		System.out.println(driver.getPhoneNo() + "line 492");

		Driver savedDriver = driverRepository.save(driver);

		// Set OTP and verification status in the driver object
		ManageOtp manageOtp = new ManageOtp();
		manageOtp.setDriver(savedDriver);
		manageOtp.setRegisterOtp(otpSms);
		manageOtpRepository.save(manageOtp);

		System.out.println(savedDriver + "line 492");

		return savedDriver;

	}

	@Override
	public boolean verifySmsOtp(Long driverId, String otp) {

		Optional<ManageOtp> manageOtp = manageOtpRepository.findByDriver_DriverId(driverId);

		if (manageOtp != null) {
			ManageOtp manageOtp2 = manageOtp.get();
			String registerOtp = manageOtp2.getRegisterOtp();
			if (registerOtp.equals(otp)) {
				System.out.println("line 568");
				return true;
			}

		}
		return false;
	}

	@Override
	public Driver authenticate(String phoneNo, String password) {
		// Find driver by phone number
		Driver driver = driverRepository.findByPhoneNo(phoneNo);

		// Check if driver exists and password matches
		if (driver != null && passwordEncoder.matches(password, driver.getPassword())) {
			return driver; // Authentication successful
		} else {
			return null; // Authentication failed
		}
	}

	@Override
	public int getTotalOngoingDrivers() {
		List<Driver> ongoingDrivers = findOngoingDriver();
		return ongoingDrivers.size();
	}

	@Override
	public List<Driver> findOngoingDriver() {
		List<Driver> ongoingDrivers = driverRepository.findByStatus(Status.ONGOING);
		System.out.println("ongoingDrivers=" + ongoingDrivers);

		if (ongoingDrivers.isEmpty()) {
			throw new BusinessException("601", "No ongoing drivers found.");
		}
		return ongoingDrivers;
	}

	@Override
	public ResponseLogin getByPhoneno(String phoneno) {
		// TODO Auto-generated method stub
		Driver driver = driverRepository.findByPhoneNo(phoneno);
		if (driver != null) {

			ResponseLogin response = new ResponseLogin();
			response.setUserId(driver.getDriverId());
			response.setEmail(driver.getEmail());
			response.setPhoneNo(driver.getPhoneNo());
			response.setMessage("Success");
			response.setName(driver.getName());
			response.setHubId(driver.getHub().getHubId());
//			response.setApproveStatus(driver.getApproveStatus());
			response.setDriverType(driver.getDriverType());
			response.setStatus(driver.getStatus());
			response.setUserName(driver.getUsername());
			return response;

		}

		return null;
	}

	public boolean setNewPassword(Long driverId, PasswordRequestDto passwordDto) {

		Driver driver = driverRepository.findById(driverId).orElse(null);

		if (driver != null) {

			if (userServiceImple.isValidPassword(passwordDto.getNewpassword(), passwordDto.getConfirmPassword())) {

				Optional<UserIdentity> userIdentityOptional = userIdentityRepository.findByPhoneNo(driver.getPhoneNo());

				if (userIdentityOptional.isPresent()) {
					UserIdentity userIdentity = userIdentityOptional.get();

					if (driver.getPhoneNo().equals(userIdentity.getPhoneNo())) {

						String encodedPassword = passwordEncoder.encode(passwordDto.getNewpassword());

						driver.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						driverRepository.save(driver);
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

	// @Override
	// public DriverDocument getDriverDocument(Long driverId) {
	// return driverDocumentRepository.findByDriverId(driverId);
	// }

	// bank update // chnaged acording to new table
	@Override
	public String updateDriverBankDetails(Long driverId, Long accountNo, String IFSCcode, String accountHolderName,
			String branchName) {

		Optional<BankDetails> optionalDriver = bankDetailsRepository.findByDriver_DriverId(driverId);

		System.out.println(optionalDriver);

		if (optionalDriver.isPresent()) {
			BankDetails existingDriver = optionalDriver.get();
			existingDriver.setAccountNo(accountNo);
			existingDriver.setIfsc(IFSCcode);
			existingDriver.setAccountHolderName(accountHolderName);
			existingDriver.setBranchName(branchName);
			bankDetailsRepository.save(existingDriver);// save(existingDriver);

			return "Driver details updated successfully";
		} else {
			return "Driver not found with ID: " + driverId;
		}
	}

	@Override
	public boolean forgetPasswordVerify(Long driverId, String forgetOtp) {
		Optional<ManageOtp> manageOtp = manageOtpRepository.findByDriver_DriverId(driverId);

//				System.out.println(manageOtp + "byemail 106");
		if (manageOtp != null) {
			ManageOtp manageOtp2 = manageOtp.get();
			String smsOtp = manageOtp2.getForgetOtp();
			if (smsOtp.equals(forgetOtp)) {
				System.out.println(smsOtp + "byemail 667");
				return true;
			}

		}

		return false;
	}

	// get bank details 106 //chnaged acording to new table //get bnak details
	@Override
	public DriverDocumentResponseDto getBankDetailsByDriverId(Long driverId) {

		BankDetails driverBankDetails = bankDetailsRepository.findById(driverId)
				.orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));

		// Now, create and populate the DTO with the required fields
		DriverDocumentResponseDto bankDetailsDTO = new DriverDocumentResponseDto();
		bankDetailsDTO.setAccountNo(driverBankDetails.getAccountNo());// setAccountNo(driverBankDetails.getAccountNo());
		bankDetailsDTO.setIFSCcode(driverBankDetails.getIfsc());
		bankDetailsDTO.setAccountHolderName(driverBankDetails.getAccountHolderName());
		return bankDetailsDTO;
	}

	@Override
	public boolean driverChangePassword(Long driverId, DriverChangePasswordRequestDto driverChangePasswordRequestDto) {
		// TODO Auto-generated method stub
		Driver driver = driverRepository.findById(driverId).orElse(null);

		if (driver != null
				&& passwordEncoder.matches(driverChangePasswordRequestDto.getOldPassword(), driver.getPassword())) {

			Optional<UserIdentity> userIdentityOptional = userIdentityRepository.findByPhoneNo(driver.getPhoneNo());
			System.out.println(userIdentityOptional.get() + " user phoneno line 148");
			// Check if the new password and confirm password match
			if (userIdentityOptional.isPresent()) {
				UserIdentity userIdentity = userIdentityOptional.get();

				// Optional: You might want to log userIdentity to ensure it's retrieved
				// correctly
				System.out.println("UserIdentity: " + userIdentity);

				System.out.println(userIdentity.getPhoneNo());

				if (driver.getPhoneNo().equals(userIdentity.getPhoneNo())) {
					if (driverChangePasswordRequestDto.getNewPassword()
							.equals(driverChangePasswordRequestDto.getConfirmPassword())) {
						String encodedPassword = passwordEncoder
								.encode(driverChangePasswordRequestDto.getNewPassword());

						driver.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						driverRepository.save(driver);
						userIdentityRepository.save(userIdentity);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String getVehicleNumberByDriverId(Long driverId) {
		Driver driver = driverRepository.findById(driverId).orElse(null);
		System.out.println("Driver=" + driver);
		if (driver != null) {
			Vehicle vehicle = driver.getVehicle();
			System.out.println("vehile=" + vehicle);
			// Vehicle
			// vehicle=vehicleRepository.findByVechileId(driver.getVehicle().getVehicleId());
			return vehicle.getVehicleNo();
		}
		return null;
	}

	@Override
	public Driver updateDriverProfile(Long driverId, DriverDataDto driverDataDto, String s3Url) throws Exception {
		Optional<Driver> optionalDriver = driverRepository.findById(driverId);
		if (optionalDriver.isPresent()) {
			Driver driver = optionalDriver.get();
			// Update driver's profile data
			if (driverDataDto.getName() != null && !driverDataDto.getName().isEmpty()) {
				driver.setName(driverDataDto.getName());
			}
			if (driverDataDto.getAdharNo() != null) {
				driver.setAdharNo(driverDataDto.getAdharNo());
			}

			if (driverDataDto.getAltPhoneNumber() != null && !driverDataDto.getAltPhoneNumber().isEmpty()) {
				driver.setAltPhoneNumber(driverDataDto.getAltPhoneNumber());
			}
			// Check and update PAN number if provided in the DTO
			if (driverDataDto.getPanNo() != null && !driverDataDto.getPanNo().isEmpty()) {
				driver.setPanNo(driverDataDto.getPanNo());
			}
			if (driverDataDto.getAddress() != null && !driverDataDto.getAddress().isEmpty()) {
				driver.setAddress(driverDataDto.getAddress());
			}
			if (driverDataDto.getDlNumber() != null && !driverDataDto.getDlNumber().isEmpty()) {
				driver.setDlNumber(driverDataDto.getDlNumber());
			}
			// Update profile image link if provided
			if (s3Url != null && !s3Url.isEmpty()) {
				driver.setProfileImgLink(s3Url);
			}
			// Save the updated driver entity
			return driverRepository.save(driver);
		} else {
			throw new Exception("Driver not found with id: " + driverId);
		}
	}

//	public Admin updateHubProfile(AdminProfileEditDto adminDataJson, String s3Url) {
//	    // Retrieve the admin from the database
//	    Admin admin = adminRepository.findById(adminDataJson.getAdminId()).get();
//
//	    // Update admin data if provided in the DTO
//	    if (adminDataJson.getName() != null && !adminDataJson.getName().isEmpty()) {
//	        admin.setName(adminDataJson.getName());
//	    }
//	    if (adminDataJson.getEmail() != null && !adminDataJson.getEmail().isEmpty()) {
//	        admin.setEmail(adminDataJson.getEmail());
//	    }
//	    if (adminDataJson.getPhoneNo() != null && !adminDataJson.getPhoneNo().isEmpty()) {
//	        admin.setPhoneNo(adminDataJson.getPhoneNo());
//	    }
//	    if (adminDataJson.getAddress() != null && !adminDataJson.getAddress().isEmpty()) {
//	        admin.setAddress(adminDataJson.getAddress());
//	    }
//
//	    // Update profile image link if provided
//	    if (s3Url != null && !s3Url.isEmpty()) {
//	        admin.setProfileImgLink(s3Url);
//	    }
//
//	    // Save the updated admin
//	    return adminRepository.save(admin);
//	}
//	

	@Override
	public void addDriverDocument(Long driverId, String driverPhotoUrl, String dLUrl, String driverSignatureUrl,
			String adharCardUrl, String dpassbookUrl) {

		Optional<Driver> optionalDriver = driverRepository.findById(driverId);

		// driverPhoto
		if (optionalDriver.isPresent()) {
			Driver driver = optionalDriver.get();
			DriverDocument driverDocument = new DriverDocument();

			driverDocument.setDriver(driver);
			driverDocument.setDriverImage(driverPhotoUrl);
			driverDocument.setDL(dLUrl);
			driverDocument.setDriverSignature(driverSignatureUrl);
			driverDocument.setAdharCard(adharCardUrl);
			driverDocument.setDpassbook(dpassbookUrl);
			driverDocumentRepository.save(driverDocument);

		} else {
			throw new DriverNotFoundException("Driver with ID " + driverId + " not found");
		}

	}

	@Override
	public String returnCar(Long driverId, CarCondition carCondition, String message) {

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDateTime = LocalDate.now().atStartOfDay(); // Midnight of today
		LocalDateTime endDateTime = startDateTime.plusDays(1); // Midnight of tomorrow

		ReturnCar returnCar = returnCarRepository
				.findByDriver_DriverIdAndAssignTimeBetween(driverId, startDateTime, endDateTime)
				.orElseThrow(() -> new IllegalArgumentException("ReturnCar not found"));

		Vehicle vehicle = returnCar.getVehicle();
		vehicle.setVehicleStatus(VehicleStatus.AVAILABLE);

		Driver driver = returnCar.getDriver();
		driver.setVehicleAssignStatus(VehicleAssignStatus.CHECKOUT);
		driver.setStatus(Status.AVAILABLE);
		driver.setVehicle(null);

		returnCar.setReturnTime(LocalDateTime.now());
		returnCar.setCarCondition(carCondition);

		// Set message if car condition is worst or normal
		if (carCondition.WORST.equals(carCondition) || CarCondition.NORMAL.equals(carCondition)) {
			// Check if message is provided for worst or normal car condition
			if (message == null) {
				return "Message is required for worst or normal car condition";
			}
			returnCar.setMessage(message);
		}

		if (CarCondition.WORST.equals(carCondition)) {
			CarRepair carRepair = new CarRepair();
			carRepair.setDriver(returnCar.getDriver());
			carRepair.setVehicleName(returnCar.getVehicleName());
			carRepair.setReturnTime(LocalDateTime.now());
			carRepair.setVehicleNo(returnCar.getVehicleNo());
			carRepair.setHub(returnCar.getHub());
			carRepair.setMessage(message);
			carRepairRepository.save(carRepair);
		}

		driverRepository.save(driver);
		vehicleRepository.save(vehicle);
		returnCarRepository.save(returnCar);

		return "sucessfully change";
	}

	@Override
	public String repairVehicle(Long driverId, String message) {

		Driver driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new DriverNotFoundException("Driver with ID " + driverId + " not found"));

		Vehicle vehicle = driver.getVehicle();
		if (vehicle == null) {
			return "Vehicle not found";
		}

		driver.setStatus(Status.IN_COMPLETED);

		vehicle.setVehicleStatus(VehicleStatus.MAINTENANCE);
		driver.setVehicleAssignStatus(VehicleAssignStatus.CHECKOUT);

		CarRepair carRepair = new CarRepair();
		carRepair.setDriver(driver);
		carRepair.setMessage(message);
		carRepair.setReturnTime(LocalDateTime.now());
		carRepair.setVehicleName(vehicle.getVehicleName());
		carRepair.setVehicleNo(vehicle.getVehicleNo());
		carRepair.setHub(driver.getHub());

		carRepairRepository.save(carRepair);
		driverRepository.save(driver);
		vehicleRepository.save(vehicle);

		return "sucessfully repair the vehicle";

	}

	@Override
	public void changePhoneNo(Long driverId, String newPhoneNo) {
		Driver driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new IllegalArgumentException("Driver not found"));

		// Check if the old phone number matches the current driver's phone number
		if (driver.getPhoneNo().equals(newPhoneNo)) {
			throw new IllegalArgumentException("Phone number is already in use");
		}

		// Update driver's phone number to newPhoneNo
		driver.setPhoneNo(newPhoneNo);
		driverRepository.save(driver);

		// Find existing OTP entries for this driver
		Optional<List<ManageOtp>> existingOtpListOptional = manageOtpRepository.findByDriver_DriverId(driverId)
				.map(Collections::singletonList);

		// Delete each existing OTP entry if it exists
		existingOtpListOptional.ifPresent(existingOtpList -> {
			existingOtpList.forEach(existingOtp -> {
				manageOtpRepository.delete(existingOtp);
			});
		});

		// Generate and send a verification code to the new phone number
		String smsotp = locationImpl.generateVerificationCode();
		locationImpl.sendVerificationCode(newPhoneNo, smsotp);

		// Save the new OTP and driverId in the database for verification
		ManageOtp manageOtp = new ManageOtp();
		manageOtp.setDriver(driver);
		manageOtp.setRegisterOtp(smsotp);
		manageOtpRepository.save(manageOtp);
	}

	@Override
	public List<CarRepairDto> findDriverListsWithChangeReasonsByHubId(Long hubId) {
		List<CarRepair> carRepairs = carRepairRepository.findByHub_HubId(hubId);

		if (carRepairs.isEmpty()) {
			throw new BusinessException("601", "No change reasons found for hub ID: " + hubId);
		}
		List<CarRepairDto> carRepairDtos = new ArrayList<>();

		for (CarRepair carRepair : carRepairs) {
			if (carRepair.getMessage() != null && !carRepair.getMessage().isEmpty()) {
				CarRepairDto repairDto = new CarRepairDto();
				repairDto.setDriverName(carRepair.getDriver().getName());
				repairDto.setDriverId(carRepair.getDriver().getDriverId());
				repairDto.setMessage(carRepair.getMessage());
				repairDto.setReturnTime(carRepair.getReturnTime());
				repairDto.setVehicleName(carRepair.getVehicleName());
				repairDto.setVehicleNo(carRepair.getVehicleNo());

				carRepairDtos.add(repairDto);
			}
		}

		return carRepairDtos;
	}

	@Override
	public List<CarRepairDto> findDriverListwithReturnConditionByHubId(Long hubId) {
		List<ReturnCar> CarChanges = returnCarRepository.findByHub_HubId(hubId);

		if (CarChanges.isEmpty()) {
			throw new BusinessException("601", "No return cars found for hub ID: " + hubId);
		}
		List<CarRepairDto> carReturnDtos = new ArrayList<>();
		for (ReturnCar carChange : CarChanges) {
			if (carChange.getCarCondition() != null) {
				CarRepairDto changedto = new CarRepairDto();
				changedto.setDriverName(carChange.getDriver().getName());
				changedto.setDriverId(carChange.getDriver().getDriverId());
				changedto.setReturnTime(carChange.getReturnTime());
				changedto.setVehicleName(carChange.getVehicleName());
				changedto.setVehicleNo(carChange.getVehicleNo());
				changedto.setMessage(carChange.getMessage());
				changedto.setCarCondition(carChange.getCarCondition());

				carReturnDtos.add(changedto);

			}

		}
		return carReturnDtos;
	}

	// ====================================================================

	@Override
	public List<Driver> assignDriversToHub(Long hubId, double latitude, double longitude, int radius) {
		// Find the hub
		Optional<Hub> hubOptional = hubRepository.findById(hubId);
		if (hubOptional.isPresent()) {
			Hub hub = hubOptional.get();

			// Find nearby drivers based on hub's location
			List<Driver> nearbyDrivers = findNearbyDrivers(latitude, longitude, radius);

			// Assign the hub to each nearby driver
			for (Driver driver : nearbyDrivers) {
				driver.setHub(hub);
				driverRepository.save(driver);
			}
			return nearbyDrivers;
		}
		return null; // Return null if the hub is not found
	}

	@Override
	public List<Driver> findNearbyDrivers(double latitude, double longitude, double radius) {
		List<DriverLocation> listOfDriver = driverLocationRepository.findAll();

		return driverRepository.findAll();
	}

	@Override
	public List<Driver> findDriversNearHub(Hub hub, double radius) {
		// Find nearby drivers based on hub's location
		HubLocation newHubLocation = hubLocationRepository.findByHub(hub);
		double hubLatitude = newHubLocation.getHubLatitude();
		double hubLongitude = newHubLocation.getHubLongitude();
		return findNearbyDrivers(hubLatitude, hubLongitude, radius);
	}

	@Override
	public List<HubAddressDTO> findAllByDriverId(Long driverId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CarMaintenanceDto> getAllCarChangesAndWorstReturns() {
		List<CarRepair> carRepairs = carRepairRepository.findAll();
		List<CarMaintenanceDto> result = new ArrayList<>();

		// Convert car changes to DTOs
		carRepairs.forEach(carRepair -> {
			CarMaintenanceDto dto = new CarMaintenanceDto();

			dto.setReason(carRepair.getMessage());
			dto.setReturnTime(carRepair.getReturnTime());
			dto.setVehicleName(carRepair.getVehicleName());
			dto.setVehicleNo(carRepair.getVehicleNo());

			result.add(dto);
		});

		// Fetch return cars with worst condition and add them to the result list
		List<ReturnCar> worstReturnCars = returnCarRepository.findByCarCondition(ReturnCar.CarCondition.WORST);
		worstReturnCars.forEach(returnCar -> {
			CarMaintenanceDto dto = new CarMaintenanceDto();

			dto.setReturnTime(returnCar.getReturnTime());
			dto.setVehicleName(returnCar.getVehicleName());
			dto.setVehicleNo(returnCar.getVehicleNo());

			dto.setCarCondition(returnCar.getCarCondition().toString());
			result.add(dto);
		});

		return result;
	}

	@Override
	public int getTotalCarChangesAndWorstReturns() {
		List<CarRepair> carRepairs = carRepairRepository.findAll();
		List<ReturnCar> worstReturnCars = returnCarRepository.findByCarCondition(ReturnCar.CarCondition.WORST);

		return carRepairs.size() + worstReturnCars.size();
	}

	@Override
	public List<CarRepairDto> getAllCarRepairs() {
		List<CarRepair> carRepairs = carRepairRepository.findAll();
		if (carRepairs.isEmpty()) {
			throw new BusinessException("601", "No car repairs found");
		}
		return carRepairs.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	private CarRepairDto convertToDto(CarRepair carRepair) {
		CarRepairDto dto = new CarRepairDto();
		dto.setDriverName(carRepair.getDriver().getName());
		dto.setDriverId(carRepair.getDriver().getDriverId());
		dto.setVehicleNo(carRepair.getVehicleNo());
		dto.setVehicleName(carRepair.getVehicleName());
		dto.setReturnTime(carRepair.getReturnTime());
		dto.setMessage(carRepair.getMessage());

		return dto;
	}

	@Override
	public boolean changePhoneNoDriver(Long driverId, String newPhoneNo) {
		Optional<Driver> optionalUser = driverRepository.findById(driverId);

		if (optionalUser.isPresent()) {
			Driver existingUser = optionalUser.get();

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
					driverRepository.save(existingUser); // Save the updated user entity
					// Optionally, you can log here to confirm if the user is being updated properly
					System.out.println("Phone number updated for user with ID: 955" + existingUser.getPhoneNo());

				} else {
					// Log an error if UserIdentity is not found for the user
					System.out.println("UserIdentity not found with email: {}" + existingUser.getEmail());
				}

				return true; // Phone number successfully updated
			} else {
				// Log an error if the new phone number is the same as the existing one
				System.out.println("New phone number is the same as the existing phone number: " + newPhoneNo);
				return false; // Phone number not updated
			}
		} else {
			// Log an error if the user is not found
			System.out.println("User not found with ID: {}" + driverId);
			return false; // Phone number not updated
		}
	}

	@Override
	public boolean changeEmailDriver(Long userId, String newEmail) {
		Optional<Driver> optionalDriver = driverRepository.findById(userId);

		if (optionalDriver.isPresent()) {
			Driver existingDriver = optionalDriver.get();

			if (!existingDriver.getPhoneNo().equals(newEmail)) {
				existingDriver.setEmail(newEmail);

				// Update the phone number in UserIdentity entity if it exists
				Optional<UserIdentity> optionalUserIdentity = userIdentityRepository
						.findByPhoneNo(existingDriver.getPhoneNo());

				if (optionalUserIdentity.isPresent()) {
					UserIdentity existingUserIdentity = optionalUserIdentity.get();
					existingUserIdentity.setEmail(newEmail);
					userIdentityRepository.save(existingUserIdentity); // Save the updated UserIdentity entity
					// Optionally, you can log here to confirm if the UserIdentity is being updated
					// properly
					System.out.println(
							"UserIdentity email updated for user with ID:  }" + existingUserIdentity.getEmail());
					driverRepository.save(existingDriver); // Save the updated user entity
					// Optionally, you can log here to confirm if the user is being updated properly
					System.out.println("email updated for user with ID: 955" + existingDriver.getEmail());

				} else {
					// Log an error if UserIdentity is not found for the user
					System.out.println("UserIdentity not found with phone number: {}" + existingDriver.getPhoneNo());
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

	@Override
	public List<Driver> getTwoWheelerDriverList() {
		return driverRepository.findAll().stream()
				.filter(driver -> driver.getDriverType() == DriverAndVehicleType.TWO_WHEELER)
				.collect(Collectors.toList());
	}

	@Override
	public List<Driver> getFourWheelerDriverList() {
		return driverRepository.findAll().stream()
				.filter(driver -> driver.getDriverType() == DriverAndVehicleType.FOUR_WHEELER)
				.collect(Collectors.toList());
	}

	@Override
	public List<PaymentHistoryDto> getDriverPaymentHistory1(Long driverId) {
		List<Booking> bookings = bookingRepository.findByDriver_DriverId(driverId);
		return bookings.stream()
				.map(booking -> new PaymentHistoryDto(booking.getDriver().getName(),
						booking.getTotalAmount().toString(), booking.getTimeDuration().getStartDateTime(), // Adjust
																											// this line
																											// as per
																											// your
																											// entity
																											// structure
						booking.getPickupLocation().getUserLongitude(), booking.getPickupLocation().getUserLatitude(),
						booking.getDropOffLocation().getUserLongitude(),
						booking.getDropOffLocation().getUserLatitude()))
				.collect(Collectors.toList());
	}

	@Override
	public Long getTotalBookingsByDriverId(Long driverId) {
        return bookingRepository.countByDriver_DriverId(driverId);
    }

}
