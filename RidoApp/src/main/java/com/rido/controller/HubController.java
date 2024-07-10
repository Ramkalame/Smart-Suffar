package com.rido.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rido.Exceptions.BusinessException;
import com.rido.Exceptions.DriverNotFoundException;
import com.rido.Exceptions.ErrorResponse;
import com.rido.Exceptions.HubNotFoundException;
import com.rido.Exceptions.ResourceNotFoundException;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.dto.ActiveDriverInfoDto;
import com.rido.dto.AssignCarRequestDto;
import com.rido.dto.BookedOrderDTO;
import com.rido.dto.BookingComparisonDTO;
import com.rido.dto.BookingDTO;
import com.rido.dto.BookingDetailsDto;
import com.rido.dto.CarRepairDto;
import com.rido.dto.CarRepairRequestDto;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.CourierBookingDto1;
import com.rido.dto.CourierDetailsDto;
import com.rido.dto.CourierEbikeDto;
import com.rido.dto.DriverApproveRequestDto;
import com.rido.dto.DriverAvailableDto;
import com.rido.dto.DriverDataDto;
import com.rido.dto.DriverNameAvailableDto;
import com.rido.dto.DriverPaymentDetailDto;
import com.rido.dto.DriverReturnVehicleResponseDto;
import com.rido.dto.DriverRunningVehicleResponseDto;
import com.rido.dto.DriverUpdateRequestDto;
import com.rido.dto.DriveracceptpaymentDto;
import com.rido.dto.GetEmployeeListDto;
import com.rido.dto.HubDataDto;
import com.rido.dto.HubDriverDataDto;
import com.rido.dto.HubDriverDetailsDto;
import com.rido.dto.HubDriverPaymentDetailsDto;
import com.rido.dto.HubLocationDto;
import com.rido.dto.HubMangerProfileEditDto;
import com.rido.dto.IncompleteRideDto;
import com.rido.dto.NewBookingDto;
import com.rido.dto.PasswordChangeRequestDto;
import com.rido.dto.PaymentHistoryDto;
import com.rido.dto.ProfileDto;
import com.rido.dto.RentalBookingDto;
import com.rido.dto.TodayBookingDashboardDto;
import com.rido.dto.TotalBookingDto;
import com.rido.dto.TotalCompleteBookingDto;
import com.rido.dto.TransportAllListDto;
import com.rido.dto.VehicleDataDto;
import com.rido.dto.VehicleNameAvailableDto;
import com.rido.entity.Booking;
import com.rido.entity.CancellationReason;
import com.rido.entity.CarRepair;
import com.rido.entity.CarRepairDetailCost;
import com.rido.entity.Courier;
import com.rido.entity.CourierEbike;
import com.rido.entity.Driver;
import com.rido.entity.DriverDocument;
import com.rido.entity.DriverPaymentDetail;
import com.rido.entity.Hub;
import com.rido.entity.HubLocation;
import com.rido.entity.PromoCode;
import com.rido.entity.ReturnCar;
import com.rido.entity.ReturnCourierVehicle;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.RideOrderStatus;
import com.rido.entity.enums.VehicleStatus;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.BookingRepository;
import com.rido.repository.CourierEbikeRepository;
import com.rido.repository.DriverDocumentRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubLocationRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.PromoCodeRepository;
import com.rido.repository.ReturnCarRepository;
import com.rido.repository.ReturnCourierVehicleRepository;
import com.rido.repository.VehicleRepository;
import com.rido.service.AdminService;
import com.rido.service.BookingService;
import com.rido.service.CancellationService;
import com.rido.service.CourierService;
import com.rido.service.DriverPaymentDetailService;
import com.rido.service.DriverService;
import com.rido.service.HubEmployeeService;
import com.rido.service.HubService;
import com.rido.service.UserService;
import com.rido.service.VehicleService;
import com.rido.utils.ApiResponse;

@CrossOrigin(origins = { "http://10.0.2.2:8080", "http://localhost:3000" })
@RestController
@RequestMapping("/hub")
public class HubController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private CancellationService cancellationService;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private CourierEbikeRepository courierEbikeRepo;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private DriverService driverService;

	@Autowired
	private UserService userService;

	@Autowired
	private HubService hubService;

	@Autowired
	private DriverDocumentRepository driverDocumentRepository;

	@Autowired
	private DriverPaymentDetailService driverPaymentDetailService;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private HubLocationRepository hubLocationRepository;

	@Autowired
	private CourierService courierService;

	@Autowired
	private PromoCodeRepository promoCodeRepository;

	@Autowired
	private ReturnCarRepository returnCarRepository;

	@Autowired
	private ReturnCourierVehicleRepository returncouriervechileRepo;

	@Autowired
	private HubEmployeeService hubEmployeeService;

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

//	======= Deshbord ======

//	 1.New Booking
//		Jyoti
	// NEW BOOKING
	@GetMapping("/booked-orders")
	public ResponseEntity<List<BookedOrderDTO>> getBookedOrders() {
		List<BookedOrderDTO> bookedOrders = bookingService.getBookedOrders();

		if (bookedOrders.isEmpty()) {
			throw new DataAccessException("No booked orders found in the database.") {
			};
		}
		return ResponseEntity.ok(bookedOrders);
	}

//	 3.Active Rides
//		Abhilasha
	@GetMapping("/activeDriverRide")
	public List<Driver> activeCabDriverRide() {
		return driverService.findOngoingDriver();
	}

//	 4.Total Booking

//    =========== Driver List ==========

//		1.Total Driver List 

	@GetMapping("/get-all-driver-list")
	public ResponseEntity<List<Driver>> getDriverList() {
		System.out.println("driver list  " + driverRepository.findAll());
		return ResponseEntity.status(HttpStatus.OK).body(driverRepository.findAll());
	}

	// jyoti
	@GetMapping("/get-all-driver-list/{hubId}")
	public ResponseEntity<List<Driver>> getDriverListByHubId(@PathVariable Long hubId) {
		List<Driver> drivers = driverRepository.findByHub_HubId(hubId);

		if (drivers.isEmpty()) {
			throw new DataAccessException("Driver not found for this hub id - " + hubId) {
			};
		}

		return ResponseEntity.status(HttpStatus.OK).body(drivers);
	}

	// jyoti - only excaption handle
	@GetMapping("/getdriverbyid/{driverId}")
	public ResponseEntity<Driver> getDriverById(@PathVariable Long driverId) {
		Optional<Driver> driverOptional = adminService.findDriverById(driverId);

		if (driverOptional.isPresent()) {
			return ResponseEntity.ok(driverOptional.get());
		} else {
			throw new DataAccessException("Driver not found for this id- " + driverId) {
			};
		}
	}

	@DeleteMapping("/deletedriverbyid/{driverId}")
	public ResponseEntity<String> deleteDriverById(@PathVariable Long driverId) {

		String deleteDriverById = adminService.deleteDriverById(driverId);

		return ResponseEntity.ok(deleteDriverById);

	}

