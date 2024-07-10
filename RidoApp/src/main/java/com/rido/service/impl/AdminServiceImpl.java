package com.rido.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rido.config.TwilioConfig;
import com.rido.dto.AdminDataDto;
import com.rido.dto.AdminProfileEditDto;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.CourierEbikeDto;
import com.rido.dto.DriverApproveRequestDto;
import com.rido.dto.DriveracceptpaymentDto;
import com.rido.dto.ListOfAssignVehiclesDto;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.PaymentHistoryDto;
import com.rido.dto.ProfileDto;
import com.rido.dto.VehicleCourierEbikeDto;
import com.rido.dto.VehicleDataDto;
import com.rido.entity.Admin;
import com.rido.entity.Booking;
import com.rido.entity.CarRepair;
import com.rido.entity.CourierEbike;
import com.rido.entity.Driver;
import com.rido.entity.DriverDocument;
import com.rido.entity.DriverPaymentDetail;
import com.rido.entity.Hub;
import com.rido.entity.ManageOtp;
import com.rido.entity.PaymentActivity;
import com.rido.entity.User;
import com.rido.entity.UserIdentity;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.CarRepairStatus;
import com.rido.entity.enums.MaintenanceApprovalStatus;
import com.rido.entity.enums.VehicleStatus;
import com.rido.entityDTO.DriverPaymentDto;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.AdminRepository;
import com.rido.repository.BookingRepository;
import com.rido.repository.CarRepairDetailCostRepository;
import com.rido.repository.CarRepairRepository;
import com.rido.repository.CourierEbikeRepository;
import com.rido.repository.DriverDocumentRepository;
import com.rido.repository.DriverPaymentDetailRepository;
import com.rido.repository.DriverPaymentRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubEmployeePaymentRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.PaymentRepository;
import com.rido.repository.UserIdentityRepository;
import com.rido.repository.UserRepository;
import com.rido.repository.VehicleRepository;
import com.rido.service.AdminService;
import com.rido.service.BookingService;
import com.rido.service.HubPaymentService;
import com.rido.service.HubService;
import com.rido.utils.GenerateFile;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import jakarta.persistence.EntityManager;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private DriverDocumentRepository driverDocumentRepository;

	@Autowired
	private LocationImpl locationImpl;

	@Autowired
	private TwilioConfig twilioConfig;

	@Autowired
	private ManageOtpRepository manageOtpRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private DriverServiceImpl driverServiceImpl;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private HubPaymentService hubPaymentService;

	@Autowired
	private HubService hubService;

	@Autowired
	private BookingService bookingService;

	private EntityManager entityManager;

	@Autowired
	private HubEmployeePaymentRepository hubEmployeePaymentRepository;

	@Autowired
	private DriverPaymentRepository driverPaymentRepository;

	@Autowired
	private CarRepairDetailCostRepository carRepairDetailCostRepository;

	@Autowired
	private CarRepairRepository carRepairRepository;

	@Autowired
	private DriverPaymentDetailRepository driverPaymentDetailRepo;
	@Autowired
	private CourierEbikeRepository courierEbikeRepository;

	@Autowired
	private UserIdentityRepository userIdentityRepository;
	@Autowired
	private CourierEbikeRepository courierEbikeRepo;

	@Override
	public Optional<Driver> findDriverById(Long driverId) {

		Optional<Driver> findById = driverRepository.findById(driverId);
		return findById;
	}

	@Override
	public String deleteDriverById(Long driverId) {

		Optional<Driver> findById = driverRepository.findById(driverId);

		if (findById.isPresent()) {
			driverRepository.deleteById(driverId);

			return "Driver with Id " + driverId + "deleted successfull";
		}
		return "Driver with Id " + driverId + " is not found";
	}

	@Override
	public List<Driver> findAllDrivers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Driver> findAvailableDriver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Driver> findBookedDriver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<User> findUserById(Long userId) {

		Optional<User> findById = userRepository.findById(userId);
		if (findById.isPresent()) {

			return findById;
//            	   User user = findById.get();  	   
//            	   user.getUserId();
//            	   user.getFirstName();
//            	   user.getEmail();
//            	   user.getGender();

		}
		return Optional.empty();
	}

	@Override
	public List<User> findAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vehicle findById(Long id) {
		return vehicleRepository.findById(id).orElse(null);
	}

	@Override
	public List<Vehicle> getList() {
		return vehicleRepository.findAll();
	}

	@Override
	public List<DriverApproveRequestDto> findByUnApproved() {

		List<DriverDocument> listOfDriverDocument = driverDocumentRepository.findAll();

		List<DriverDocument> listOfUnApprovedDriverDocument = listOfDriverDocument.stream()
				.filter(driverDocument -> !driverDocument.isApproved()).collect(Collectors.toList());

		List<DriverApproveRequestDto> driverUnApproveRequestDtoList = new ArrayList<>();
		int pendingDriversNumber = 0;
		for (DriverDocument driverDocument : listOfUnApprovedDriverDocument) {

			Long driverDocumentId = driverDocument.getDriverDocumentid();
			driverUnApproveRequestDtoList.add(getUnapproverDriverDocumetns(driverDocumentId));

			pendingDriversNumber++;

		}

//		getNoOfRequestedDriver(pendingDriversNumber);

		return driverUnApproveRequestDtoList;
	}

	public void sendPasswordForDriverLogin(String contactNo, String verificationCode) {
		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());

		// Send the verification code via SMS using Twilio
		Message message = Message
				.creator(new com.twilio.type.PhoneNumber(contactNo),
						new com.twilio.type.PhoneNumber(twilioConfig.getTrailNumber()), // Use your Twilio phone number
																						// here"+16562230668"
						"Your Password is sent successfully to your Register Number and Password: " + verificationCode)
				.create();

	}

	@Override
	public DriverApproveRequestDto getUnapproverDriverDocumetns(Long driverDocumentId) {

		DriverDocument existingDriverDocument = driverDocumentRepository.findById(driverDocumentId).get();

		Driver existingDriver = existingDriverDocument.getDriver();

		System.out.println("line 182" + existingDriverDocument);

		// Create a new DriverResponseDTO and populate it with data from Driver and
		// DriverDocument entities
		DriverApproveRequestDto driverApproveRequestDto = new DriverApproveRequestDto();
		driverApproveRequestDto.setName(existingDriver.getName());
		driverApproveRequestDto.setAddress(existingDriver.getAddress());
		driverApproveRequestDto.setPhoneNo(existingDriver.getPhoneNo());
		driverApproveRequestDto.setAltPhoneNumber(existingDriver.getAltPhoneNumber());
		driverApproveRequestDto.setEmail(existingDriver.getEmail());
		driverApproveRequestDto.setDpassbook(existingDriverDocument.getDpassbook());
		driverApproveRequestDto.setDsignature(existingDriverDocument.getDsignature());
		driverApproveRequestDto.setDriverPanCard(existingDriverDocument.getDriverPanCard());
		driverApproveRequestDto.setAddress(existingDriverDocument.getDAddressproof());

		return driverApproveRequestDto;
	}

	@Override
	public List<DriverApproveRequestDto> getRejectedDriverList() {

		List<DriverDocument> driverDocumentList = driverDocumentRepository.findAll();
		List<DriverDocument> rejectedDriverList = driverDocumentList.stream()
				.filter(driverDocument -> driverDocument.isRejected()).collect(Collectors.toList());
		List<DriverApproveRequestDto> newDriverDocumentDto = new ArrayList<>();
		for (DriverDocument driverDocumentRejected : rejectedDriverList) {

			Long driverDocumentId = driverDocumentRejected.getDriverDocumentid();
			newDriverDocumentDto.add(getUnapproverDriverDocumetns(driverDocumentId));
		}

		return newDriverDocumentDto;
	}

	@Override
	public Admin getHardcodedAdmin() {
		Admin admin = new Admin();

		admin.setPassword("Admin123");
		admin.setEmail("khushimashram62@gmail.com");

		adminRepository.save(admin);

		return admin;
	}

