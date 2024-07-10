package com.rido.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.razorpay.RazorpayClient;
import com.rido.Exceptions.DriverNotFoundException;
import com.rido.Exceptions.ResourceNotFoundException;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.controller.UserController;
import com.rido.dto.AssignCarRequestDto;
import com.rido.dto.BookingDTO;
import com.rido.dto.BookingDataDto;
import com.rido.dto.CarRepairRequestDto;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.CourierBookingDto1;
import com.rido.dto.DriverNameAvailableDto;
import com.rido.dto.DriverReturnVehicleResponseDto;
import com.rido.dto.DriverRunningVehicleResponseDto;
import com.rido.dto.GetEmployeeListDto;
import com.rido.dto.HubDataDto;
import com.rido.dto.HubDriverPaymentDetailsDto;
import com.rido.dto.HubLocationDto;
import com.rido.dto.HubManagerDto;
import com.rido.dto.HubManagerPaymentHistoryDto;
import com.rido.dto.HubMangerProfileEditDto;
import com.rido.dto.LocationDto;
import com.rido.dto.PasswordChangeRequestDto;
import com.rido.dto.PaymentHistoryDto;
import com.rido.dto.ProfileDto;
import com.rido.dto.RentalBookingDto;
import com.rido.dto.TodayBookingDashboardDto;
import com.rido.dto.VehicleDataDto;
import com.rido.dto.VehicleNameAvailableDto;
import com.rido.entity.Booking;
import com.rido.entity.CarRepair;
import com.rido.entity.CarRepairDetailCost;
import com.rido.entity.Courier;
import com.rido.entity.CourierBooking;
import com.rido.entity.Driver;
import com.rido.entity.DriverDocument;
import com.rido.entity.Hub;
import com.rido.entity.HubEmployee;
import com.rido.entity.HubEmployeePayment;
import com.rido.entity.HubPayment;
import com.rido.entity.ManageOtp;
import com.rido.entity.PaymentActivity;
import com.rido.entity.RentalBooking;
import com.rido.entity.ReturnCar;
import com.rido.entity.ReturnCar.CarCondition;
import com.rido.entity.UserIdentity;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.CarRepairStatus;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.RideOrderStatus;
import com.rido.entity.enums.Status;
import com.rido.entity.enums.VehicleAssignStatus;
import com.rido.entity.enums.VehicleStatus;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.AssignCarRepository;
import com.rido.repository.BookingRepository;
import com.rido.repository.CarRepairDetailCostRepository;
import com.rido.repository.CarRepairRepository;
import com.rido.repository.CourierBookingRepository;
import com.rido.repository.CourierRepository;
import com.rido.repository.DriverDocumentRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubEmployeePaymentRepository;
import com.rido.repository.HubEmployeeRepository;
import com.rido.repository.HubPaymentRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.PaymentRepository;
import com.rido.repository.RentalBookingRepository;
import com.rido.repository.ReturnCarRepository;
import com.rido.repository.UserIdentityRepository;
import com.rido.repository.VehicleRepository;
import com.rido.service.HubService;
import com.rido.service.VehicleService;