//	Abhilasha
	@GetMapping("/get-all-approved-driver-list")
	public ResponseEntity<List<DriverApproveRequestDto>> getApprovedDriverList() {
		List<DriverApproveRequestDto> approvedDrivers = adminService.getApprovedDrivers();
		if (approvedDrivers.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(approvedDrivers);
	}

//	Abhilasha
	@GetMapping("/get-approved-driver-details/{driverId}")
	public ResponseEntity<DriverApproveRequestDto> getApprovedDriverDetails(@PathVariable Long driverId) {
		DriverApproveRequestDto approvedDriver = adminService.getApprovedDriverDetails(driverId);
		if (approvedDriver == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(approvedDriver);
	}

	// jyoti
	// get online driver list - online status set from fronend
	@GetMapping("/Online-Driver-list/{hubId}")
	public List<DriverDataDto> getDriversByHubId(@PathVariable Long hubId) throws DataAccessException {

		List<Driver> drivers = driverRepository.findByHub_HubId(hubId);

		if (drivers.isEmpty()) {
			throw new DataAccessException("Online drivers are not present for the specified hub ID") {
			};
		}

		return drivers.stream().map(driver -> {

			DriverDataDto driverDataDto = new DriverDataDto();
			driverDataDto.setDriverId(driver.getDriverId());
			driverDataDto.setName(driver.getName());
			driverDataDto.setEmail(driver.getEmail());
			driverDataDto.setPhoneNo(driver.getPhoneNo());

			return driverDataDto;
		}).collect(Collectors.toList());

	}

	// driver all details - jyoti
	// calculate order payment - handel excaption - jyoti
	@GetMapping("driver-payment-history/{driverId}")
	public ResponseEntity<List<HubDriverPaymentDetailsDto>> getPaymentDetails(@PathVariable long driverId) {

		List<HubDriverPaymentDetailsDto> listofpayement = hubService.totalearninbydriver(driverId);
		if (listofpayement.isEmpty()) {

			throw new DataAccessException("There is not any payment history for this driver - " + driverId) {
			};
		} else {
			return ResponseEntity.ok(listofpayement);
		}
	}

	// driver with document - jyoti
	@GetMapping("driver-document-details/{driverId}")
	public HubDriverDetailsDto getDriverDetails(@PathVariable Long driverId) {

		Driver driver = driverService.getDriverById(driverId);
		DriverDocument driverDocument = driverDocumentRepository.getDriverDocumentByDriver_DriverId(driverId);

		if (driverDocument == null) {

			throw new DataAccessException("Driver document is not available for driver ID: " + driverId) {

			};
		}

		HubDriverDetailsDto detailsDto = new HubDriverDetailsDto();

		// DOCUMENT TYPE AND REGISTRATION NO IS NOT THERE
		detailsDto.setAddress(driver.getAddress());
		detailsDto.setAdharNo(driver.getAdharNo());

		detailsDto.setPanNo(driver.getPanNo());

		detailsDto.setDpassbook(driverDocument.getDpassbook());
		detailsDto.setDriverSignature(driverDocument.getDriverSignature());

		return detailsDto;

	}

	// driver with personal details - jyoti
	@GetMapping("/driver-data-details/{driverId}")
	public ResponseEntity<HubDriverDataDto> getDriverPaymentDetails(@PathVariable Long driverId) {
		// Retrieve driver details
		Driver driver = driverService.getDriverById(driverId);

		// Check if driver exists
		if (driver == null) {
			throw new DataAccessException("Driver details are not available for driver ID: " + driverId) {
			};
		}

		// Get total bookings for the driver
		Long todayTotalRide = driverService.getTotalBookingsByDriverId(driverId); // Assuming this method returns the
																					// total bookings

		// Create HubDriverDataDto object and set driver details and today's total ride
		HubDriverDataDto driverDataDto = new HubDriverDataDto();
		driverDataDto.setDriverId(driver.getDriverId());
		driverDataDto.setName(driver.getName());
		driverDataDto.setEmail(driver.getEmail());
		driverDataDto.setPhoneNo(driver.getPhoneNo());
		driverDataDto.setStatus(driver.getStatus());
		driverDataDto.setTodayTotalRide(todayTotalRide);

		return ResponseEntity.ok(driverDataDto);
	}

//		2. today's booking
//		3. Total Earing 

	// Rahul

	@GetMapping("/total-earning/{driverId}")
	public ResponseEntity<DriveracceptpaymentDto> fetchDataByDriverId(@PathVariable Long driverId) {

		DriveracceptpaymentDto data = adminService.customerPayment(driverId);
		if (data != null) {
			return ResponseEntity.ok(data);
		} else {
			throw new DriverNotFoundException("Driver with this " + driverId + " not found");
		}
	}

//		============ Car List ============

	// jyoti
	@GetMapping("/running-vehicle-list/{hubId}")
	public ResponseEntity<List<DriverRunningVehicleResponseDto>> getRunningDrivers(@PathVariable Long hubId) {
		List<DriverRunningVehicleResponseDto> runningDrivers = hubService.runningVehicleDetailsByHub(hubId);// runningVehicleDetails();
		return ResponseEntity.ok(runningDrivers);
	}

//    02 Return car List
	@GetMapping("/return-vehicle-list")
	public ResponseEntity<List<DriverReturnVehicleResponseDto>> getReturnVehicles() {

		List<DriverReturnVehicleResponseDto> list = hubService.returnVehiclesDetails();
		return ResponseEntity.ok(list);

	}

//		 1.Add car 

//		JALESHWARI
	@GetMapping("/get-vehicle-by-id/{vehicleId}")
	public ResponseEntity<?> getvehicleById(@PathVariable Long vehicleId) {
		Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);

		if (vehicle.isPresent()) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("There is  available vehicle this  id: " + vehicle);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is not available vehicle this id :" + vehicle);

	}

	// JALESHWARI
	@DeleteMapping("/delete-vehicle-by-id/{vehicleId}")
	public ResponseEntity<String> deleteVehicleById(@PathVariable Long vehicleId) throws UserNotFoundException {

		Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow();

		if (vehicle == null) {
			throw new UserNotFoundException("driver with this id " + vehicleId + " not found");

		} else {

			return ResponseEntity.status(HttpStatus.OK)
					.body("driver with this id " + vehicleId + " deleted succesfully");
		}
	}

//		 2. Running Car 
//		 3.Asign Car

// Rishi
	@GetMapping("/getAvailableDrivers")
	public ResponseEntity<List<DriverNameAvailableDto>> getAvailableDriversName() {
		return ResponseEntity.ok(hubService.getAvailableDriversName());
	}

// Rishi
	@GetMapping("/getAvailableCars")
	public ResponseEntity<List<VehicleNameAvailableDto>> getAvailableCars() {
		return ResponseEntity.ok(hubService.getAvailableCars());
	}

// Rishi
	@GetMapping("/driverAvailable")
	public ResponseEntity<List<DriverAvailableDto>> getAvailableDrivers() {
		List<Driver> drivers = hubService.getAvailableDrivers();
		if (drivers.isEmpty()) {
			throw new BusinessException("601", "No Driver Available");
		}
		List<DriverAvailableDto> driverLists = drivers.stream().map(driver -> {
			DriverAvailableDto driverList = new DriverAvailableDto();
			driverList.setDriverName(driver.getName());
			driverList.setPresentLocation(driver.getAddress());
			driverList.setAssignDriverEndpoint("/driverAvailable/" + driver.getDriverId() + "/assignDriver");
			return driverList;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(driverLists);
	}

	@GetMapping("/weekly")
	public ResponseEntity<List<Booking>> getWeeklyBookings() {
		List<Booking> bookings = bookingService.getWeeklyBookings();
		if (bookings.isEmpty()) {
			return ResponseEntity.noContent().build(); // Return 204 No Content if no bookings found
		}
		return ResponseEntity.ok(bookings);
	}

	@GetMapping("/weeklyGraph")
	public ResponseEntity<Map<String, Integer>> getWeeklyBookingGraph() {
		Map<String, Integer> graphData = bookingService.generateWeeklyBookingGraph();
		return ResponseEntity.ok(graphData);
	}

	@GetMapping("/weeklyPayment")
	public ResponseEntity<BigDecimal> getWeeklyPayment() {
		BigDecimal weeklyPayment = bookingService.calculateWeeklyPayment();
		return ResponseEntity.ok(weeklyPayment);
	}

	// Rishi
	@GetMapping("/getHubLocation")
	public ResponseEntity<List<HubLocationDto>> getHubLocation() {
		return ResponseEntity.ok(hubService.getHubLocation());
	}
//		========== Booking =======
	// Bookings
	// Rishi

//	1.New Booking
	// Rishi
	@GetMapping("/newBooking/{hubId}")
	public ResponseEntity<List<NewBookingDto>> newBooking(@PathVariable Long hubId) {
		List<Booking> bookings = bookingService.getNewBookings(hubId);
		if (bookings.isEmpty()) {
			throw new BusinessException("601", "No new Bookings Available");
		}
		List<NewBookingDto> bookingDTOs = new ArrayList<>();

		for (Booking booking : bookings) {
			NewBookingDto bookingDTO = new NewBookingDto();
			bookingDTO.setCustomerName(booking.getUser().getName());
			bookingDTO.setTripFrom(booking.getPickupLocation());
			bookingDTO.setStartDateTime(booking.getTimeDuration().getStartDateTime());
			bookingDTO.setTripTo(booking.getDropOffLocation());
			bookingDTO.setEndDateTime(booking.getTimeDuration().getEndDateTime());
			bookingDTOs.add(bookingDTO);
		}
		return ResponseEntity.ok(bookingDTOs);
	}

// Rishi
	@GetMapping("/bookingDetails/{hubId}")
	public ResponseEntity<List<BookingDetailsDto>> bookingDetails(@PathVariable Long hubId) {
		List<Booking> bookings = bookingService.getAllBookings(hubId);
		if (bookings.isEmpty()) {
			throw new BusinessException("601", "No Bookings Available");
		}
		List<BookingDetailsDto> bookingDTOs = new ArrayList<>();

		for (Booking booking : bookings) {
			BookingDetailsDto bookingDTO = new BookingDetailsDto();
			bookingDTO.setCustomerName(booking.getUser().getName());
			bookingDTO.setLocality(booking.getPickupLocation());
			bookingDTO.setSourceOfDestination(booking.getPickupLocation() + "  To  " + booking.getDropOffLocation());
//					bookingDTO.setTripTo(booking.getDropOffLocation());
//					bookingDTO.setEndDateTime(booking.getTimeDuration().getEndDateTime());
			bookingDTOs.add(bookingDTO);
		}
		return ResponseEntity.ok(bookingDTOs);
	}

// Rishi
	@GetMapping("/todaysBookingHistory/{hubId}")
	public ResponseEntity<List<TotalCompleteBookingDto>> todaysBookingHistory(@PathVariable Long hubId) {
		List<Booking> todaysBookings = bookingService.getTodaysBookings(hubId);
		if (todaysBookings.isEmpty()) {
			throw new BusinessException("601", "No Todays Bookings Available");
		}
		List<TotalCompleteBookingDto> bookingDtos = new ArrayList<>();
		for (Booking booking : todaysBookings) {
			TotalCompleteBookingDto bookingDto = new TotalCompleteBookingDto();
			bookingDto.setCustomerName(booking.getUser().getName());
			bookingDto.setTripFrom(booking.getPickupLocation());
			bookingDto.setStartDateTime(booking.getTimeDuration().getStartDateTime());
			bookingDto.setTripTo(booking.getDropOffLocation());
			bookingDto.setEndDateTime(booking.getTimeDuration().getEndDateTime());
			bookingDto.setDriverAssigned(booking.getDriver().getName());
			bookingDto.setVehicleName(booking.getDriver().getVehicle().getVehicleName());
			bookingDto.setPaymentMethod(booking.getTotalAmount());
			bookingDtos.add(bookingDto);
		}
		return ResponseEntity.ok(bookingDtos);
	}

//	3.Total Booking 
// Rishi
	@GetMapping("/totalCompleteBooking/{hubId}")
	public ResponseEntity<List<TotalCompleteBookingDto>> totalCompleteBooking(@PathVariable Long hubId) {
		List<Booking> bookings = bookingService.getAllBookings(hubId);
		if (bookings.isEmpty()) {
			throw new BusinessException("601", "No Bookings Available");
		}
		List<TotalCompleteBookingDto> bookingDtos = new ArrayList<>();

		for (Booking booking : bookings) {
			TotalCompleteBookingDto bookingDto = new TotalCompleteBookingDto();
			bookingDto.setCustomerName(booking.getUser().getName());
			bookingDto.setTripFrom(booking.getPickupLocation());
			bookingDto.setStartDateTime(booking.getTimeDuration().getStartDateTime());
			bookingDto.setTripTo(booking.getDropOffLocation());
			bookingDto.setEndDateTime(booking.getTimeDuration().getEndDateTime());
			bookingDto.setDriverAssigned(booking.getDriver().getName());
			bookingDto.setVehicleName(booking.getDriver().getVehicle().getVehicleName());
			bookingDto.setPaymentMethod(booking.getTotalAmount());
			bookingDtos.add(bookingDto);
		}
		return ResponseEntity.ok(bookingDtos);
	}

//  4. Get TotalAmount of Current Month	
// Rishi	
	@GetMapping("/totalAmountOfcurrentMonth")
	public ResponseEntity<BigDecimal> getTotalAmountForCurrentMonth() {
		BigDecimal totalAmount = bookingService.getTotalAmountForCurrentMonth();
		return ResponseEntity.ok(totalAmount);
	}

//Rishi	
	@GetMapping("/totalAmountOfcurrentMonth/{hubId}")
	public ResponseEntity<BigDecimal> getTotalAmountForCurrentMonthByHub(@PathVariable Long hubId) {
		BigDecimal totalAmount = bookingService.getTotalAmountForCurrentMonthByHub(hubId);
		return ResponseEntity.ok(totalAmount);
	}

	@GetMapping("/driver-booking-details")
	public ResponseEntity<?> getData() {
		// Fetch data from the repository
		Iterable<Booking> data = bookingRepository.findAll();
		// Return the fetched data as a response
		return ResponseEntity.ok(data);
	}

//		Chart Logic

//		Managment
//		1.Payment Details
//		2.Payment Driver by id
//		3.Payment

	@GetMapping("/driver-booking-details/{bookingId}")
	public ResponseEntity<?> getBookingDriverDetails(@PathVariable Long bookingId) {
		// Fetch data from the repository based on bookingId
		Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

		if (optionalBooking.isPresent()) {
			Booking booking = optionalBooking.get();
			// Return the fetched booking details as a response
			return ResponseEntity.status(HttpStatus.OK).body(booking);
		} else {
			// If booking not found, return a not found response
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/canclebooking")
	public List<CancellationReason> getAllCancel() {
		return cancellationService.getAllCancellation();

	}

	// COMPLETE BOOKING
//jyoti
	@GetMapping("/completed-orders")
	public ResponseEntity<List<BookedOrderDTO>> getCompletedOrders() {
		List<BookedOrderDTO> completedOrders = bookingService.getCompletedOrders();

		if (completedOrders.isEmpty()) {

			throw new DataAccessException("No completed orders found in the database.") {
			};

		} else {
			return ResponseEntity.ok(completedOrders);
		}
	}

	@GetMapping("/get-unapproved-driver-list")
	public ResponseEntity<List<DriverApproveRequestDto>> getUnapprovedDrivers() {
		System.out.println("line 221");
		List<DriverApproveRequestDto> findByApproved = adminService.findByUnApproved();
		System.out.println("line 223" + findByApproved);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(adminService.findByUnApproved());
	}

	@GetMapping("/list-of-rejected-driver")
	public ResponseEntity<List<DriverApproveRequestDto>> getRejectedDriverList() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(adminService.getRejectedDriverList());

	}

//	Abhilasha
	@GetMapping("/list-total-booking-details")
	public ResponseEntity<List<Booking>> getAllOrders() {
		List<Booking> orders = bookingService.getAllOrders();
		return ResponseEntity.ok(orders);
	}

//	RAM
	@GetMapping("/get-no-of-unapproved-drivers")
	public ResponseEntity<Integer> getUnApprovedNoOfDriver() {

		List<DriverApproveRequestDto> listOfDriver = this.adminService.findByUnApproved();

		int noOfUnApprovedDriver = listOfDriver.size();

		return ResponseEntity.status(HttpStatus.ACCEPTED).body(noOfUnApprovedDriver);

	}

//       Abhilasha
	@GetMapping("/totalCancelBooking")
	public List<TotalBookingDto> getTotalcancleBooking() {
		List<Booking> bookings = bookingService.getAllOrders();
		List<TotalBookingDto> bookingDtos = new ArrayList<>();

		for (Booking booking : bookings) {
			if (RideOrderStatus.CANCELLED.equals(booking.getRideOrderStatus())) { // Check if ride status is "cancel"
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

	// Rahul
	@GetMapping("/all-car-issue/{hubId}")
	public ResponseEntity<List<CarRepairRequestDto>> getAllcarIssue(@PathVariable Long hubId) throws Exception {
		List<CarRepairRequestDto> allcarissue = hubService.carProblems(hubId);
		if (allcarissue.isEmpty()) {
			throw new DataAccessException("No All carissue found in the database.") {
			};
		}
		return ResponseEntity.ok(allcarissue);
	}

	// Rahul
	@GetMapping("/list-of-allcarissue/{hubId}")
	public ResponseEntity<List<CarRepairRequestDto>> getAllCarRepairRequest(@PathVariable Long hubId) throws Exception {
		List<CarRepairRequestDto> listOfCarRepair = hubService.carRepairRequest(hubId);
		if (listOfCarRepair.isEmpty()) {
			throw new DataAccessException("No All carissue found in the database.") {
			};
		}
		return ResponseEntity.ok(listOfCarRepair);
	}

	// RAHUL
	@PutMapping("/edit-hub-profile")
	public ResponseEntity<String> updateHubProfile(
			@RequestParam(name = "profilePic", required = false) MultipartFile profileImgFile,
			@RequestParam(name = "signatuePic", required = false) MultipartFile signatureImgFile,
			@RequestParam(name = "passbookPic", required = false) MultipartFile passbookImgFile,
			@RequestParam("hubDataJson") String hubDataJson) throws Exception {

		String profileImageUrl = null;
		String signatureImageUrl = null;
		String passbookImageUrl = null;
		try {
			// Convert JSON to DriverDataDto object
			ObjectMapper objectMapper = new ObjectMapper();
			HubDataDto HubDataDto = objectMapper.readValue(hubDataJson, HubDataDto.class);

			// Check if profile image is provided
			if (profileImgFile != null && !profileImgFile.isEmpty()) {
				String profileImgFileName = UUID.randomUUID().toString() + "_" + profileImgFile.getOriginalFilename();
				File profileImgConvertedFile = convertMultiPartToFile(profileImgFile, profileImgFileName);
				profileImageUrl = uploadFileToS3(profileImgConvertedFile, profileImgFileName);
				System.out.println("Profile Image link=" + profileImageUrl);
			}

			// Check if signature image is provided
			if (signatureImgFile != null && !signatureImgFile.isEmpty()) {
				String signatureImgFileName = UUID.randomUUID().toString() + "_"
						+ signatureImgFile.getOriginalFilename();
				File signatureImgConvertedFile = convertMultiPartToFile(signatureImgFile, signatureImgFileName);
				signatureImageUrl = uploadFileToS3(signatureImgConvertedFile, signatureImgFileName);
				System.out.println("Signature Image link=" + signatureImageUrl);
			}

			// Check if passbook image is provided
			if (passbookImgFile != null && !passbookImgFile.isEmpty()) {
				String passbookImgFileName = UUID.randomUUID().toString() + "_" + passbookImgFile.getOriginalFilename();
				File passbookImgConvertedFile = convertMultiPartToFile(passbookImgFile, passbookImgFileName);
				passbookImageUrl = uploadFileToS3(passbookImgConvertedFile, passbookImgFileName);
				System.out.println("Passbook Image link=" + passbookImageUrl);
			}

			// Call service method to update hub profile
			hubService.updateHubProfile(HubDataDto, profileImageUrl, signatureImageUrl, passbookImageUrl);
			return ResponseEntity.ok("HubData uploaded successfully");
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

	// Rahul
	@PutMapping("/send-otp-forgate-password/{email}")
	public ResponseEntity<String> setPassByEmail(@PathVariable String email,
			@RequestBody PasswordChangeRequestDto passwordRequest) {
		try {
			// Validate email and passwordRequest
			if (email == null || email.isEmpty() || passwordRequest == null) {
				return ResponseEntity.badRequest().body("Invalid email or password request.");
			}

			// Call the service method to set password by email
			String details = hubService.setPasswordByEmail(email, passwordRequest);

			return ResponseEntity.status(HttpStatus.CREATED).body(details);
		} catch (Exception e) {
			e.printStackTrace(); // Handle the exception appropriately (e.g., log it)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing the request.");
		}
	}

	// Rahul
	@PostMapping("/verify-email-hub")
	public ResponseEntity<String> verifyOtpByEmailForForgatePassword(@RequestBody VerifyOtpRequest verifyOtpRequest) {
		boolean isVerified = hubService.verifyEmailOtp(verifyOtpRequest.getAdminId(), verifyOtpRequest.getOtp());

		if (isVerified) {
			return ResponseEntity.status(HttpStatus.OK).body("OTP verified successfully");

		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");

		}
	}

	// Rahul
	@GetMapping("/edit-hub-profile/{hubId}")
	public ResponseEntity<HubMangerProfileEditDto> getHubMangerProfile(@PathVariable Long hubId)
			throws HubNotFoundException {
		HubMangerProfileEditDto getProfile = hubService.getHubMangerProfile(hubId);
		if (getProfile != null) {

			return ResponseEntity.status(HttpStatus.OK).body(getProfile);
		} else {
			throw new HubNotFoundException("Hub Id Is not found !");
		}
	}

	// Rahul
	@GetMapping("/list-of-hub-employee/{hubId}")
	public ResponseEntity<List<GetEmployeeListDto>> getHubEmployeeList(@PathVariable Long hubId)
			throws DataAccessException {

		List<GetEmployeeListDto> listofemp = hubService.getHubEmployeeList(hubId);
		if (listofemp != null) {
			return ResponseEntity.status(HttpStatus.OK).body(listofemp);
		} else {
			throw new DataAccessException("list of employee is Employee is empty") {
			};
		}
	}

	@GetMapping("/get-all-vehicle-of-hub/{hubId}")
	public ResponseEntity<?> getListOfVehicle(@PathVariable Long hubId) {

		List<Vehicle> listOfVehicles = hubService.getListOfHubsVehicle(hubId);
		if (listOfVehicles.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no Vehicle present");
		} else {

			return ResponseEntity.status(HttpStatus.OK).body(listOfVehicles);
		}
	}

	@GetMapping("/get-vehicle-by-id/{hubId}/{vehicleId}")
	public ResponseEntity<?> getVehicleById(@PathVariable Long hubId, @PathVariable Long vehicleId) {

		Vehicle exitingVehicle = hubService.getVehicleOfHubByVehicleId(hubId, vehicleId);

		if (exitingVehicle != null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("There is no Vehicle present with this id" + vehicleId);
		} else {

			return ResponseEntity.status(HttpStatus.OK).body(exitingVehicle);
		}

	}

	@PostMapping("/approve-driver/{driverId}")
	public ResponseEntity<Driver> approveDriver(@PathVariable Long driverId) {
		return ResponseEntity.status(HttpStatus.OK).body(hubService.approveDriver(driverId));
	}

	@PutMapping("/change-byOldPassword/{hubId}")
	public ResponseEntity<?> setPasswordByOldPassword(@PathVariable Long hubId,
			@RequestBody ChangePasswordRequestDto changepassword) {

		try {
			boolean passwordChanged = hubService.changePasswordByOldPassword(hubId, changepassword);
			if (passwordChanged) {
				return ResponseEntity.ok("Password changed successfully.");
			} else {
				return ResponseEntity.badRequest().body("Old password is incorrect.");
			}
		} catch (HubNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Jyoti
	// profile
	@GetMapping("/hub-profile-by-email")
	public ResponseEntity<ProfileDto> getAdminProfileByEmail(@RequestParam String email) {

		ProfileDto profileDto = hubService.getProfileByEmail(email);
		if (profileDto == null) {
			throw new DataAccessException("email not found") {
			};
		}
		return ResponseEntity.ok(profileDto);

	}

	@GetMapping("/get-all/carchange/{hubId}/driverlist")
	public ResponseEntity<Object> getAllChangeReasonsByHubId(@PathVariable Long hubId) {
		try {
			List<CarRepairDto> listofDrivercarchange = driverService.findDriverListsWithChangeReasonsByHubId(hubId);
			;
			return ResponseEntity.ok(listofDrivercarchange);
		} catch (BusinessException ex) {
			ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}

	}

	@GetMapping("/get-all/returncar/{hubId}/driverlist")
	public ResponseEntity<Object> getAllReturnCarByHubId(@PathVariable Long hubId) {
		try {
			List<CarRepairDto> listOfDriverCarReturn = driverService.findDriverListwithReturnConditionByHubId(hubId);
			return ResponseEntity.ok(listOfDriverCarReturn);
		} catch (BusinessException ex) {
			ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

//Rishi refcator -jyoti
//	@PostMapping("/carRepairDetailCost/{carRepairId}")
//	public ResponseEntity<String> carRepairDetailCostSend(@PathVariable Long carRepairId,
//			@RequestParam(name = "invoice") MultipartFile file, @RequestParam BigDecimal totalCostOfRepairing) {
//		try {
//			String invoiceImg = vehicleService.uploadFile(file);
//
//			String carRepairDetailCost = hubService.carRepairDetailCostSend(carRepairId, invoiceImg,
//					totalCostOfRepairing);
//			return ResponseEntity.ok(carRepairDetailCost);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("500 : INTERNAL SERVER ERROR");
//		}
//	}

	@PostMapping("/carRepairDetailCost/{carRepairId}")
	public ResponseEntity<String> carRepairDetailCostSend(@PathVariable Long carRepairId,
	        @RequestParam(name = "invoice") MultipartFile file, @RequestParam BigDecimal totalCostOfRepairing) {
	    try {
	        String invoiceImg = vehicleService.uploadFile(file);

	        String result = hubService.carRepairDetailCostSend(carRepairId, invoiceImg, totalCostOfRepairing);
	        
	        if ("Repair Car Detail Sent Successfully".equals(result)) {
	            return ResponseEntity.ok("Repair Car Detail Sent Successfully");
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Failed to send car repair details. Please try again.");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("500 : INTERNAL SERVER ERROR");
	    }
	}
	
//Rishi
	@GetMapping("/carRepairDetailCostHistory")
	public ResponseEntity<List<CarRepairDetailCost>> getCarRepairDetailCostHistory() {
		List<CarRepairDetailCost> carRepairDetailCosts = hubService.getAllCarRepairDetailCosts();
		if (carRepairDetailCosts.isEmpty()) {
			throw new BusinessException("601", "Car Repair Detail Cost History is Empty");
		}
		return ResponseEntity.ok(carRepairDetailCosts);
	}

//Rishi - refactor jyoti
	@PostMapping("/carRepairApproval/{carRepairId}")
	public CarRepair carRepairApproval(@PathVariable Long carRepairId,
			@RequestParam(name = "damageCarImgs", required = false) List<MultipartFile> files,
			@RequestParam(name = "damageCarVideo", required = false) MultipartFile video,
			@RequestParam(name = "hubMessage", required = false) String hubMessage) throws IOException {

		// Upload image files and collect their URLs
		List<String> damageCarImgs = files != null ? files.stream().map(file -> {
			try {
				return vehicleService.uploadFile(file);
			} catch (IOException e) {
				throw new RuntimeException("Failed to upload file", e);
			}
		}).collect(Collectors.toList()) : List.of();

		// Upload video file if provided
		String damageCarVideo = video != null ? vehicleService.uploadFile(video) : null;

		// Call the service method with the collected data
		CarRepair carRepairApproval = hubService.carRepairApproval(carRepairId, damageCarImgs, damageCarVideo,
				hubMessage);
		return carRepairApproval;
	}
//Rishi
	@GetMapping("/totalCostOfRepairingForCurrentMonth")
	public ResponseEntity<BigDecimal> getTotalCostOfRepairingForCurrentMonth() {
		BigDecimal totalCost = hubService.getTotalCostOfRepairingForCurrentMonth();
		return ResponseEntity.ok(totalCost);
	}

//Rishi
	@GetMapping("/totalCostOfRepairingForCurrentMonth/{hubId}")
	public ResponseEntity<BigDecimal> getTotalCostOfRepairingForCurrentMonthByHub(@PathVariable Long hubId) {
		BigDecimal totalCost = hubService.getTotalCostOfRepairingForCurrentMonthByHub(hubId);
		return ResponseEntity.ok(totalCost);
	}

	@GetMapping("/get-by-hub-phoneno/{phoneno}")
	public ResponseEntity<ResponseLogin> getUserByPhoneNo(@PathVariable String phoneno) {
		ResponseLogin response = hubService.getByPhoneno(phoneno);
		if (response != null) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/{driverId}/completedRides") // not completed
	public int getTotalCompletedRidesForDriver(@PathVariable Long driverId,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		return bookingService.getTotalCompletedRidesForDriverOnDate(driverId, date);
	}

	@GetMapping("/comparisonChart")
	public ResponseEntity<BookingComparisonDTO> getBookingComparisonChart() {
		BookingComparisonDTO comparisonDTO = bookingService.getBookingComparisonChart();
		return ResponseEntity.ok(comparisonDTO);
	}

	@GetMapping("/maintenanceCarList")
	public ResponseEntity<Object> getAllCarRepairs() {
		try {
			List<CarRepairDto> carRepairs = driverService.getAllCarRepairs();
			return ResponseEntity.ok(carRepairs);
		} catch (BusinessException ex) {
			ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	// RAHUL
	@GetMapping("booking/{hubid}")
	public ResponseEntity<List<TodayBookingDashboardDto>> getBookingById(@PathVariable Long hubid)
			throws DataAccessException {
		List<TodayBookingDashboardDto> bookingDto = hubService.getTodayBookingDashboard(hubid);
		if (bookingDto != null) {
			return ResponseEntity.ok().body(bookingDto);
		} else {
			throw new DataAccessException(" There are no Booking ") {
			};
		}
	}

	// RAHUL
	@GetMapping("payment-history/{hubId}")
	public ResponseEntity<List<PaymentHistoryDto>> getEmployeePayementHistoryByHubId(@PathVariable Long hubId)
			throws DataAccessException {

		List<PaymentHistoryDto> EmpPayment = hubService.getEmployeePayementHistoryByHubId(hubId);
		if (EmpPayment != null) {
			return ResponseEntity.ok(EmpPayment);
		} else {
			throw new DataAccessException("There are no Payment list") {
			};
		}

	}

//Abhilasha
	@PostMapping("/createPayment/{hubId}/{driverId}")
	public ResponseEntity<String> createPayment1(@PathVariable Long hubId, @PathVariable Long driverId,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		try {

			double totalCompletedRides = bookingService.getTotalCompletedRidesForDriverOnDate(driverId, date);
			System.out.println("totalCompletedRides " + totalCompletedRides);

			String result = driverPaymentDetailService.createPaymentForHub(hubId, driverId, totalCompletedRides, date);

			if (result != null) {
				return ResponseEntity.badRequest().body(result);
			}
			return ResponseEntity.ok("Payment created successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error occurred while processing the request");
		}

	}

	// jyoti
	@PostMapping("/create-Employee-Paymentt/{hubEmployeeId}")
	public ResponseEntity<String> createEmployeePayment(@PathVariable Long hubEmployeeId, @RequestParam String amount,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		String result = hubEmployeeService.createEmployee2Payment(hubEmployeeId, amount, date);
		if (result == null) {
			return ResponseEntity.ok("Payment created successfully");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("payement faild ");// .body(result);
		}
	}

	@GetMapping("/Activedriverlist")
	public ResponseEntity<Object> getOngoingDriversOnDate() {
		try {
			List<ActiveDriverInfoDto> ongoingDrivers = bookingService.getOngoingDriversOnDate();
			return ResponseEntity.ok(ongoingDrivers);
		} catch (BusinessException ex) {
			ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		}
	}

	@GetMapping("/change-carlist")
	public ResponseEntity<Object> getIncompleteRides(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		try {
			List<IncompleteRideDto> incompleteRides = bookingService.getIncompleteRidesByDate(date);
			return ResponseEntity.ok(incompleteRides);
		} catch (BusinessException ex) {
			ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		}
	}

//Ram	
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
			double distance = calculateDistance(userLatitude, userLongitude, hubLocation.getHubLatitude(),
					hubLocation.getHubLongitude());
			if (distance < minDistance) {
				minDistance = distance;
				nearestHub = hub;
			}
		}

		// Book the car and get nearby drivers from the nearest hub
		HubLocation nearestHubLocation = hubLocationRepository.findByHub(nearestHub);
//			Hub nearestHub = null;
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

	// Abhilasha
	@GetMapping("/{hubId}/driverPaymentDetails")
	public ResponseEntity<Object> getDriverPaymentDetailsByHubId(@PathVariable Long hubId) {
		try {
			List<DriverPaymentDetailDto> driverPaymentDetailDtos = driverPaymentDetailService
					.getAllDriverPaymentDetailsByHubId(hubId);
			return ResponseEntity.ok(driverPaymentDetailDtos);
		} catch (BusinessException ex) {
			ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	// Abhilasha
	@GetMapping("/driverPaymentProfile/{driverPaymentDetailId}")
	public ResponseEntity<Object> getDriverProfilePaymentDetails(@PathVariable Long driverPaymentDetailId) {
		try {
			DriverPaymentDetail profileDetail = driverPaymentDetailService
					.getDriverPaymentDetailById(driverPaymentDetailId);
			if (profileDetail != null) {
				return ResponseEntity.ok(profileDetail);
			} else {
				throw new BusinessException("601", "Driver payment detail not found with ID: " + driverPaymentDetailId);
			}
		} catch (BusinessException ex) {
			ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	@GetMapping("/{driverId}/completedRidesofweek")
	public int getTotalCompletedRidesForDriverWithinLast7Days(@PathVariable Long driverId,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		// Calculate the start date by subtracting 7 days from the given date
		LocalDate startDate = date.minus(7, ChronoUnit.DAYS);

		// Call the service method to get the total completed rides within the date
		// range
		return bookingService.getTotalCompletedRidesForDriverWithinDateRanges(driverId, startDate, date);
	}

	@PostMapping("/driverPayment-weekly/{driverId}")
	public ResponseEntity<Map<String, Object>> calculateDriverPayment(@PathVariable Long driverId,
			@RequestParam LocalDate date) {

		Map<String, Object> response = driverPaymentDetailService.calculateDriverPayment(driverId, date);
		return ResponseEntity.ok(response);
	}

	// Rishi
//	@GetMapping("/getRentalBookingList")
//	public List<RentalBooking> getRentalBookingList() {
//		return rentalBookingRepository.findAll();
//	}

	@GetMapping("/getRentalBookingList")
	public List<RentalBookingDto> getAllRentalBookings() {
		return bookingService.getAllRentalBookingsList();
	}

//	Courier hub side implements
	// JALESHWARI
	@GetMapping("/getCourierPendingListForFour/{hubId}")
	public ResponseEntity<ApiResponse<List<DriverUpdateRequestDto>>> getCourierPendingListForFour(
			@PathVariable Long hubId) {
		List<DriverUpdateRequestDto> pendingCourierList = courierService.getAllPendingCourierListForFour(hubId);

		if (!pendingCourierList.isEmpty()) {
			ApiResponse<List<DriverUpdateRequestDto>> response = new ApiResponse<>(pendingCourierList, HttpStatus.OK,
					true, "Courier Driver Request Pending");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<List<DriverUpdateRequestDto>> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"No pending courier driver requests found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// JALESHWARI
	@GetMapping("/getCourierApprovedListForFour/{hubId}")
	public ResponseEntity<ApiResponse<List<DriverUpdateRequestDto>>> getCourierApprovedListForFour(
			@PathVariable Long hubId) {
		List<DriverUpdateRequestDto> approvedCourierList = courierService.getAllApprovedCourierListForFour(hubId);

		if (!approvedCourierList.isEmpty()) {
			ApiResponse<List<DriverUpdateRequestDto>> response = new ApiResponse<>(approvedCourierList, HttpStatus.OK,
					true, "Courier Driver List");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<List<DriverUpdateRequestDto>> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"No approved courier drivers available");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Rishi
	@GetMapping("/getCourierPendingListForTwo/{hubId}")
	public ResponseEntity<ApiResponse<List<DriverUpdateRequestDto>>> getCourierPendingListForTwo(
			@PathVariable Long hubId) {
		List<DriverUpdateRequestDto> pendingCourierList = courierService.getAllPendingCourierListForTwo(hubId);

		if (!pendingCourierList.isEmpty()) {
			ApiResponse<List<DriverUpdateRequestDto>> response = new ApiResponse<>(pendingCourierList, HttpStatus.OK,
					true, "Courier Driver Request Pending");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<List<DriverUpdateRequestDto>> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"No pending courier driver requests found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Rishi
	@GetMapping("/getCourierApprovedListForTwo/{hubId}")
	public ResponseEntity<ApiResponse<List<DriverUpdateRequestDto>>> getCourierApprovedListForTwo(
			@PathVariable Long hubId) {
		List<DriverUpdateRequestDto> approvedCourierList = courierService.getAllApprovedCourierListForTwo(hubId);
		if (!approvedCourierList.isEmpty()) {
			ApiResponse<List<DriverUpdateRequestDto>> response = new ApiResponse<>(approvedCourierList, HttpStatus.OK,
					true, "Courier Driver List");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<List<DriverUpdateRequestDto>> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"No approved courier drivers available");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Rishi
	@GetMapping("/getAllAvailableApprovedCourierDriverList/{hubId}")
	public ResponseEntity<ApiResponse<List<DriverUpdateRequestDto>>> getAllApprovedCourierDriverList(
			@PathVariable Long hubId) {
		List<DriverUpdateRequestDto> approvedCourierList = courierService.getAllApprovedCourierDriverList(hubId);
		if (!approvedCourierList.isEmpty()) {
			ApiResponse<List<DriverUpdateRequestDto>> response = new ApiResponse<>(approvedCourierList, HttpStatus.OK,
					true, "All Courier Driver List");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<List<DriverUpdateRequestDto>> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"No Approved courier drivers available");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// JALESHWARI
	@GetMapping("/getprofile/{hubId}/detailsbyid/{courierId}")
	public ResponseEntity<ApiResponse<CourierDetailsDto>> getCourierProfileDetailsById(@PathVariable Long hubId,
			@PathVariable Long courierId) {
		CourierDetailsDto courierDetailsDto = courierService.getCourierProfileDetailsById(hubId, courierId);
		if (courierDetailsDto != null) {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(courierDetailsDto, HttpStatus.OK, true,
					"Courier details retrieved successfully");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"Courier not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// JALESHWARI
	@GetMapping("/getProfilePendingForFour/{hubId}/detailsById/{courierId}")
	public ResponseEntity<ApiResponse<CourierDetailsDto>> getCourierProfilePendingDetailsByIdForFour(
			@PathVariable Long hubId, @PathVariable Long courierId) {
		CourierDetailsDto courierDetailsDto = courierService.getCourierProfilePendingDetailsByIdForFour(hubId,
				courierId);
		if (courierDetailsDto != null) {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(courierDetailsDto, HttpStatus.OK, true,
					"Courier details retrieved successfully");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"Courier not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Rishi
	@GetMapping("/getProfileApprovedForFour/{hubId}/detailsById/{courierId}")
	public ResponseEntity<ApiResponse<CourierDetailsDto>> getCourierProfileApprovedDetailsByIdForFour(
			@PathVariable Long hubId, @PathVariable Long courierId) {
		CourierDetailsDto courierDetailsDto = courierService.getCourierProfileApprovedDetailsByIdForFour(hubId,
				courierId);
		if (courierDetailsDto != null) {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(courierDetailsDto, HttpStatus.OK, true,
					"Courier details retrieved successfully");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"Courier not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Rishi
	@GetMapping("/getProfilePendingForTwo/{hubId}/detailsById/{courierId}")
	public ResponseEntity<ApiResponse<CourierDetailsDto>> getCourierProfilePendingDetailsByIdForTwo(
			@PathVariable Long hubId, @PathVariable Long courierId) {
		CourierDetailsDto courierDetailsDto = courierService.getCourierProfilePendingDetailsByIdForTwo(hubId,
				courierId);
		if (courierDetailsDto != null) {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(courierDetailsDto, HttpStatus.OK, true,
					"Courier details retrieved successfully");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"Courier not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Rishi
	@GetMapping("/getProfileApprovedForTwo/{hubId}/detailsById/{courierId}")
	public ResponseEntity<ApiResponse<CourierDetailsDto>> getCourierProfileApprovedDetailsByIdForTwo(
			@PathVariable Long hubId, @PathVariable Long courierId) {
		CourierDetailsDto courierDetailsDto = courierService.getCourierProfileApprovedDetailsByIdForTwo(hubId,
				courierId);
		if (courierDetailsDto != null) {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(courierDetailsDto, HttpStatus.OK, true,
					"Courier details retrieved successfully");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"Courier not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Rishi
	@GetMapping("/getAvailableCourierDriverProfileDetailsById/{hubId}/{courierId}")
	public ResponseEntity<ApiResponse<CourierDetailsDto>> getAvailableCourierDriverProfileDetailsById(
			@PathVariable Long hubId, @PathVariable Long courierId) {
		CourierDetailsDto courierDetailsDto = courierService.getAvailableCourierDriverProfileDetailsById(hubId,
				courierId);
		if (courierDetailsDto != null) {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(courierDetailsDto, HttpStatus.OK, true,
					"Courier details retrieved successfully");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"Courier not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Rishi
	@GetMapping("/getCourierEbikeList/{hubId}")
	public List<CourierEbikeDto> getCourierEbikeList(@PathVariable Long hubId) {
		return courierService.getCourierEbikeListByHubId(hubId);
	}

	// Rishi
	@GetMapping("/getCourierEbikeById/{hubId}/{courierEbikeId}")
	public ResponseEntity<CourierEbikeDto> getCourierEbikeById(@PathVariable Long hubId,
			@PathVariable Long courierEbikeId) {
		CourierEbikeDto courierEbikeDto = courierService.getCourierEbikeByIdAndHubId(courierEbikeId, hubId);
		if (courierEbikeDto != null) {
			return ResponseEntity.ok(courierEbikeDto);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// JALESHWARI
	@PostMapping("/courierdrivere/{hubId}/approved/{courierId}")
	public ResponseEntity<ApiResponse<CourierDetailsDto>> courierDriverApprove(@PathVariable Long hubId,
			@PathVariable Long courierId) {
		CourierDetailsDto courierDetailsDto = courierService.courierDriverApprove(hubId, courierId);
		if (courierDetailsDto != null) {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(courierDetailsDto, HttpStatus.OK, true,
					"Courier driver  successfully approved..!!");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"Courier driver not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// JALESHWARI
	@PostMapping("/courierdrivere/{hubId}/reject/{courierId}")
	public ResponseEntity<ApiResponse<CourierDetailsDto>> courierDriverReject(@PathVariable Long hubId,
			@PathVariable Long courierId) {
		CourierDetailsDto courierDetailsDto = courierService.courierDriverReject(hubId, courierId);
		if (courierDetailsDto != null) {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(courierDetailsDto, HttpStatus.OK, true,
					"Your courier registration has been rejected. Please contact support for further assistance.");
			return ResponseEntity.ok(response);
		} else {
			ApiResponse<CourierDetailsDto> response = new ApiResponse<>(null, HttpStatus.NOT_FOUND, false,
					"Courier driver  not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

//Rishi
	@PostMapping("/createPromoCode")
	public ResponseEntity<PromoCode> createPromoCode(@RequestBody PromoCode promoCodeRequest) {
		PromoCode promoCode = new PromoCode();
		promoCode.setCode(promoCodeRequest.getCode());
		promoCode.setCodeDescription(promoCodeRequest.getCodeDescription());
		promoCode.setDiscountPercentage(promoCodeRequest.getDiscountPercentage());

		promoCodeRepository.save(promoCode);

		return ResponseEntity.ok(promoCode);
	}

	// RAHUL
	@GetMapping("/transport-list/{hubId}")
	public ResponseEntity<List<BookingDTO>> getCourierListDetails(@PathVariable Long hubId)
			throws UserNotFoundException {
		List<BookingDTO> bookingDetails = hubService.getCourierListDetails(hubId);

		if (bookingDetails != null) {
			return ResponseEntity.ok(bookingDetails);
		} else {
			throw new UserNotFoundException("hub not found with this id " + hubId);
		}
	}

	// RAHUL
	@GetMapping("/get-two-wheelar/{hubId}")
	public ResponseEntity<List<VehicleDataDto>> getTwoWheelarList(@PathVariable Long hubId)
			throws UserNotFoundException {
		List<VehicleDataDto> list = hubService.getTwoWheelarlist(hubId);
		if (list != null) {
			return ResponseEntity.ok(list);
		} else {
			throw new UserNotFoundException("hub not found with this id " + hubId);
		}
	}

	// RAHUL
	@GetMapping("/get-four-wheelar/{hubId}")
	public ResponseEntity<List<VehicleDataDto>> getFourWheelarList(@PathVariable Long hubId)
			throws UserNotFoundException {
		List<VehicleDataDto> list = hubService.getFourWheelarlist(hubId);
		if (list != null) {
			return ResponseEntity.ok(list);
		} else {
			throw new UserNotFoundException("hub not found with this id " + hubId);
		}
	}

	// Rishi
	@GetMapping("/getTwoWheelerDriverList")
	public List<Driver> getTwoWheelerDriverList() {
		return driverService.getTwoWheelerDriverList();
	}

	// Rishi
	@GetMapping("/getFourWheelerDriverList")
	public List<Driver> getFourWheelerDriverList() {
		return driverService.getFourWheelerDriverList();
	}

	// Rishi
	@PostMapping("/assignCarToDriver/{hubId}")
	public ReturnCar assignCarToDriver(@PathVariable Long hubId, @RequestBody AssignCarRequestDto request) {
		return hubService.assignCarToDriver(hubId, request);
	}

	@GetMapping("/get-rental-booking-details/{hubId}")
	public ResponseEntity<Object> getRentalBookingDetails(@PathVariable Long hubId) {

		try {
			var listOfBooking = hubService.getRentalBookingDetailsOfHub(hubId);
			ApiResponse<Object> response = new ApiResponse<Object>(listOfBooking,
					org.springframework.http.HttpStatus.OK, true, "Here is a list of rental booking details");
			return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(response);
		} catch (Exception ex) {
			ApiResponse<Object> response = new ApiResponse<Object>(null,
					org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, false, "There is something wrong");
			return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);

		}
	}

//	// Abhilasha
//	@PostMapping("/driverPayment/{driverId}/{hubId}")
//	public ResponseEntity<Map<String, Object>> calculateDriverPayment2(@PathVariable Long driverId,
//			@PathVariable Long hubId, @RequestParam LocalDate date) {
//
//		// Calculate start date 6 days back from the given date
//		LocalDate startDate1 = date.minusDays(7);
//
//		// Calculate end date which is the day before the given date
//		LocalDate endDate2 = date.minusDays(1);
//
//		LocalDateTime startDate = startDate1.atStartOfDay();
//		System.out.println("startDate=" + startDate);
//		LocalDateTime endDate = endDate2.atTime(LocalTime.MAX);
//		System.out.println("endDate=" + endDate);
//		// int totalCompletedRides=0;
//		int totalCompletedRides = bookingService.getTotalCompletedRidesForDriverinweek(driverId, startDate, endDate);
//		System.out.println("totalCompletedRides=" + totalCompletedRides);
//		int totalRentalPayment = bookingService.calculateTotalRentalPayment(driverId, startDate, endDate);
//		System.out.println("totalRentalPayment=" + totalRentalPayment);
//
//		List<ReturnCar> assignCars = returnCarRepository.findByDriverIdAndAssignTimeBetween(driverId, startDate,
//				endDate);
//
//		List<ReturnCar> returnCars = returnCarRepository.findByDriverIdAndReturnTimeBetween(driverId, startDate,
//				endDate);
//		System.out.println("returnCars=" + returnCars);
//
//		// Calculate the total working hours per day
//		Map<LocalDate, Integer> workingHoursMap = bookingService.calculateWorkingHours(assignCars, returnCars);
//		System.out.println("workingHoursMap=" + workingHoursMap);
//
//		DriverAndVehicleType driverType = bookingService.determineDriverType(driverId);
//
//		int totalPayment = bookingService.calculateTotalPayment(workingHoursMap, totalRentalPayment,
//				totalCompletedRides, driverType);
//		bookingService.saveTotalPayment(driverId, totalPayment, date);
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("dailyPayments", workingHoursMap);
//		response.put("totalPayment", totalPayment);
//
//		return ResponseEntity.ok(response);
//	}

	@GetMapping("/get-courier-booking-details/{hubId}")
	public ResponseEntity<Object> getCourierBookingDetails(@PathVariable Long hubId) {

		try {
			var listOfBooking = hubService.getCourierBookingDetailsOfHub(hubId);
			ApiResponse<Object> response = new ApiResponse<Object>(listOfBooking,
					org.springframework.http.HttpStatus.OK, true, "Here is a list of rental booking details");
			return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(response);
		} catch (Exception ex) {
			ApiResponse<Object> response = new ApiResponse<Object>(null,
					org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, false, "There is something wrong");
			return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);

		}

	}

//Abhilasha
	@GetMapping("/list-all-courierEbike")
	public ResponseEntity<List<CourierEbike>> getAllEBikes() {
		try {
			List<CourierEbike> ebikes = vehicleService.getAllEBikes();
			return ResponseEntity.ok(ebikes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Abhilasha
	@GetMapping("/courierEbike/{CourierEbikeId}")
	public ResponseEntity<CourierEbike> getCourierEbikeById(@PathVariable Long CourierEbikeId) {
		try {
			CourierEbike ebike = vehicleService.getEBikeById(CourierEbikeId);
			if (ebike != null) {
				return ResponseEntity.ok(ebike);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// jyoti
	@GetMapping("/getAll_courierList_and_eBikeCourierList_fourWheelerCourierList/{hubId}")
	public List<TransportAllListDto> getTransportListByHubId(@PathVariable Long hubId,
			@RequestParam(required = false) DriverAndVehicleType vehicleType) {
		if (vehicleType == null) {
			// If vehicle type is not specified, default to returning all types
			return courierService.getTransportListByHubId(hubId);
		} else {
			List<TransportAllListDto> transportList = courierService.getTransportListByHubIdAndVehicleType(hubId,
					vehicleType);
			// Check if there are no bookings for the specified vehicle type
			if (transportList.isEmpty()) {

				throw new NotFoundException("No bookings found for the specified vehicle type: " + vehicleType);
			} else {
				return transportList;
			}
		}
	}

//Abhilasha
	@PostMapping("/ebikedriverPayment/{courierId}/{hubId}")
	public ResponseEntity<Map<String, Object>> calculateEbikeDriverPayment2(@PathVariable Long courierId,
			@PathVariable Long hubId, @RequestParam LocalDate date) {
		// Calculate start date 6 days back from the given date
		LocalDate startDate1 = date.minusDays(7);

		// Calculate end date which is the day before the given date
		LocalDate endDate2 = date.minusDays(1);

		LocalDateTime startDate = startDate1.atStartOfDay();
		System.out.println("startDate=" + startDate);
		LocalDateTime endDate = endDate2.atTime(LocalTime.MAX);
		System.out.println("endDate=" + endDate);

		List<ReturnCourierVehicle> assignCars = returncouriervechileRepo.findByCourierIdAndAssignTimeBetween(courierId,
				startDate, endDate);

		List<ReturnCourierVehicle> returnCars = returncouriervechileRepo.findByCourierIdAndReturnTimeBetween(courierId,
				startDate, endDate);
		System.out.println("returnCars=" + returnCars);

		// Calculate the total working hours per day
		Map<LocalDate, Integer> workingHoursMap = courierService.calculateEbikeWorkingHours(assignCars, returnCars);
		System.out.println("workingHoursMap=" + workingHoursMap);

		int totalPayment = courierService.calculateTotalPayment(workingHoursMap);

		courierService.saveTotalPayment(courierId, totalPayment, date);

		Map<String, Object> response = new HashMap<>();
		response.put("dailyPayments", workingHoursMap);
		response.put("totalPayment", totalPayment);

		return ResponseEntity.ok(response);

	}

//Abhilasha
	@PutMapping("/edit-courier-ebike/{ebikeId}")
	public ResponseEntity<String> editCourierEbike(@PathVariable Long ebikeId,

			@RequestParam(name = "insuranceNo", required = false) String insuranceNo,
			@RequestParam(name = "pricePerKm", required = false) BigDecimal pricePerKm,
			@RequestParam(name = "ebikeImage", required = false) MultipartFile ebikeImage) {
		try {
			// Find the eBike by its ID
			CourierEbike ebike = courierEbikeRepo.findById(ebikeId).orElseThrow();

			System.out.println("ebike=" + ebike);

			// Update insurance number if provided
			if (insuranceNo != null) {
				ebike.setInsuranceNo(insuranceNo);
			}

			// Update price per km if provided
			if (pricePerKm != null) {
				ebike.setPricePerKm(pricePerKm);
			}

			// Update eBike image if provided
			if (ebikeImage != null) {
				String s3Url = vehicleService.uploadFile(ebikeImage);
				ebike.setEbikeImage(s3Url);
			}

			// Save the updated eBike
			courierEbikeRepo.save(ebike);

			return ResponseEntity.ok("Courier eBike updated successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating Courier eBike.");
		}

	}
////Abhilasha
//	@GetMapping("/driverpayments/{hubId}")
//	public List<DriverPaymentDetail> getPaymentsByHubAndDate(@PathVariable("hubId") Long hubId,
//			@RequestParam("date") LocalDate date) {
//		return driverPaymentDetailService.getPaymentsByHubAndDate(hubId, date);
//	}
////Abhilasha
//	@GetMapping("/driver-payment-pending-list")
//	public List<DriverPaymentDetail> getAllPendingPayments() {
//		return driverPaymentDetailService.getPendingPayments();
//	}
////Abhilasha
//	@GetMapping("/pending-driver/{driverPaymentDetailId}")
//	public ResponseEntity<DriverPaymentDetail> getPendingDriverPaymentDetailById(
//			@PathVariable Long driverPaymentDetailId) {
//		Optional<DriverPaymentDetail> driverPaymentDetail = driverPaymentDetailService
//				.getPendingDriverPaymentDetailById(driverPaymentDetailId);
//		if (driverPaymentDetail.isPresent()) {
//			return ResponseEntity.ok(driverPaymentDetail.get());
//		} else {
//			return ResponseEntity.notFound().build();
//		}
//	}

	@PostMapping("/courierdriver/assign/vehicle/{courierEbikeId}/{courierId}/{hubId}")
	public ResponseEntity<Object> assignBikeToCourier(@PathVariable Long courierEbikeId, @PathVariable Long courierId,
			@PathVariable Long hubId, @RequestParam String bikeCondition) {
		try {
			var data = courierService.assignBikeToCourier(courierEbikeId, courierId, bikeCondition, hubId);
			ApiResponse<Object> response = new ApiResponse<Object>(data, HttpStatus.OK, true,
					"Car assign succesfully to the driver");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (ResourceNotFoundException e) {

			ApiResponse<Object> response = new ApiResponse<Object>(null, HttpStatus.NOT_FOUND, false, e.getMessage());
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/get-all-courier-drivers")
	public ResponseEntity<Object> getAllCourierDrivers() {
		try {
			List<Courier> courieres = courierService.findCourierForAssignBike();
			ApiResponse<List<Courier>> response = new ApiResponse<List<Courier>>(courieres, HttpStatus.OK, true,
					"This is a courier driver list");
			return new ResponseEntity<Object>(response, HttpStatus.OK);

		} catch (ResourceNotFoundException e) {

			ApiResponse<List<Courier>> response = new ApiResponse<List<Courier>>(null, HttpStatus.NOT_FOUND, false,
					"This is a courier driver list");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/get-all-courier-vehicles")
	public ResponseEntity<Object> getAllCourierVehicles() {
		try {
			List<CourierEbike> courierEbikeVehicles = courierEbikeRepo.findAll();
			List<CourierEbike> courierEbikeAvailableVehicles = courierEbikeVehicles.stream()
					.filter(bike -> bike.getVehicleStatus() == VehicleStatus.AVAILABLE).collect(Collectors.toList());
			ApiResponse<List<CourierEbike>> response = new ApiResponse<List<CourierEbike>>(
					courierEbikeAvailableVehicles, HttpStatus.OK, true, "This is a courier ebike vehicles list");
			return new ResponseEntity<Object>(response, HttpStatus.OK);

		} catch (ResourceNotFoundException e) {

			ApiResponse<List<Courier>> response = new ApiResponse<List<Courier>>(null, HttpStatus.NOT_FOUND, false,
					"This is a courier ebike  vehicles list");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}
	}

	// Abhilasha New Driverpayment
//	@PostMapping("/driverPayments/{hubId}")
//    public ResponseEntity<Map<Long, Map<String, Object>>> calculateDriverPayments(@PathVariable Long hubId, @RequestParam LocalDate date) {
//        
//        // Fetch all drivers for the given hubId
//        List<Driver> drivers = driverRepository.findByHubHubId(hubId);
//        System.out.println("drivers="+drivers);
//        
//        // Calculate start date 6 days back from the given date
//        LocalDate startDate1 = date.minusDays(7);
//
//        // Calculate end date which is the day before the given date
//        LocalDate endDate2 = date.minusDays(1);
//
//        LocalDateTime startDate = startDate1.atStartOfDay();
//        System.out.println("startDate=" + startDate);
//        LocalDateTime endDate = endDate2.atTime(LocalTime.MAX);
//        System.out.println("endDate=" + endDate);
//
//        Map<Long, Map<String, Object>> driverPayments = new HashMap<>();
//
//        for (Driver driver : drivers) {
//            Long driverId = driver.getDriverId();
//
//            int totalCompletedRides1 = bookingService.getTotalCompletedRidesForDriverinweek(driverId, startDate, endDate);
//            System.out.println("totalCompletedRides1=" + totalCompletedRides1);
//            int totalCompletedRides2 = bookingService.getTotalCompletedrentalRidesForDriverinweek(driverId, startDate, endDate);
//            System.out.println("totalCompletedRides2=" + totalCompletedRides2);
//            int totalride = totalCompletedRides1 + totalCompletedRides2;
//            System.out.println("totalride=" + totalride);
//            System.out.println("totalCompletedRides=" + totalCompletedRides1);
//
//            List<ReturnCar> assignCars = returnCarRepository.findByDriverIdAndAssignTimeBetween(driverId, startDate, endDate);
//            List<ReturnCar> returnCars = returnCarRepository.findByDriverIdAndReturnTimeBetween(driverId, startDate, endDate);
//            System.out.println("returnCars=" + returnCars);
//
//            // Calculate the total working hours per day
//            Map<LocalDate, Integer> workingHoursMap = bookingService.calculateWorkingHours(assignCars, returnCars);
//            System.out.println("workingHoursMap=" + workingHoursMap);
//
//            DriverAndVehicleType driverType = bookingService.determineDriverType(driverId);
//            System.out.println("driverType=" + driverType);
//
//            int totalPayment = bookingService.calculateTotalPayment(workingHoursMap, totalride, driverType);
//            bookingService.saveTotalPayment(driverId, totalPayment, date);
//
//            Map<String, Object> driverResponse = new HashMap<>();
//            driverResponse.put("dailyPayments", workingHoursMap);
//            driverResponse.put("totalPayment", totalPayment);
//
//            driverPayments.put(driverId, driverResponse);
//        }
//
//        return ResponseEntity.ok(driverPayments);
//    }

	@GetMapping("/all-vehicles-list/{hubId}")
	public ResponseEntity<List<Vehicle>> getVehiclesByHubId(@PathVariable Long hubId) {
		List<Vehicle> vehicles = vehicleRepository.findByHub_HubId(hubId);
		return ResponseEntity.ok(vehicles);
	}

	// Abhilasha
	@GetMapping("/list-all-BookingGraph/{hubId}")
	public ResponseEntity<List<CourierBookingDto1>> getAllBookings(@PathVariable Long hubId) {
		List<CourierBookingDto1> bookings = bookingService.getBookingsByHubId(hubId);
		return ResponseEntity.ok(bookings);
	}

	// Abhilasha
	@GetMapping("/list-by-RentalGraph/{hubId}")
	public ResponseEntity<List<CourierBookingDto1>> getRentalBookingsByHubId(@PathVariable Long hubId) {
		List<CourierBookingDto1> rentalBookings = bookingService.getRentalBookingsByHubId(hubId);
		return ResponseEntity.ok(rentalBookings);
	}

}