//	@Override
//	public Integer getNoOfRequestedDriver(int pendingDrivers) {
//		
//		return pendingDrivers;
//	}

	@Override
	public boolean setNewPasswordForAdmin(Long adminId, PasswordRequestDto passwordDto) {

		// Retrieve user by userId
		Admin admin = adminRepository.findById(adminId).orElse(null);

		if (admin != null) {

			if (isValidPassword(passwordDto.getNewpassword(), passwordDto.getConfirmPassword())) {

				Optional<UserIdentity> userIdentityOptional = userIdentityRepository.findByPhoneNo(admin.getPhoneNo());

				if (userIdentityOptional.isPresent()) {
					UserIdentity userIdentity = userIdentityOptional.get();

					if (admin.getPhoneNo().equals(userIdentity.getPhoneNo())) {

						String encodedPassword = passwordEncoder.encode(passwordDto.getNewpassword());

						admin.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						adminRepository.save(admin);
						userIdentityRepository.save(userIdentity);
						return true;
					}
				}
			}
		}
		return false; // Password update failed

	}

	private boolean isValidPassword(String newPassword, String confirmPassword) {
		// Check if passwords match
		if (!newPassword.equals(confirmPassword)) {
			return false;
		}

		// Check if the password contains at least one number and one special character
		if (!newPassword.matches(".*\\d.*") || !newPassword.matches(".*[!@#$%^&*()-_=+\\[\\]{}|;:'\",.<>/?].*")) {
			return false;
		}

		return true;
	}

	@Override
	public boolean verifyEmailOtp(Long adminId, String otp) {

		Optional<ManageOtp> manageOtp = manageOtpRepository.findByAdmin_AdminId(adminId);

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

	@Override
	public List<DriverApproveRequestDto> getApprovedDrivers() {
		List<DriverApproveRequestDto> approvedDrivers = new ArrayList<>();
		List<DriverDocument> driverDocuments = driverDocumentRepository.findAll();

		for (DriverDocument driverDocument : driverDocuments) {
			if (driverDocument.isApproved()) {
				Long driverId = driverDocument.getDriver().getDriverId();
				Driver driver = driverRepository.findById(driverId).orElse(null);
				if (driver != null) {
					DriverApproveRequestDto dto = new DriverApproveRequestDto();
					dto.setName(driver.getName());
					dto.setVehicleNo(driver.getVehicle().getVehicleNo());
					approvedDrivers.add(dto);
				}
			}
		}

		return approvedDrivers;
	}

	@Override
	public DriverApproveRequestDto getApprovedDriverDetails(Long driverId) {
		DriverDocument driverDocument = driverDocumentRepository.findByDriverId(driverId);

		if (driverDocument != null && driverDocument.isApproved()) {
			Driver driver = driverRepository.findById(driverId).orElse(null);
			if (driver != null) {
				DriverApproveRequestDto dto = new DriverApproveRequestDto();
				dto.setName(driver.getName());
				dto.setAddress(driver.getAddress());
				dto.setPhoneNo(driver.getPhoneNo());
				dto.setAltPhoneNumber(driver.getAltPhoneNumber());
				dto.setEmail(driver.getEmail());
				dto.setDpassbook(driverDocument.getDpassbook());
				dto.setDsignature(driverDocument.getDriverSignature());
				dto.setDriverPanCard(driverDocument.getDriverPanCard());
				dto.setDAddressproof(driverDocument.getDAddressproof());
				dto.setVehicleNo(driver.getVehicle().getVehicleNo());
				return dto;
			}

		}

		return null;

	}

	@Override
	public DriveracceptpaymentDto customerPayment(Long driverId) {
		// TODO Auto-generated method stub
		BigDecimal totalAmount = null;
//		BigDecimal todayTotalRide = BigDecimal.ZERO;
		Long totalride = null;

		Driver driver = driverRepository.findById(driverId).orElse(null);

		Vehicle vehicle = vehicleRepository.findById(driverId).orElse(null);
		String vehicleno = vehicle.getVehicleNo();
		List<PaymentActivity> paymentActivities = paymentRepository.findByDriver_DriverId(driverId);
		Map<LocalDate, BigDecimal> amount = calculateTotalAmountByDate(paymentActivities);

		Map<LocalDate, Long> order = calculateNumberOfOrders(paymentActivities);

		for (Map.Entry<LocalDate, Long> entry : order.entrySet()) {

			totalride = entry.getValue();
//				 todayTotalRide = entry.getKey().isEqual(targetDate2) ? entry.getValue() : BigDecimal.ZERO;

		}

//		String myString2 = Integer.toString(order);
		LocalDate targetDate2 = LocalDate.now();

		for (Map.Entry<LocalDate, BigDecimal> entry : amount.entrySet()) {

			totalAmount = entry.getValue();
//			 todayTotalRide = entry.getKey().isEqual(targetDate2) ? entry.getValue() : BigDecimal.ZERO;

		}

		List<PaymentActivity> listOfHis = paymentRepository.findByDriver_DriverId(driverId);

//		LocalDate targetDate = LocalDate.now(); // Set your target date here
		// System.out.println("targetdate :- "+targetDate);

		List<PaymentHistoryDto> filteredList = listOfHis.stream()
//		       .filter(history -> history.getLocalDatetime().toLocalDate() != null && history.getLocalDatetime().toLocalDate().isEqual(targetDate)) // Filter by the target date
				.map(history -> new PaymentHistoryDto(history.getUser().getName(), history.getAmount(),
						history.getLocalDatetime()))
				.collect(Collectors.toList());
		System.out.println(filteredList);

		return new DriveracceptpaymentDto(driverId.toString(), vehicleno, totalAmount.toString(), totalride.toString(),
				filteredList);

//	    return new DriveracceptpaymentDto(driverId.toString(), vehicleno, myString2, filteredList);
	}

	public Map<LocalDate, BigDecimal> calculateTotalAmountByDate(List<PaymentActivity> paymentActivities) {
		Map<LocalDate, BigDecimal> totalAmountByDate = new TreeMap<>();
		Map<LocalDate, Integer> dateCountMap = new TreeMap<>();

		for (PaymentActivity activity : paymentActivities) {
			LocalDate date = activity.getLocalDatetime().toLocalDate();
			BigDecimal amount = new BigDecimal(activity.getAmount());
			LocalDate targetdate = LocalDate.now();

			if (date.equals(targetdate)) {
				// If the date matches the target date, update the total amount
				totalAmountByDate.put(date, totalAmountByDate.getOrDefault(date, BigDecimal.ZERO).add(amount));
				dateCountMap.put(date, dateCountMap.getOrDefault(date, 0) + 1);
			}
		}

		return totalAmountByDate;
	}

	public static Map<LocalDate, Long> calculateNumberOfOrders(List<PaymentActivity> paymentHistoryList) {
		Map<LocalDate, Long> dateCountMap = new TreeMap<>();

		for (PaymentActivity payment : paymentHistoryList) {
			LocalDateTime localDateTime = payment.getLocalDatetime();
			LocalDate date = localDateTime.toLocalDate();
			dateCountMap.put(date, dateCountMap.getOrDefault(date, 0L) + 1);
		}

		return dateCountMap;

	}

	@Override
	public Admin updateAdminProfile(AdminDataDto adminDataJson, String s3Url) throws Exception {

		// Retrieve the admin from the database
		Admin admin = adminRepository.findById(adminDataJson.getAdminId()).get();

		// Update admin data if provided in the DTO
		if (adminDataJson.getName() != null && !adminDataJson.getName().isEmpty()) {
			admin.setName(adminDataJson.getName());
		}
		if (adminDataJson.getEmail() != null && !adminDataJson.getEmail().isEmpty()) {
			admin.setEmail(adminDataJson.getEmail());
		}
		if (adminDataJson.getPhoneNo() != null && !adminDataJson.getPhoneNo().isEmpty()) {
			admin.setPhoneNo(adminDataJson.getPhoneNo());
		}
		if (adminDataJson.getAddress() != null && !adminDataJson.getAddress().isEmpty()) {
			admin.setAddress(adminDataJson.getAddress());
		}

		// Update profile image link if provided
		if (s3Url != null && !s3Url.isEmpty()) {
			admin.setProfileImgLink(s3Url);
		}

		// Save the updated admin
		return adminRepository.save(admin);

	}

	public AdminProfileEditDto getAdminProfile(Long AdminId) throws Exception {
		Admin admin = adminRepository.findById(AdminId)
				.orElseThrow(() -> new Exception("Admin not found with id: " + AdminId));

		// Convert Admin entity to AdminProfileEditDto
		AdminProfileEditDto adminProfileEditDto = new AdminProfileEditDto();
		adminProfileEditDto.setAdminId(admin.getAdminId());
		adminProfileEditDto.setProfileImgLink(admin.getProfileImgLink());
		adminProfileEditDto.setName(admin.getName());
		adminProfileEditDto.setEmail(admin.getEmail());
		adminProfileEditDto.setPhoneNo(admin.getPhoneNo());
		adminProfileEditDto.setAddress(admin.getAddress());

		return adminProfileEditDto;
	}

//
////update admin profile
//	@Override
//	public Admin updateAdmin(Long adminId, AdminDataDto adminDataDto, MultipartFile file) throws IOException {
//		// TODO Auto-generated method stub
//		
//		Admin updateAdmin = adminRepository.findById(adminId)
//	            .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + adminId));
//		
//		System.out.println(updateAdmin);
//		
//		 String fileName = driverServiceImpl.saveImage(file);
//		
//		updateAdmin.setAdminName(adminDataDto.getAdminName());
//		updateAdmin.setPhoneNo(adminDataDto.getPhoneNo());
//		updateAdmin.setEmail(adminDataDto.getEmail());
//		updateAdmin.setAddress(adminDataDto.getAddress());
//		
//		
//		
//		updateAdmin.setProfileImgLink(ServletUriComponentsBuilder.fromCurrentContextPath().path("/RidoApp/images").path(file.getOriginalFilename()).toUriString());
//		updateAdmin.setProfileImg(file.getBytes());
//		updateAdmin.setProfileImgName(fileName);
//		 
//		    return adminRepository.save(updateAdmin);
//	}

	public ProfileDto getProfileByEmail(String email) {
		Optional<Admin> optionalAdmin = Optional.ofNullable(adminRepository.findByEmail(email));

		if (optionalAdmin.isPresent()) {
			Admin admin = optionalAdmin.get();
			ProfileDto profileDto = new ProfileDto();
			profileDto.setName(admin.getName());
			profileDto.setProfileImgLink(admin.getProfileImgLink());
			profileDto.setEmail(admin.getEmail());
			profileDto.setId(admin.getAdminId()); // Assuming id is a String in your ProfileDto

			return profileDto;
//		} else {
//			throw new RuntimeException("Admin not found with email: " + email);
		}
		return null;
	}

	@Override
	public BigDecimal getTotalExpensesForCurrentMonth() {
		Double hubEmployeePayment = hubPaymentService.getSumOfAllAmountsOfHubEmployeeForCurrentMonth();
		Double driverPayment = hubPaymentService.getSumOfAllAmountsOfDriverForCurrentMonth();
		BigDecimal repairCost = hubService.getTotalCostOfRepairingForCurrentMonth();
		return BigDecimal.valueOf(hubEmployeePayment).add(BigDecimal.valueOf(driverPayment)).add(repairCost);
	}

	@Override
	public BigDecimal getTotalExpensesForCurrentMonthByHub(Long hubId) {
		Double hubEmployeePayment = hubPaymentService.getSumOfAllAmountsOfHubEmployeeForCurrentMonthByHub(hubId);
		Double driverPayment = hubPaymentService.getSumOfAllAmountsOfDriverForCurrentMonthByHub(hubId);
		BigDecimal repairCost = hubService.getTotalCostOfRepairingForCurrentMonthByHub(hubId);
		return BigDecimal.valueOf(hubEmployeePayment).add(BigDecimal.valueOf(driverPayment)).add(repairCost);
	}

	@Override
	public BigDecimal getAvailableAmountForCurrentMonth() {
		BigDecimal totalAmount = bookingService.getTotalAmountForCurrentMonth();
		BigDecimal totalExpenses = getTotalExpensesForCurrentMonth();
		return totalAmount.subtract(totalExpenses);
	}

	@Override
	public BigDecimal getAvailableAmountForCurrentMonthByHub(Long hubId) {
		BigDecimal totalAmount = bookingService.getTotalAmountForCurrentMonthByHub(hubId);
		BigDecimal totalExpenses = getTotalExpensesForCurrentMonthByHub(hubId);
		return totalAmount.subtract(totalExpenses);
	}

	public Object getRevenueOfRido(LocalDate startDate) {

		List<Booking> allBookingList = bookingRepository.findAll();
		if (allBookingList.size() != 0) {

			LocalDateTime startOfWeek = startDate.atStartOfDay().with(java.time.DayOfWeek.MONDAY);
			LocalDateTime endOfWeek = startOfWeek.plusDays(6).plusHours(23).plusMinutes(59).plusSeconds(59);

			List<Booking> bookingsThisWeek = allBookingList.stream()
					.filter(booking -> booking.getTimeDuration().getStartDateTime().isAfter(startOfWeek)
							&& booking.getTimeDuration().getStartDateTime().isBefore(endOfWeek))
					.collect(Collectors.toList());
			if (bookingsThisWeek.size() != 0) {
				Map<LocalDate, BigDecimal> dailyRevenueMap = bookingsThisWeek.stream()
						.collect(Collectors.groupingBy(
								booking -> booking.getTimeDuration().getStartDateTime().toLocalDate(),
								Collectors.mapping(Booking::getTotalAmount,
										Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
				System.out.println("dailyRevenueMap : " + dailyRevenueMap);
				return dailyRevenueMap;

			} else {

				return "there is no payment availabe on this weeak";
			}

		} else {
			return "there is no payment available right now";
		}
	}

	@Override
	public ResponseLogin getByPhoneno(String phoneno) {
		// TODO Auto-generated method stub
		Optional<Admin> admin = adminRepository.findByPhoneNo(phoneno);
		if (admin.isPresent()) {
			ResponseLogin response = new ResponseLogin();
			response.setUserId(admin.get().getAdminId());
			response.setEmail(admin.get().getEmail());
			response.setPhoneNo(admin.get().getPhoneNo());
			return response;
		}
		return null;
	}

	@Override
	public Map<String, BigDecimal> getMonthlyTotalExpensesForYear(int year) {
		Map<String, BigDecimal> monthlyExpenses = new TreeMap<>();
		// Initialize the map with all months set to BigDecimal.ZERO
		for (Month month : Month.values()) {
			monthlyExpenses.put(month.toString(), BigDecimal.ZERO);
		}

		// Aggregate monthly sums from each repository and add them to the map
		List<Object[]> hubEmployeeSums = hubEmployeePaymentRepository.findMonthlySumsForHubEmployee(year);
		List<Object[]> driverSums = driverPaymentRepository.findMonthlySumsForDriver(year);
		List<Object[]> carRepairSums = carRepairDetailCostRepository.findMonthlySumsForCarRepair(year);

		// Process the sums and add them to the monthlyExpenses map
		processMonthlySums(hubEmployeeSums, monthlyExpenses);
		processMonthlySums(driverSums, monthlyExpenses);
		processMonthlySums(carRepairSums, monthlyExpenses);

		return monthlyExpenses;
	}

	private void processMonthlySums(List<Object[]> monthlySums, Map<String, BigDecimal> monthlyExpenses) {
		for (Object[] sum : monthlySums) {
			Integer month = (Integer) sum[0];
			BigDecimal amount = (BigDecimal) sum[1];
			String monthName = Month.of(month).toString();
			monthlyExpenses.computeIfPresent(monthName, (key, value) -> value.add(amount));
		}
	}

	@Override
	public CarRepair carRepairAccepted(Long carRepairId, MaintenanceApprovalStatus maintenanceApprovalStatus) {
		CarRepair carRepair = carRepairRepository.findById(carRepairId)
				.orElseThrow(() -> new IllegalArgumentException("CarChange with id " + carRepairId + " not found"));

		carRepair.setMaintenanceApprovalStatus(MaintenanceApprovalStatus.ACCEPTED);
		carRepair.setCarRepairStatus(CarRepairStatus.PROCESSING);

		 String uniqueKey = generateUniqueKey(carRepair.getHub().getHubName());
		    carRepair.setCarRepairUniqueKey(uniqueKey);
		carRepairRepository.save(carRepair);

		return carRepair;
	}
	
    private String generateUniqueKey(String hubName) {
	    
	    String hubPrefix = hubName.substring(0, 3).toUpperCase();
        int randomNumber = (int) (Math.random() * 100);
        String formattedNumber = String.format("%02d", randomNumber);

	     return hubPrefix + formattedNumber;
	}

    @Override
	public CarRepair carRepairRejected(Long carRepairId, MaintenanceApprovalStatus maintenanceApprovalStatus) {
	    CarRepair carRepair = carRepairRepository.findById(carRepairId)
	            .orElseThrow(() -> new IllegalArgumentException("CarRepair with id " + carRepairId + " not found"));

	    carRepair.setMaintenanceApprovalStatus(MaintenanceApprovalStatus.REJECTED);
	    carRepair.setCarRepairStatus(CarRepairStatus.REJECT);
	    carRepairRepository.save(carRepair);

	    return carRepair;
	}

//	@Override
//	public String addVehicle(VehicleDataDto dataDto, String s3Url, Long adminId) {
//	    try {
//	        Vehicle vehicle = new Vehicle();
//	        vehicle.setVehicleName(dataDto.getVehicleName());
//	        vehicle.setPrice(dataDto.getPrice());
//	        vehicle.setBattery(dataDto.getBattery());
//	        vehicle.setChargingTime(dataDto.getChargingTime());
//	        vehicle.setSeatingCapacity(dataDto.getSeatingCapacity());
//	        vehicle.setTransmissionTypo(dataDto.getTransmissionTypo());
//	        vehicle.setVehicleNo(dataDto.getVehicleNo());
//	        vehicle.setInsuranceNo(dataDto.getInsuranceNo());
//	      //  vehicle.setVehicleImgLink(s3Url);
//	        vehicle.setVehicleStatus(VehicleStatus.AVAILABLE);
//	        vehicle.setPricePerKm(dataDto.getPricePerKm());
//	        vehicle.setVehicleServiceType(dataDto.getVehicleServiceType());
//	        vehicle.setVehicleType(dataDto.getVehicleType());
//
//	        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);
//	        if (optionalAdmin.isPresent()) {
//	            Admin admin = optionalAdmin.get();
//	            vehicle.setAdmin(admin);
//	        } else {
//	            throw new IllegalArgumentException("Admin not found with id: " + adminId);
//	        }
//
//	        vehicleRepository.save(vehicle);
//
//	        return "New vehicle added successfully!";
//	    } catch (DataIntegrityViolationException e) {
//	        // This exception is thrown when unique constraints are violated
//	        e.printStackTrace();
//	        return "This Vehicle with the same vehicle number or insurance number already exists.";
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return "Error adding vehicle";
//	    }
//	}

	@Override
	public List<Vehicle> getVehiclesByAdminId(Long adminId) {
		return vehicleRepository.findByAdmin_AdminId(adminId);
	}

	@Override
	public List<Vehicle> assignHubToVehicles(List<ListOfAssignVehiclesDto> assignVehiclesList) {
		List<Vehicle> assignedVehicles = new ArrayList<>();
		// list of assign vehicles
		for (ListOfAssignVehiclesDto assignVehicles : assignVehiclesList) {
			
			String hubName = assignVehicles.getHubName();
			String state = assignVehicles.getState();
			String city = assignVehicles.getCity();		
			List<Long> vehicleIds = assignVehicles.getVehicleIds();

			// Assuming you have a now variable for current date/time
			LocalDateTime now = LocalDateTime.now();
			Hub hub = hubRepository.findByHubName(hubName)
					.orElseThrow(() -> new RuntimeException("Hub not found"));

			List<Vehicle> vehicles = vehicleRepository.findAllById(vehicleIds);
//	        vehicles.forEach(vehicle ->  {vehicle.setHub(hub);vehicle.setAssignHubDate(now)} );

			// Assign the hub and date to each vehicle
			vehicles.forEach(vehicle -> {
				vehicle.setHub(hub);
				vehicle.setAssignHubDate(now);
			});

			assignedVehicles.addAll(vehicleRepository.saveAll(vehicles));
		}

		return assignedVehicles;
	}

	

	@Override
	public List<DriverPaymentDetail> getPendingPaymentsByHubId(Long hubId) {
		return driverPaymentDetailRepo.findPendingPaymentsByHubId(hubId);
	}

	@Override
	public Optional<DriverPaymentDetail> getPaymentDetailByHubIdAndDriverId(Long hubId, Long driverId) {
		return driverPaymentDetailRepo.findByHubIdAndDriverId(hubId, driverId);
	}

//	@Override
//	public List<CourierEbike> assignHubToCourierVehicles(List<ListOfAssignVehiclesDto> assignVehiclesList) {
//		// TODO Auto-generated method stub
//		  List<CourierEbike> assignedVehicles = new ArrayList<>();
//
//	        for (ListOfAssignVehiclesDto assignVehicles : assignVehiclesList) {
//	            String managerName = assignVehicles.getManagerName();
//	            System.out.println(assignVehicles.getManagerName());
//	            System.out.println(assignVehicles.getLocation());
//	            String location = assignVehicles.getLocation();
//	            List<Long> vehicleIds = assignVehicles.getVehicleIds();
//               
//	            
//	            Hub hub = hubRepository.findByManagerNameAndLocation(managerName, location)
//	                                    .orElseThrow(() -> new RuntimeException("Hub not found"));
//
//	            List<CourierEbike> vehicles = courierEbikeRepository.findAllById(vehicleIds);
//	            vehicles.forEach(vehicle -> vehicle.setHub(hub); vehicle);
//
//	            assignedVehicles.addAll(courierEbikeRepository.saveAll(vehicles));
//	        }
//
//	        return assignedVehicles;
//	}

	@Override
	public DriverPaymentDto driverToDto(DriverPaymentDetail driverDetails) {
		DriverPaymentDto driverPaymentDto = new DriverPaymentDto();
		DriverDocument driverDocument = driverDocumentRepository
				.findByDriverId(driverDetails.getDriver().getDriverId());
		driverPaymentDto.setAccountNo(driverDocument.getAccountNo());
		driverPaymentDto.setBeneficiaryName(driverDetails.getDriver().getName());
		driverPaymentDto.setEmail(driverDetails.getDriver().getEmail());
		driverPaymentDto.setPnoneNo(driverDetails.getDriver().getAltPhoneNumber());
		driverPaymentDto.setPayableAmount(driverDetails.getAmount());
		driverPaymentDto.setInvoiceNo("invoice");
		driverPaymentDto.setNotes("some notes");
		driverPaymentDto.setPayOutNarration("norration");
		driverPaymentDto.setPaymentMode("online");
		driverPaymentDto.setPayOutNarration("pay out narration");

		return driverPaymentDto;
	}

	@Override

	public ByteArrayInputStream getDriverPaymentExcelData(LocalDate date) throws IOException {

		List<Hub> hubList = hubRepository.findAll();
		List<DriverPaymentDetail> ListOfDriverPaymentDetails = new ArrayList<>();
		List<DriverPaymentDto> ListOfDriverPaymentDto = new ArrayList<>();
		for (Hub hub : hubList) {
			ListOfDriverPaymentDetails = driverPaymentDetailRepo.findByHubIdAndDate(hub.getHubId(), date);
		}
		for (DriverPaymentDetail driverPaymentDetails : ListOfDriverPaymentDetails) {

			DriverPaymentDto driverPaymentDto = driverToDto(driverPaymentDetails);
			ListOfDriverPaymentDto.add(driverPaymentDto);
		}
		return GenerateFile.dataToExcel(ListOfDriverPaymentDto);
	}

	public List<Vehicle> getVehiclesByHubIdAndAdminId(Long hubId, Long adminId) {
		// TODO Auto-generated method stub
		List<Vehicle> vehicles = vehicleRepository.findByAdmin_AdminId(adminId);

		// Filter vehicles where hub is null
		List<Vehicle> vehiclesWithoutHub = vehicles.stream()
				.filter(vehicle -> vehicle.getHub().getHubId().equals(hubId)).collect(Collectors.toList());
		System.out.println(vehiclesWithoutHub);
		return vehiclesWithoutHub;
	}

	public List<Vehicle> getVehiclesWithNoHub(Long adminId) {
		List<Vehicle> vehicles = vehicleRepository.findByAdmin_AdminId(adminId);

		List<Vehicle> filteredVehicles = vehicles.stream().filter(vehicle -> {
			Hub hub = vehicle.getHub();
			return hub == null;
		}).collect(Collectors.toList());

		System.out.println(filteredVehicles);
		return filteredVehicles;
	}

	@Override
	public List<CourierEbike> assignHubToCourierVehicles(List<ListOfAssignVehiclesDto> assignVehiclesList) {
		List<CourierEbike> assignedVehicles = new ArrayList<>();

		for (ListOfAssignVehiclesDto assignVehicles : assignVehiclesList) {
//			String managerName = assignVehicles.getManagerName();
			String hubName = assignVehicles.getHubName();
			String state  = assignVehicles.getCity();
			String city = assignVehicles.getState();
			List<Long> courierEbikeId = assignVehicles.getCourierEbikeId();

			// Assuming you have a now variable for current date/time
			LocalDateTime now = LocalDateTime.now();

			// Fetch the hub based on manager name and location
			Hub hub = hubRepository.findByHubName( hubName)
					.orElseThrow(() -> new RuntimeException("Hub not found"));

			// Fetch the list of vehicles using their IDs
			List<CourierEbike> vehicles = courierEbikeRepository.findAllById(courierEbikeId);

			// Assign the hub and date to each vehicle
			vehicles.forEach(vehicle -> {
				vehicle.setHub(hub);
				vehicle.setAssignHubDate(now);
			});

			// Save the updated vehicles back to the repository
			assignedVehicles.addAll(courierEbikeRepository.saveAll(vehicles));
		}

		return assignedVehicles;
	}

	public List<Vehicle> getVehiclesWithAvailableHub(Long adminId) {
		return vehicleRepository.findByAdminAdminIdAndHubIsNotNull(adminId);
	}

	public List<VehicleCourierEbikeDto> getVehiclesEbikesWithAvailableHub(Long adminId) {
		return null;
//	        List<Vehicle> vehicles = vehicleRepository.findByAdminAdminIdAndHubIsNotNull(adminId);
//	        List<VehicleCourierEbikeDto> vehicleCourierEbikeDtos = new ArrayList<>();
//
//	        for (Vehicle vehicle : vehicles) {
//	            // Find the corresponding courier ebike with a non-null hub for the current vehicle
////	            CourierEbike courierEbike = courierEbikeRepository.findByAdminAdminIdAndHubIsNotNull(adminId);
//	            if (courierEbike != null) {
//	                vehicleCourierEbikeDtos.add(new VehicleCourierEbikeDto(vehicle, courierEbike));
//	            }
//	        }
//
//	        return vehicleCourierEbikeDtos;
	}

	@Override
	public List<CourierEbike> getCourierEbikeWithAvailableHub(Long adminId) {

		return courierEbikeRepository.findByAdminAdminIdAndHubIsNotNull(adminId);
	}

	@Override
	public boolean changePassword(Long adminId, ChangePasswordRequestDto changePasswordRequestDto) {
		// Retrieve user from the database

		Admin admin = adminRepository.findById(adminId).orElse(null);

		// Check if the old password matches the stored password
		System.out.println(admin.getPassword() + "user old pass");

		System.out.println(admin.getPhoneNo() + " phoneno");
		if (admin != null && passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), admin.getPassword())) {

			Optional<UserIdentity> userIdentityOptional = userIdentityRepository.findByPhoneNo(admin.getPhoneNo());
			System.out.println(userIdentityOptional.get() + " user phoneno line 148");
			// Check if the new password and confirm password match
			if (userIdentityOptional.isPresent()) {
				UserIdentity userIdentity = userIdentityOptional.get();

				// Optional: You might want to log userIdentity to ensure it's retrieved
				// correctly
				System.out.println("UserIdentity: " + userIdentity);

				System.out.println(userIdentity.getPhoneNo());

				if (admin.getPhoneNo().equals(userIdentity.getPhoneNo())) {
					if (changePasswordRequestDto.getNewPassword()
							.equals(changePasswordRequestDto.getConfirmPassword())) {
						String encodedPassword = passwordEncoder.encode(changePasswordRequestDto.getNewPassword());

						admin.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						adminRepository.save(admin);
						userIdentityRepository.save(userIdentity);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String addVehicle(VehicleDataDto dataDto, List<String> vehicleImgUrls, String invoiceUrl, Long adminId) {
		try {
			Vehicle vehicle = new Vehicle();
			vehicle.setVehicleName(dataDto.getVehicleName());
			vehicle.setPrice(dataDto.getPrice());
			vehicle.setSeatingCapacity(dataDto.getSeatingCapacity());
			vehicle.setVehicleNo(dataDto.getVehicleNo());
			vehicle.setInsuranceNo(dataDto.getInsuranceNo());
			// vehicle.setVehicleImgLink(vehicleImgUrl);
			vehicle.setVehicleStatus(VehicleStatus.AVAILABLE);
			vehicle.setPricePerKm(dataDto.getPricePerKm());
			vehicle.setVehicleServiceType(dataDto.getVehicleServiceType());
			vehicle.setVehicleType(dataDto.getVehicleType());
			vehicle.setChassisNo(dataDto.getChassisNo());
			vehicle.setVehiclerange(dataDto.getVehiclerange());
			vehicle.setDateOfPurchase(dataDto.getDateOfPurchase());
			vehicle.setInvoice(invoiceUrl);

			// Convert list of URLs to a comma-separated string for storage in database
			String vehicleImgLinks = String.join(",", vehicleImgUrls);
			System.out.println("vehicleImgLinks=" + 123 + vehicleImgLinks);
			vehicle.setVehicleImgLink(vehicleImgLinks);

			Optional<Admin> optionalAdmin = adminRepository.findById(adminId);
			if (optionalAdmin.isPresent()) {
				Admin admin = optionalAdmin.get();
				vehicle.setAdmin(admin);
			} else {
				throw new IllegalArgumentException("Admin not found with id: " + adminId);
			}

			vehicleRepository.save(vehicle);

			return "New vehicle added successfully!";
		} catch (DataIntegrityViolationException e) {
			// This exception is thrown when unique constraints are violated
			e.printStackTrace();
			return "This Vehicle with the same vehicle number, insurance number, or chassis number already exists.";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error adding vehicle";
		}
	}

	@Override
	public List<Hub> getAllHubList(Long adminId) {
		return hubRepository.findByAdmin_AdminId(adminId);
	}

	@Override
	public String addCourierEbikeVehicle(CourierEbikeDto ebikeDataDto, List<String> s3Urls,String invoiceUrl, Long adminId) {
		try {
			Optional<Admin> adminOptional = adminRepository.findById(adminId);
			if (!adminOptional.isPresent()) {
				return "Admin with ID " + adminId + " not found.";
			}
			Admin admin = adminOptional.get();

			CourierEbike courierEbike = new CourierEbike();
			courierEbike.setInsuranceNo(ebikeDataDto.getInsuranceNo());
			courierEbike.setVehicleName(ebikeDataDto.getVehicleName());
			courierEbike.setVehicleNo(ebikeDataDto.getVehicleNo());
			courierEbike.setWeight(ebikeDataDto.getWeight());
			courierEbike.setTopSpeed(ebikeDataDto.getTopSpeed());
			courierEbike.setPricePerKm(ebikeDataDto.getPricePerKm());
			courierEbike.setRc(ebikeDataDto.getRc());
			courierEbike.setChassisNo(ebikeDataDto.getChassisNo());
			courierEbike.setEbikerange(ebikeDataDto.getEbikerange());
			courierEbike.setDateOfPurchase(ebikeDataDto.getDateOfPurchase());
			courierEbike.setInvoice(invoiceUrl);
			courierEbike.setVehicleStatus(VehicleStatus.AVAILABLE);
			courierEbike.setAdmin(admin);
			
			String vehicleImgLinks = String.join(",", s3Urls);
			System.out.println("vehicleImgLinks=" + 123 + vehicleImgLinks);
			courierEbike.setEbikeImage(vehicleImgLinks);


			courierEbikeRepo.save(courierEbike);

			return "Courier eBike added successfully.";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error adding Courier eBike.";
		}
	}

	@Override
	public Optional<Admin> getAdminById(Long adminId) {
		return adminRepository.findById(adminId);

	}

}