import jakarta.persistence.EntityNotFoundException;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class HubServiceImpl implements HubService {

	@Autowired
	VehicleService vehicleService;
	
	@Autowired
	private SendGrid sendGrid;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private AssignCarRepository assignCarRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private CarRepairRepository carRepairRepository;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private ManageOtpRepository manageOtpRepository;

	@Autowired
	private HubEmployeeRepository hubEmployeeRepository;

	@Autowired
	private UserController userController;

	@Autowired
	private HubEmployeePaymentRepository hubEmployeePaymentRepository;

	@Autowired
	private DriverDocumentRepository driverDocumentRepository;

	@Autowired
	private RazorpayClient razorpayClient;

	@Autowired
	private HubPaymentRepository hubPaymentRepository;

	@Autowired
	private CarRepairDetailCostRepository carRepairDetailCostRepository;

	@Autowired
	private ReturnCarRepository returnCarRepository;

	@Autowired
	private UserIdentityRepository userIdentityRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CourierBookingRepository courierBookingRepository;

	@Autowired
	private RentalBookingRepository rentalBookingRepository;

	@Autowired
	private CourierRepository courierRepository;

	@Override
	public List<Booking> getAllBookings() {
		return bookingRepository.findAll();
	}

	@Override
	public List<Booking> getTodaysBookings() {
		// Get today's date
		LocalDate today = LocalDate.now();

		// Get bookings for today
		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime endOfDay = today.atStartOfDay().plusDays(1).minusSeconds(1);

		return bookingRepository.findByTimeDurationStartDateTimeBetween(startOfDay, endOfDay);
	}

	@Override
	public List<Driver> getAvailableDrivers() {

		return driverRepository.findByStatus(Status.AVAILABLE);
	}

	@Override
	public List<DriverNameAvailableDto> getAvailableDriversName() {

		return driverRepository.findByStatus(Status.AVAILABLE).stream()
				.map(driver -> new DriverNameAvailableDto(driver.getName(), driver.getDriverId()))
				.collect(Collectors.toList());
	}

	@Override
	public List<VehicleNameAvailableDto> getAvailableCars() {

		return vehicleRepository.findByVehicleStatus(VehicleStatus.AVAILABLE).stream()
				.map(vehicle -> new VehicleNameAvailableDto(vehicle.getVehicleId(), vehicle.getVehicleName()))
				.collect(Collectors.toList());
	}

//	@Override
//	public void assignCarToDriver(Long hubId, AssignCarRequestDto request) {
//		Long driverId = null;
//		Long vehicleId = null;
//
//		if (request.getDriverName() != null) {
//			Driver driver = driverRepository.findByName(request.getDriverName());
//			if (driver != null) {
//				driverId = driver.getDriverId();
//			}
//		}
//		if (request.getVehicleName() != null) {
//			Vehicle vehicle = vehicleRepository.findByVehicleName(request.getVehicleName());
//			if (vehicle != null) {
//				vehicleId = vehicle.getVehicleId();
//			}
//		}
//
//		if (driverId == null || vehicleId == null) {
//			throw new IllegalArgumentException("Driver name and vehicle ID are required");
//		}
//
//		Hub hub = hubRepository.findById(hubId).orElseThrow(() -> new IllegalArgumentException("Hub not found"));
//
//		Vehicle vehicle = vehicleRepository.findById(vehicleId)
//				.orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
//
//		Driver driver = driverRepository.findById(driverId)
//				.orElseThrow(() -> new IllegalArgumentException("Driver not found"));
//		driver.setVehicle(vehicle);
//		driver.setVehicleAssignStatus(VehicleAssignStatus.CHECKIN);
//
//		vehicle.setVehicleStatus(VehicleStatus.ENGAGED);
//
//		AssignCar assignCar = new AssignCar();
//		assignCar.setHubId(hub);
//		assignCar.setDriverId(driver);
//		assignCar.setVehicleId(vehicle);
//		assignCar.setOpeningTime(LocalDateTime.now());
//		assignCar.setLocality(request.getLocality());
//		assignCarRepository.save(assignCar);
//	}

	@Override
	public ReturnCar assignCarToDriver(Long hubId, AssignCarRequestDto request) {

		Hub hub = hubRepository.findById(hubId).orElseThrow(() -> new IllegalArgumentException("Hub not found"));

		Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
				.orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

		Driver driver = driverRepository.findById(request.getDriverId())
				.orElseThrow(() -> new IllegalArgumentException("Driver not found"));

		driver.setVehicle(vehicle);
		driver.setVehicleAssignStatus(VehicleAssignStatus.CHECKIN);
		driverRepository.save(driver);

		vehicle.setVehicleStatus(VehicleStatus.ENGAGED);
		vehicleRepository.save(vehicle);

		ReturnCar returnCar = new ReturnCar();
		returnCar.setAssignTime(LocalDateTime.now());
		returnCar.setDriver(driver);
		returnCar.setVehicle(vehicle);
		returnCar.setHub(hub);
		returnCar.setVehicleName(vehicle.getVehicleName());
		returnCar.setVehicleNo(vehicle.getVehicleNo());
		returnCarRepository.save(returnCar);

		return returnCar;
	}

	@Override
	public List<DriverRunningVehicleResponseDto> runningVehicleDetailsByHub(Long hubId) {

		List<Driver> driverList = driverRepository.findByHub_HubIdAndVehicleAssignStatus(hubId,
				VehicleAssignStatus.CHECKIN);

		List<DriverRunningVehicleResponseDto> responseDtoList = new ArrayList<>();

		for (Driver driver : driverList) {
			if (driver.getVehicle() != null) { // Check if the vehicle is not null
				DriverRunningVehicleResponseDto responseDto = new DriverRunningVehicleResponseDto();
				responseDto.setDriverName(driver.getName());
				responseDto.setVehicleName(driver.getVehicle().getVehicleName());
				responseDto.setVehicleNo(driver.getVehicle().getVehicleNo());
//				responseDto.setVehicleType(driver.getVehicle().getVehicleType());
				responseDto.setSeatingCapacity(driver.getVehicle().getSeatingCapacity());
				responseDtoList.add(responseDto);
			}
		}

		return responseDtoList;
	}

//	@Override
//	public List<DriverReturnVehicleResponseDto> returnVehiclesDetails() {
//
//		List<CarChange> carChangeList = carChangeRepository.findAll();
////		List<DriverReturnVehicleResponseDto> responseDtoList = new ArrayList<>();
////
////		for (CarChange cars : carChangeList) {
////			DriverReturnVehicleResponseDto responseDto = new DriverReturnVehicleResponseDto();
////			responseDto.setCarCondition(cars.getCarCondition());
////			responseDto.setChangeReason(cars.getChangeReason());
////			responseDto.setReturnTime(cars.getReturnTime());
////			responseDto.setMessage(cars.getMessage());
////			responseDto.setDriverName(cars.getDriver().getName());
//////            responseDto.setDriverId(cars.getDriver().getDriverId());
////			responseDto.setCarName(cars.getDriver().getVehicle().getVehicleName());
////			responseDto.setCarNumber(cars.getDriver().getVehicle().getVehicleNo());
////
////			// Set other properties accordingly
////			responseDtoList.add(responseDto);
////		}
//
//		return responseDtoList;
//	}

	// driver total earning
	public List<HubDriverPaymentDetailsDto> totalearninbydriver(Long driverId) {

		List<PaymentActivity> payment = paymentRepository.findByDriver_DriverId(driverId);
		return calculateEarningsByDay(payment);
	}

	public static List<HubDriverPaymentDetailsDto> calculateEarningsByDay(List<PaymentActivity> paymentHistoryList) {
		Map<LocalDate, Double> dailyEarningsMap = new HashMap<>();

		// Calculate total earnings for each day
		for (PaymentActivity payment : paymentHistoryList) {
			LocalDateTime localDateTime = payment.getLocalDatetime();
			LocalDate date = localDateTime.toLocalDate();
			double amount = Double.parseDouble(payment.getAmount());

			dailyEarningsMap.put(date, dailyEarningsMap.getOrDefault(date, 0.0) + amount);
		}

		// Convert the map to a list of DTOs
		List<HubDriverPaymentDetailsDto> result = new ArrayList<>();
		for (Map.Entry<LocalDate, Double> entry : dailyEarningsMap.entrySet()) {
			HubDriverPaymentDetailsDto dto = new HubDriverPaymentDetailsDto();
			dto.setLocalDate(entry.getKey().atStartOfDay().toLocalDate());
			dto.setTotalEarning(String.valueOf(entry.getValue()));
			result.add(dto);
		}

		return result;
	}

	public List<DriverRunningVehicleResponseDto> runningCerDetails() {

		return null;
	}

	public List<CarRepairRequestDto> carProblems(Long hubId) throws Exception {

		List<ReturnCar> carIssues = returnCarRepository.findByHub_HubId(hubId);
		return carIssues.stream().filter(carChange -> carChange.getCarCondition().equals(CarCondition.NORMAL)
				|| carChange.getCarCondition().equals(CarCondition.WORST)).map(carChange -> {
					CarRepairRequestDto carRepairRequestDto = new CarRepairRequestDto();
					carRepairRequestDto.setVehicleName(carChange.getVehicleName());
					carRepairRequestDto.setVehicleNo(carChange.getVehicleNo());
					carRepairRequestDto.setCarCondition(carChange.getCarCondition());
					carRepairRequestDto.setMessage(carChange.getMessage());
					carRepairRequestDto.setDriverid(carChange.getDriver().getDriverId());
					carRepairRequestDto.setDriverName(carChange.getDriver().getName());
					carRepairRequestDto.setRepairDateTime(carChange.getReturnTime());
					return carRepairRequestDto;
				}).collect(Collectors.toList());

	}

	@Override
//	public List<CarRepairRequestDto> carRepairRequest(Long hubId) throws Exception {
//		
//
//		Optional<Hub> hubid = hubRepository.findById(hubId);
//
//		if (hubid.isPresent()) {
//
//			List<Vehicle> vehicles = vehicleRepository.findAll();
//			List<CarChange> changecar = carChangeRepository.findAll();
//
//			// Create a list to store car repair requests
//			List<CarRepairRequestDto> carRepairRequests = new ArrayList<>();
//
//			// Iterate through vehicles
//			for (Vehicle vehicle : vehicles) {
//				// Filter car changes for the current vehicle
//				List<CarChange> relevantCarChanges = changecar.stream()
//						.filter(change -> change.getDriver().getVehicle().getVehicleId().equals(vehicle.getVehicleId()))
//						.filter(carChange -> carChange.getCarCondition().equals("normal")
//								|| carChange.getCarCondition().equals("worst"))
//						.collect(Collectors.toList());
//
//				// Create a car repair request for each relevant car change
////		            for (CarChange carChange : relevantCarChanges) {
////		                // Create CarRepairDto instance and add it to the list
////		                carRepairRequests.add(new CarRepairRequestDto(
////		                        vehicle.getVehicleName(),
////		                        vehicle.getVehicleNo(),
////		                        carChange.getReturnTime(),
////		                        carChange.getMessage(),
////		                        carChange.getCarCondition()
////		                ));
////		            }
//			}
//
//			return carRepairRequests;
//		} else {
//			throw new Exception("Hub not found with id: " + hubId);
//		}
//
//	}

	public Hub updateHubProfile(HubDataDto hubDataDto, String s3Url, String signatureImageUrl, String passbookImageUrl)
			throws Exception {

		Optional<Hub> optionhub = hubRepository.findById(hubDataDto.getHubMangerId());
		// Update admin data if provided in the DTO
		Hub hub = optionhub.get();
		if (hubDataDto.getName() != null && !hubDataDto.getName().isEmpty()) {
			hub.setManagerName(hubDataDto.getName());
		}
		if (hubDataDto.getEmail() != null && !hubDataDto.getEmail().isEmpty()) {
			hub.setEmail(hubDataDto.getEmail());
		}
		if (hubDataDto.getPhoneNumber() != null && !hubDataDto.getPhoneNumber().isEmpty()) {
			hub.setPhoneNo(hubDataDto.getPhoneNumber());
		}
		if (hubDataDto.getUidNo() != null && !hubDataDto.getUidNo().isEmpty()) {
			hub.setUidNo(hubDataDto.getUidNo());
		}

//	    PersonalAddress	address = personalAddressRepository.findByHub_HubId(hub.getHubId());
//	    
//	    if(hubDataDto.getAddress() != null && !hubDataDto.getAddress().isEmpty()) {
//	    	address.setPersonalAddress(hubDataDto.getAddress());
//	    }

		// Update profile image link if provided
		if (s3Url != null && !s3Url.isEmpty()) {
			hub.setProfileImgLink(s3Url);
		}

		if (signatureImageUrl != null && !signatureImageUrl.isEmpty()) {
			hub.setSignatuePic(signatureImageUrl);
		}

		if (passbookImageUrl != null && !passbookImageUrl.isEmpty()) {
			hub.setPassbookPic(passbookImageUrl);
		}

		return hubRepository.save(hub);

	}

	@Override
	public String setPasswordByEmail(String email, PasswordChangeRequestDto requestDto) {
		Hub hub = hubRepository.findByEmail(email);

		if (hub != null) {
			// Check if newPassword and ConfirmnewPassword match
			if (requestDto.getNewPassword().equals(requestDto.getConfirmnewPassword())) {
				// Update the password

				Long hubId = hub.getHubId();
				// Generate and save OTP
				String otp = userServiceImpl.generateRandomOtp();
				ManageOtp manageOtp = manageOtpRepository.findByHub_HubId(hubId).get();
				manageOtp.setForgetOtp(otp);
				manageOtpRepository.save(manageOtp);

				// Save the updated hub
				hubRepository.save(hub);

				// Send OTP by email
				userServiceImpl.sendOtpByEmail(email, otp);

				hub.setPassword(requestDto.getNewPassword());
				return "Password updated successfully. OTP sent to your email.";
			} else {
				return "New password and confirm new password do not match.";
			}
		} else {
			return "Hub not found with email: " + email;
		}

	}

	@Override
	public boolean verifyEmailOtp(Long HubId, String otp) {

		Optional<ManageOtp> manageOtp = manageOtpRepository.findByHub_HubId(HubId);

		System.out.println(manageOtp + "byemail 106");
		if (manageOtp != null) {
			ManageOtp manageOtp2 = manageOtp.get();
			String emailOtp = manageOtp2.getForgetOtp();
			if (emailOtp.equals(otp)) {

				return true;
			}

		}

		return false;

	}

	// get list

//	@Override
//	public List<HubManagerDto> getActiveHubs() {
//		List<Hub> activeHubs = hubRepository.findByStatus(Status.AVAILABLE);
//
//		List<HubManagerDto> activeHubDtos = new ArrayList<>();
//		for (Hub hub : activeHubs) {
//			HubManagerDto hubInfDto = new HubManagerDto();
//
//			hubInfDto.setManagerName(hub.getManagerName());
//
////			hubInfDto.setStauts(hub.getStatus());
//
//			hubInfDto.setHubName(hub.getHubName());
//
//
//			activeHubDtos.add(hubInfDto);
//		}
//
//		return activeHubDtos;
//	}

	// get by id
	public HubManagerDto getHubDetails(Long hubId) {
		Optional<Hub> hubOptional = hubRepository.findById(hubId);

		if (hubOptional.isPresent()) {
			Hub hub = hubOptional.get();
			HubManagerDto hubDetails = new HubManagerDto();

			hubDetails.setHubId(hub.getHubId());
			hubDetails.setManagerName(hub.getManagerName());
			hubDetails.setHubName(hub.getHubName());
			hubDetails.setEmail(hub.getEmail());
			hubDetails.setPhoneNo(hub.getPhoneNo());
			hubDetails.setProfileImgLink(hub.getProfileImgLink());
			hubDetails.setCity(hub.getCity());
			hubDetails.setState(hub.getState());
			return hubDetails;
		} else {
			return null; // Hub not found
		}
	}

	@Override
	public List<Hub> getHubList() {

		return hubRepository.findAll();

	}

	@Override
	public List<GetEmployeeListDto> getHubEmployeeList(Long hubId) {
		// Retrieve hub employees by hubId
		List<HubEmployee> listOfHubEmployee = hubEmployeeRepository.findByHub_HubId(hubId);

		List<GetEmployeeListDto> employeeListDto = new ArrayList<>();

		// Convert HubEmployee objects to GetEmployeeListDto objects
		for (HubEmployee hubEmployee : listOfHubEmployee) {
			GetEmployeeListDto employeeDto = new GetEmployeeListDto();
			employeeDto.setName(hubEmployee.getName());
			employeeDto.setHubEmpId(hubEmployee.getHubEmployeeId());
			employeeDto.setPhoneNo(hubEmployee.getPhoneNo());
			employeeDto.setEmail(hubEmployee.getEmail());
			employeeListDto.add(employeeDto);
		}

		return employeeListDto;
	}

	@Override
	public String paySalaryToHubEmployee(String amount, Long employeeOrderId) {

//		HubEmployee existingEubEmployee = hubEmployeeRepository.findById(hubEmployeeId).get();
		HubEmployeePayment existingHubEmployeePayment = hubEmployeePaymentRepository.findById(employeeOrderId).get();

		if (existingHubEmployeePayment != null) {
//			String amountinPaise = this.convertRupeeToPaise(existingHubEmployeePayment.getAmount());

			return null;

		} else {
			return "There is no detail availbale with this id" + employeeOrderId;
		}

	}

	@Override
	public List<Vehicle> getListOfHubsVehicle(Long hubId) {

		List<Vehicle> listOfHubsVehicles = vehicleRepository.findByHub(hubRepository.findById(hubId).get());

		return listOfHubsVehicles;
	}

	@Override
	public Vehicle getVehicleOfHubByVehicleId(Long hubId, Long vehicleId) {
		Optional<Vehicle> existingVehicleOptional = vehicleRepository.findByHub(hubRepository.findById(hubId).get())
				.stream().filter(vehicle -> vehicle.getVehicleId().equals(vehicleId)).findFirst();

		if (existingVehicleOptional.isPresent()) {
			return existingVehicleOptional.get();
		} else {
			return null;
		}
	}

	@Override
	public Driver approveDriver(Long driverDocumentId) {

		DriverDocument existingDriverDocument = driverDocumentRepository.findById(driverDocumentId).orElseThrow(
				() -> new DriverNotFoundException("Driver document not found with ID: " + driverDocumentId));

		existingDriverDocument.setApproved(true);
		existingDriverDocument.setRejected(false);

		driverDocumentRepository.save(existingDriverDocument);

		Driver existingDriver = existingDriverDocument.getDriver();
		if (existingDriver == null) {
			throw new DriverNotFoundException("Driver not found for document ID: " + driverDocumentId);
		}

		Driver updatedDriver = driverRepository.save(existingDriver);

		return updatedDriver;
	}

	// manager payemnt list
	@Override
	public List<HubManagerPaymentHistoryDto> getPaymentHistoryByHubId(Long hubId) {
		// Assuming HubPaymentRepository has a method like findByHubIdOrderByDateAsc
		List<HubPayment> hubPayments = hubPaymentRepository.findByHub_HubIdOrderByDateAsc(hubId);
		// Convert HubPayment entities to DTOs
		return hubPayments.stream()
				.map(payment -> new HubManagerPaymentHistoryDto(payment.getAmount(), payment.getDate()))
				.collect(Collectors.toList());
	}

	// all hub list
	@Override
	public List<HubManagerDto> getAllHubs() {
		List<Hub> allHubs = hubRepository.findAll();

		List<HubManagerDto> allHubDtos = new ArrayList<>();
		for (Hub hub : allHubs) {
			HubManagerDto hubInfDto = new HubManagerDto();

			hubInfDto.setHubId(hub.getHubId());

			hubInfDto.setManagerName(hub.getManagerName());
			hubInfDto.setHubName(hub.getHubName());
			;
//			hubInfDto.setStauts(hub.getStatus());

			hubInfDto.setCity(hub.getCity());

			allHubDtos.add(hubInfDto);
		}

		return allHubDtos;
	}

	@Override
	public HubMangerProfileEditDto getHubMangerProfile(Long hubId) {

		Optional<Hub> hubManger = hubRepository.findById(hubId);
		if (hubManger.isPresent()) {
			HubMangerProfileEditDto hubMangerdto = new HubMangerProfileEditDto();
			Hub hubentity = hubManger.get();
			hubMangerdto.setHubMangerId(hubManger.get().getHubId());
			hubMangerdto.setFullName(hubentity.getManagerName());
			hubMangerdto.setUidNo(hubentity.getUidNo());
			hubMangerdto.setPhoneNumber(hubentity.getPhoneNo());
			hubMangerdto.setEmail(hubentity.getEmail());
			hubMangerdto.setAddress(hubentity.getHubName());
			hubMangerdto.setProfilePic(hubentity.getProfileImgLink());
			hubMangerdto.setSignatuePic(hubentity.getSignatuePic());
			hubMangerdto.setPassbookPic(hubentity.getPassbookPic());
			return hubMangerdto;
		} else {
			throw new EntityNotFoundException("Hub not found with id: " + hubId);
		}

	}

	@Override
	public boolean changePasswordByOldPassword(Long HubEmpId, ChangePasswordRequestDto changePasswordRequestDto) {
		// Retrieve user from the database
		HubEmployee HubEmp = hubEmployeeRepository.findById(HubEmpId).orElse(null);

		if (HubEmp != null
				&& passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), HubEmp.getPassword())) {
			Optional<UserIdentity> userIdentityOptional = userIdentityRepository.findByPhoneNo(HubEmp.getPhoneNo());

			if (userIdentityOptional.isPresent()) {
				UserIdentity userIdentity = userIdentityOptional.get();
				if (HubEmp.getPhoneNo().equals(userIdentity.getPhoneNo())) {
					if (changePasswordRequestDto.getNewPassword()
							.equals(changePasswordRequestDto.getConfirmPassword())) {
						String encodedPassword = passwordEncoder.encode(changePasswordRequestDto.getNewPassword());

						HubEmp.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						hubEmployeeRepository.save(HubEmp);
						userIdentityRepository.save(userIdentity);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<HubLocationDto> getHubLocation() {
		return hubRepository.findAll().stream().map(hub -> new HubLocationDto(hub.getHubName()))
				.collect(Collectors.toList());
	}

	// get profile by email
	public ProfileDto getProfileByEmail(String email) {
		Optional<Hub> optionalHub = Optional.ofNullable(hubRepository.findByEmail(email));

		if (optionalHub.isPresent()) {
			Hub hub = optionalHub.get();
			ProfileDto profileDto = new ProfileDto();
			profileDto.setName(hub.getManagerName());
			profileDto.setProfileImgLink(hub.getProfileImgLink());
			profileDto.setEmail(hub.getEmail());
			profileDto.setId(hub.getHubId());
			profileDto.setLocation(hub.getHubName());
			profileDto.setPhoneNo(hub.getPhoneNo());

			return profileDto;
		}
		return null;

	}

//	@Override
//	public String carRepairDetailCostSend(Long carRepairId, String invoiceImg, BigDecimal totalCostOfRepairing) {
//		CarRepair carRepair = carRepairRepository.findById(carRepairId)
//				.orElseThrow(() -> new IllegalArgumentException("CarChange with id " + carRepairId + " not found"));
//
//		CarRepairDetailCost carRepairDetailCost = new CarRepairDetailCost();
//		carRepairDetailCost.setInvoice(invoiceImg);
//		carRepairDetailCost.setDateOfCarRepaired(LocalDateTime.now());
//		carRepairDetailCost.setTotalCostOfRepairing(totalCostOfRepairing);
//		carRepairDetailCost.setVehicleName(carRepair.getVehicleName());
//		carRepairDetailCost.setVehicleNo(carRepair.getVehicleNo());
//		carRepairDetailCost.setDateOfRepairing(carRepair.getReturnTime());
//		carRepairDetailCost.setIssueDetail(carRepair.getMessage());
//		carRepairDetailCost.setHub(carRepair.getHub());
//
//		carRepairDetailCostRepository.save(carRepairDetailCost);
//
//		Vehicle vehicle = carRepair.getDriver().getVehicle();
//		if (vehicle != null) {
//			vehicle.setVehicleStatus(VehicleStatus.AVAILABLE);
//			vehicleRepository.save(vehicle);
//		}
//
//		return "Repair Car Detail Send Successfully";
//	}
	
	
	
	@Override
	public String carRepairDetailCostSend(Long carRepairId, String invoiceImg, BigDecimal totalCostOfRepairing) {
	    CarRepair carRepair = carRepairRepository.findById(carRepairId)
	            .orElseThrow(() -> new IllegalArgumentException("CarRepair with id " + carRepairId + " not found"));

	    CarRepairDetailCost carRepairDetailCost = new CarRepairDetailCost();
	    carRepairDetailCost.setInvoice(invoiceImg);
	    carRepairDetailCost.setDateOfCarRepaired(LocalDateTime.now());
	    carRepairDetailCost.setTotalCostOfRepairing(totalCostOfRepairing);
	    carRepairDetailCost.setVehicleName(carRepair.getVehicleName());
	    carRepairDetailCost.setVehicleNo(carRepair.getVehicleNo());
	    carRepairDetailCost.setDateOfRepairing(carRepair.getReturnTime());
	    carRepairDetailCost.setIssueDetail(carRepair.getMessage());
	    carRepairDetailCost.setHub(carRepair.getHub());

	    carRepair.setCarRepairStatus(CarRepairStatus.READY_FOR_RIDE);
	    carRepairDetailCostRepository.save(carRepairDetailCost);

	    Vehicle vehicle = carRepair.getDriver().getVehicle();
	    if (vehicle != null) {
	        vehicle.setVehicleStatus(VehicleStatus.AVAILABLE);
	        vehicleRepository.save(vehicle);
	    }

	    // Retrieve the admin email from the hub
	    String adminEmail = carRepair.getHub().getAdmin().getEmail();

	    // Send email with SendGrid
	    sendInvoiceEmail(adminEmail, invoiceImg, totalCostOfRepairing, carRepair);

	    return "Repair Car Detail Sent Successfully";
	}

	// Helper method to send the invoice email using SendGrid
		
	private void sendInvoiceEmail(String adminEmail, String invoiceImg, BigDecimal totalCostOfRepairing, CarRepair carRepair) {
	    Email from = new Email("jaleshwarimasram@gmail.com"); // Replace with your sender email
	    Email to = new Email(adminEmail);
	    String subject = "Car Repair Invoice Details";
	    String emailContent = composeEmailContent(invoiceImg, totalCostOfRepairing, carRepair);
	    Content content = new Content("text/plain", emailContent);
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
	        throw new RuntimeException("Error sending invoice email", ex);
	    }
	}

	// Helper method to compose the email content
	private String composeEmailContent(String invoiceImg, BigDecimal totalCostOfRepairing, CarRepair carRepair) {
	    StringBuilder content = new StringBuilder();
	    content.append("Dear Admin,\n\n");
	    content.append("Please find below the details of the car repair invoice:\n\n");
	    content.append("Vehicle Name: ").append(carRepair.getVehicleName()).append("\n");
	    content.append("Vehicle Number: ").append(carRepair.getVehicleNo()).append("\n");
	    content.append("Issue Detail: ").append(carRepair.getMessage()).append("\n");
	    content.append("Total Cost of Repairing: ").append(totalCostOfRepairing).append("\n");
	    content.append("Invoice Image: ").append(invoiceImg).append("\n\n");
	    content.append("Thank you,\n");
	    content.append("Your Company Name");
	    return content.toString();
	}

	
	
	
	

	@Override
	public CarRepair carRepairApproval(Long carRepairId, List<String> damageCarImgs, String damageCarVideo, String hubMessage) {
	    CarRepair carRepair = carRepairRepository.findById(carRepairId)
	            .orElseThrow(() -> new IllegalArgumentException("CarRepair with id " + carRepairId + " not found"));

	    
	    // Join the list of image URLs into a single string separated by commas
	    String damageCarImgsStr = String.join(",", damageCarImgs);

	    // Set the images, video, and message if provided
	    if (!damageCarImgs.isEmpty()) {
	        carRepair.setDamageCarImg(damageCarImgsStr);
	    }

	    if (damageCarVideo != null && !damageCarVideo.isEmpty()) {
	        carRepair.setDamageCarVideo(damageCarVideo);
	    }

	    if (hubMessage != null && !hubMessage.isEmpty()) {
	        carRepair.setHubMessage(hubMessage);
	    }
	    
	    // Set the car repair status to PENDING
	    carRepair.setCarRepairStatus(CarRepairStatus.PENDING);

	    carRepairRepository.save(carRepair);
	    return carRepair;
	}

	@Override
	public List<CarRepairDetailCost> getAllCarRepairDetailCosts() {
		return carRepairDetailCostRepository.findAll();
	}

	@Override
	public BigDecimal getTotalCostOfRepairingForCurrentMonth() {
		return carRepairDetailCostRepository.getTotalCostOfRepairingForCurrentMonth();
	}

	@Override
	public BigDecimal getTotalCostOfRepairingForCurrentMonthByHub(Long hubId) {
		return carRepairDetailCostRepository.getTotalCostOfRepairingForCurrentMonthByHub(hubId);
	}

	@Override
	public List<Hub> findNearbyHubs(double latitude, double longitude, double radius) {
		// Implement logic to find nearby hubs based on latitude, longitude, and radius
		// This is just a placeholder implementation
		// You would typically query the database to find hubs within the specified
		// radius
		return hubRepository.findAll(); // Example: returning all hubs for demonstration
	}

	@Override
	public Hub getHubById(Long hubId) {
		Optional<Hub> hubOptional = hubRepository.findById(hubId);
		return hubOptional.orElse(null);
	}

	public List<TodayBookingDashboardDto> getTodayBookingDashboard(Long hubId) {
		List<Booking> bookings = bookingRepository.findByHub_HubId(hubId);

		List<TodayBookingDashboardDto> dtos = new ArrayList<>();

		for (Booking booking : bookings) {
			TodayBookingDashboardDto dto = new TodayBookingDashboardDto();
			dto.setTime(booking.getTimeDuration());

			// user pickup location
			LocationDto pickupLocationDto = new LocationDto();
			pickupLocationDto.setLatitude(booking.getPickupLocation().getUserLatitude());
			pickupLocationDto.setLongitude(booking.getPickupLocation().getUserLongitude());
			dto.setPickupLocation(pickupLocationDto);

			// user drop-off location
			LocationDto dropOffLocationDto = new LocationDto();
			dropOffLocationDto.setLatitude(booking.getDropOffLocation().getUserLatitude());
			dropOffLocationDto.setLongitude(booking.getDropOffLocation().getUserLatitude());
			dto.setDropOffLocation(dropOffLocationDto);

			dtos.add(dto);
		}

		return dtos;
	}

	@Override
	public ResponseLogin getByPhoneno(String phoneno) {

		Hub hub = hubRepository.findByPhoneNo(phoneno);
		if (hub != null) {
			ResponseLogin response = new ResponseLogin();
			response.setUserId(hub.getHubId());
			response.setEmail(hub.getEmail());
			response.setPhoneNo(hub.getPhoneNo());
			response.setName(hub.getManagerName());
			response.setLocation(hub.getHubName());
			return response;
		} else {
			return null;
		}
	}

	@Override
	public List<CarRepairRequestDto> carRepairRequest(Long hubId) throws Exception {

		// TODO Auto-generated method stub
		List<ReturnCar> returnCars = returnCarRepository.findAll();
		return returnCars.stream()
				.filter(returnCar -> returnCar.getCarCondition() == ReturnCar.CarCondition.NORMAL
						|| returnCar.getCarCondition() == ReturnCar.CarCondition.WORST)
				.map(this::mapToCarRepairRequestDto).collect(Collectors.toList());

	}

	private CarRepairRequestDto mapToCarRepairRequestDto(ReturnCar returnCar) {
		CarRepairRequestDto dto = new CarRepairRequestDto();
		dto.setDriverid(returnCar.getDriver().getDriverId());
		dto.setDriverName(returnCar.getDriver().getName());
		dto.setVehicleName(returnCar.getVehicleName());
		dto.setVehicleNo(returnCar.getVehicleNo());
		// Assuming repairDateTime is the same as returnTime, adjust accordingly if it's
		// different
		dto.setRepairDateTime(returnCar.getReturnTime());
		dto.setMessage(returnCar.getMessage());
		dto.setCarCondition(returnCar.getCarCondition());
		return dto;
	}

	@Override
	public List<DriverReturnVehicleResponseDto> returnVehiclesDetails() {

		return null;
	}

	public long getTotalNumberOfVehicles() {
		return vehicleRepository.count();
	}

	@Override
	public List<PaymentHistoryDto> getEmployeePayementHistoryByHubId(Long hubId) {

		List<HubEmployeePayment> listOfHubEmployee = hubEmployeePaymentRepository.findByHub_HubId(hubId);
		List<PaymentHistoryDto> paymentHistoryDtoList = new ArrayList<>();

		// Convert HubEmployeePayment objects to PaymentHistoryDto objects
		for (HubEmployeePayment payment : listOfHubEmployee) {
			PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();
			paymentHistoryDto.setAmount(payment.getAmount());
			paymentHistoryDto.setLocaldatetime(payment.getLocalDatetime());
			paymentHistoryDtoList.add(paymentHistoryDto);
		}

		return paymentHistoryDtoList;
	}

	@Override
	public List<BookingDTO> getCourierListDetails(Long hubId) throws UserNotFoundException {
		Optional<Hub> checkhub = hubRepository.findById(hubId);

		if (checkhub.isPresent()) {
			List<CourierBooking> courierBookingList = courierBookingRepository.findAll();

			List<BookingDTO> dtos = new ArrayList<>();
			for (CourierBooking courier : courierBookingList) {
				dtos.add(mapCourierBookingToDto(courier));
			}
			return dtos;
		}

		throw new UserNotFoundException("hub not found with this id " + hubId); // Or throw an exception if hubId is not
																				// found
	}

	private BookingDTO mapCourierBookingToDto(CourierBooking courier) {
		BookingDTO bookingDTO = new BookingDTO();
		bookingDTO.setTime(courier.getTimeDuration().getStartDateTime()); // Assuming getTime() returns a string for
																			// time
		bookingDTO.setDriverName(courier.getCourierDriver().getOwnerName()); // Assuming getDriverName() returns a
																				// string for driver name

		// Create BookingData object and populate its fields
		BookingDataDto bookingData = new BookingDataDto();
		bookingData.setUserName(courier.getUser().getName());
		bookingData.setPickuplatituelocation(courier.getSenderReceiverInfo().getSenderLatitude());
		bookingData.setPickuplonglocation(courier.getSenderReceiverInfo().getSenderLongitude());
		bookingData.setDropuplatitueLocation(courier.getSenderReceiverInfo().getReceiverLatitude());
		bookingData.setDropuplongLocation(courier.getSenderReceiverInfo().getReceiverLongitude());
		bookingData.setPhoneNo(courier.getUser().getPhoneNo());
		// bookingData.setDistance(courier.getDistance()); // Assuming getDistance()
		// returns a string for distance

		// Add BookingData object to BookingDTO's list of BookingData
		List<BookingDataDto> bookingDataList = new ArrayList<>();
		bookingDataList.add(bookingData);
		bookingDTO.setBookingDataList(bookingDataList);

		bookingDTO.setBookingDate(courier.getTimeDuration().getStartDateTime()); // Assuming getBookingDate() returns a
																					// string for booking date
		bookingDTO.setStatus(courier.getRideOrderStatus()); // Assuming getStatus() returns a string for status

		return bookingDTO;
	}

	@Override
	public List<VehicleDataDto> getTwoWheelarlist(Long hubId) {
		List<Vehicle> vehicles = vehicleRepository.findByHub_HubIdAndVehicleType(hubId,
				DriverAndVehicleType.TWO_WHEELER);

		return vehicles.stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Override
	public List<VehicleDataDto> getFourWheelarlist(Long hubId) {

		List<Vehicle> vehicles = vehicleRepository.findByHub_HubIdAndVehicleType(hubId,
				DriverAndVehicleType.FOUR_WHEELER);

		return vehicles.stream().map(this::mapToDto).collect(Collectors.toList());
	}

	// Helper method to map Vehicle entity to VehicleDataDto
	private VehicleDataDto mapToDto(Vehicle vehicle) {
		VehicleDataDto dto = new VehicleDataDto();
		dto.setVehicleName(vehicle.getVehicleName());
		dto.setPrice(vehicle.getPrice());
		// dto.setBattery(vehicle.getBattery());
		// dto.setChargingTime(vehicle.getChargingTime());
		dto.setSeatingCapacity(vehicle.getSeatingCapacity());
		// dto.setTransmissionTypo(vehicle.getTransmissionTypo());
		dto.setVehicleNo(vehicle.getVehicleNo());
		dto.setInsuranceNo(vehicle.getInsuranceNo());
		dto.setPricePerKm(vehicle.getPricePerKm());
		dto.setVehicleServiceType(vehicle.getVehicleServiceType());
		dto.setVehicleType(vehicle.getVehicleType());
		return dto;
	}

	@Override
	public Object getScheduleBookingDetails() {

		List<Booking> bookingList = bookingRepository.findAll();
		if (bookingList.isEmpty()) {
			throw new ResourceNotFoundException("There is no schedule booking details is available", "901");
		}
		return bookingList;

	}

	@Override
	public Object getRentalBookingDetails() {

		List<RentalBooking> rentalBookingList = rentalBookingRepository.findAll();
		if (rentalBookingList.isEmpty()) {
			throw new ResourceNotFoundException("There is no rental booking details is available", "901");
		}
		return rentalBookingList;

	}

	@Override
	public Object getCourierBookingDetails() {

		List<CourierBooking> courierBookingList = courierBookingRepository.findAll();
		if (courierBookingList.isEmpty()) {
			throw new ResourceNotFoundException("There is no rental booking details is available", "901");
		}
		return courierBookingList;

	}

	@Override
	public Object getRentalBookingDetailsOfHub(Long hubId) {
		List<RentalBooking> listOfBookingDetails = rentalBookingRepository.findByHub_HubId(hubId);

		List<RentalBooking> completedOfBookingDetails = listOfBookingDetails.stream()
				.filter(booking -> booking.getRideOrderStatus() == RideOrderStatus.COMPLETE)
				.collect(Collectors.toList());

		List<RentalBookingDto> rentalBookingDtos = new ArrayList<>();
		for (RentalBooking rentalBooking : completedOfBookingDetails) {

			RentalBookingDto rentalBookingDto = convertRentalBookingToDto(rentalBooking);
			rentalBookingDtos.add(rentalBookingDto);
		}

		if (rentalBookingDtos.isEmpty()) {
			throw new ResourceNotFoundException("There is no rental booking details is available", "901");
		}
		return rentalBookingDtos;

	}

	private RentalBookingDto convertRentalBookingToDto(RentalBooking rentalBooking) {

		RentalBookingDto rentalBookingDto = new RentalBookingDto();
		rentalBookingDto.setRentalBookingId(rentalBooking.getRentalBookingId());

		rentalBookingDto.setRideOrderStatus(rentalBooking.getRideOrderStatus());
		rentalBookingDto.setTimeDuration(rentalBooking.getTimeDuration());
		return rentalBookingDto;

	}

	@Override
	public Object getCourierBookingDetailsOfHub(Long hubId) {
		List<Courier> courierDriverList = courierRepository.findByHub_HubId(hubId);
		List<CourierBooking> allCourierBooking = courierBookingRepository.findAll();

		List<CourierBooking> allRelatedBookings = courierDriverList.stream()
				.flatMap(courier -> allCourierBooking.stream()
						.filter(booking -> booking.getCourierDriver().getCourierId() == courier.getCourierId()))
				.collect(Collectors.toList());

		if (allRelatedBookings.isEmpty()) {
			throw new ResourceNotFoundException("There is no courier booking details is available", "901");
		}
		return allRelatedBookings;

	}

	@Override
	public List<CourierBookingDto1> getAllCourierBookings() {
		List<CourierBooking> courierBookings = courierBookingRepository.findAll();
		return courierBookings.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	private CourierBookingDto1 convertToDto(CourierBooking courierBooking) {
		return new CourierBookingDto1(courierBooking.getRideOrderStatus(),
				courierBooking.getTimeDuration().getStartDateTime(), courierBooking.getTimeDuration().getEndDateTime(),
				courierBooking.getTotalAmount());
	}

}
