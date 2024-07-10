package com.rido.service.impl;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.rido.Exceptions.BusinessException;
import com.rido.config.MyWebSocketHandler;
import com.rido.config.RazorPayConfiguration;
import com.rido.config.TwilioConfig;
import com.rido.dto.ActiveDriverInfoDto;
import com.rido.dto.BookedOrderDTO;
import com.rido.dto.BookingComparisonDTO;
import com.rido.dto.BookingResponseDto;
import com.rido.dto.CourierBookingDto1;
import com.rido.dto.DriverInfoDTO;
import com.rido.dto.DriverNotificationDto;
import com.rido.dto.ExtendedRental;
import com.rido.dto.IncompleteRideDto;
import com.rido.dto.PaymentActivityResponseDto;
import com.rido.dto.RentalBookingDto;
import com.rido.dto.ScheduleRideRequest;
import com.rido.dto.UserNotificationDto;
import com.rido.entity.Booking;
import com.rido.entity.Driver;
import com.rido.entity.DriverPaymentDetail;
import com.rido.entity.Hub;
import com.rido.entity.PaymentActivity;
import com.rido.entity.PromoCode;
import com.rido.entity.RentalBooking;
import com.rido.entity.RentalUserLocation;
import com.rido.entity.ReturnCar;
import com.rido.entity.TimeDuration;
import com.rido.entity.User;
import com.rido.entity.UserLocation;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.CourierBookingStatus;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.RentalPackageType;
import com.rido.entity.enums.RideOrderStatus;
import com.rido.entity.enums.Status;
import com.rido.repository.BookingRepository;
import com.rido.repository.DriverPaymentDetailRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.PaymentRepository;
import com.rido.repository.PromoCodeRepository;
import com.rido.repository.RentalBookingRepository;
import com.rido.repository.RentalUserLocationRepository;
import com.rido.repository.TimeDurationRepository;
import com.rido.repository.UserLocationRepository;
import com.rido.repository.UserRepository;
import com.rido.repository.VehicleRepository;
import com.rido.service.BookingService;
import com.rido.service.HubPaymentService;
import com.rido.service.HubService;
import com.rido.service.PromoCodeService;
import com.rido.utils.CalculateDistance;

import jakarta.transaction.Transactional;

@Service
public class BookingServiceImpl implements BookingService {

//	private static final double EARTH_RADIUS_KM = 6371.0;
	private static final double GST_RATE = 0.5; // 18% GST rate

	private final Logger log = Logger.getLogger(MyWebSocketHandler.class.getName());
	private static final Logger logger = Logger.getLogger(BookingServiceImpl.class.getName());

	@Autowired
	private UserLocationRepository userLocationRepository;

	@Autowired
	private TimeDurationRepository timeDurationRepository;

	@Autowired
	private PromoCodeService promoCodeService;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	PaymentRepository paymentRepository;

	@Autowired
	private HubPaymentService hubPaymentService;

	@Autowired
	HubService hubService;

	@Autowired
	private RentalBookingRepository rentalBookingRepository;

	@Autowired
	private RentalUserLocationRepository rentalUserLocationRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private RazorPayConfiguration razorPayConfiguration;

	@Autowired
	private PromoCodeRepository promoCodeRepository;

	@Autowired
	private DriverPaymentDetailRepository driverPaymentDetailRepo;

	private CalculateDistance calculateDistance;

	@Autowired
	private TwilioConfig twilioConfig;

	@Autowired
	private LocationImpl locationImpl;

	@Autowired
	private MyWebSocketHandler myWebSocketHandler;

//    private final HttpSession httpSession;
//    
//    @Autowired
//    public BookingServiceImpl(HttpServletRequest request) {
//        this.httpSession = request.getSession();
//    }

	public Booking calculateBooking(Booking booking, Long userId, Long hubId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

		booking.setUser(user);

		Hub hub = hubRepository.findById(hubId)
				.orElseThrow(() -> new RuntimeException("Hub not found with ID: " + hubId));

		booking.setHub(hub);

		// Calculate rental charges
		double rentalCharges = calculateRentalCharges(booking);

		// Apply promo code if applicable
		if (booking.getPromoCode() != null && !booking.getPromoCode().isEmpty()) {
			double discount = promoCodeService.applyPromoCode(booking.getPromoCode(), rentalCharges);
			rentalCharges -= discount;
		}

		// Calculate GST
		double gst = calculateGST(rentalCharges);

		// Calculate refundable deposit
		double refundableDeposit = calculateRefundableDeposit(rentalCharges);

		// Calculate total amount using BigDecimal
		BigDecimal rentalChargesBD = BigDecimal.valueOf(rentalCharges);
		BigDecimal gstBD = BigDecimal.valueOf(gst);
		BigDecimal refundableDepositBD = BigDecimal.valueOf(refundableDeposit);
		BigDecimal totalAmount = rentalChargesBD.add(gstBD).add(refundableDepositBD);

		// Update booking object with calculated values
		booking.setRentalCharge(rentalCharges);
		booking.setGst(gst);
		booking.setRefundableDeposit(refundableDeposit);
		booking.setTotalAmount(totalAmount);

		// Save the drop off location
		UserLocation dropOffLocation = new UserLocation();
		dropOffLocation.setUserLatitude(booking.getDropOffLocation().getUserLatitude());
		dropOffLocation.setUserLongitude(booking.getDropOffLocation().getUserLongitude());
		dropOffLocation.setAddress(booking.getDropOffLocation().getAddress());
		userLocationRepository.save(dropOffLocation);

		// Set the saved drop off location in the booking object
		booking.setDropOffLocation(dropOffLocation);

		// Save the pickup location if it's not null
		if (booking.getPickupLocation() != null) {
			UserLocation pickupLocation = booking.getPickupLocation();
			userLocationRepository.save(pickupLocation);
		}

		// Create and save a TimeDuration object
		TimeDuration timeDuration = new TimeDuration();
		timeDuration.setStartDateTime(LocalDateTime.now());
		timeDuration.setEndDateTime(LocalDateTime.now().plusHours(2));

		// Set the TimeDuration object in the booking
		booking.setTimeDuration(timeDuration);

		// Save the TimeDuration object
		timeDurationRepository.save(timeDuration);

		// Save the booking object in the repository
		return bookingRepository.save(booking);
	}

	private double calculateRentalCharges(Booking booking) {
		// Implement logic to calculate rental charges based on time duration and
		// package type
		// For simplicity, let's assume a basic calculation
		double rentalCharges = 0.0;

		// Example: Calculate rental charges based on time duration and package type
		double ratePerHour = 10.0; // Default rate per hour for BASIC package

		long durationInHours = Duration
				.between(booking.getTimeDuration().getStartDateTime(), booking.getTimeDuration().getEndDateTime())
				.toHours();
		rentalCharges = durationInHours * ratePerHour;

		return rentalCharges;
	}

	private double calculateGST(double rentalCharges) {
		// Implement logic to calculate GST
		// For simplicity, let's assume a flat 18% GST rate
		return rentalCharges * 0.18;
	}

	private double calculateRefundableDeposit(double rentalCharges) {
		// Implement logic to calculate refundable deposit based on rental charges
		// For simplicity, let's assume it's a fixed percentage (e.g., 10%)
		return rentalCharges * 0.10; // 10% of rental charges
	}
//	@Override
//	public Booking calculateBooking(Booking booking, Long userId, Long hubId) {
//		User user = userRepository.findById(userId)
//				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
//
//		booking.setUser(user);
//
//		Hub hub = hubRepository.findById(hubId)
//				.orElseThrow(() -> new RuntimeException("Hub not found with ID: " + hubId));
//
//		booking.setHub(hub);
//
//		double rentalCharges = calculateRentalCharges(booking);
//
//		if (booking.getPromoCode() != null && !booking.getPromoCode().isEmpty()) {
//			double discount = promoCodeService.applyPromoCode(booking.getPromoCode(), rentalCharges);
//			rentalCharges -= discount;
//		}
//
//		double gst = calculateGST(rentalCharges);
//		double refundableDeposit = calculateRefundableDeposit(rentalCharges);
//		BigDecimal totalAmount = calculateTotalAmount(rentalCharges, gst, refundableDeposit);
//
//		booking.setRentalCharge(rentalCharges);
//		booking.setGst(gst);
//		booking.setRefundableDeposit(refundableDeposit);
//		booking.setTotalAmount(totalAmount);
//
//		UserLocation dropOffLocation = new UserLocation();
//		dropOffLocation.setUserLatitude(booking.getDropOffLocation().getUserLatitude());
//		dropOffLocation.setUserLongitude(booking.getDropOffLocation().getUserLongitude());
//		dropOffLocation.setAddress(booking.getDropOffLocation().getAddress());
//		locationRepository.save(dropOffLocation);
//		booking.setDropOffLocation(dropOffLocation);
//
//		if (booking.getPickupLocation() != null) {
//			UserLocation pickupLocation = booking.getPickupLocation();
//			locationRepository.save(pickupLocation);
//		}
//
////		Order order = new Order();
////		order.setUser(user);
////		order.setRideStatus("Booked");
//
//		TimeDuration timeDuration = new TimeDuration();
//		timeDuration.setStartDateTime(LocalDateTime.now());
//		timeDuration.setEndDateTime(LocalDateTime.now().plusHours(2));
//
//		booking.setTimeDuration(timeDuration);
////		order.setTimeDuration(timeDuration);
//		timeDurationRepository.save(timeDuration);
////		orderRepository.save(order);
//
//		return bookingRepository.save(booking);
//	}
//
//	private double calculateRentalCharges(Booking booking) {
//		double rentalCharges = 0.0;
//		double ratePerHour = 0.0;
//
//		switch (booking.getPackageType()) {
//		case BASIC:
//			ratePerHour = 10.0;
//			break;
//		case STANDARD:
//			ratePerHour = 15.0;
//			break;
//		case PREMIUM:
//			ratePerHour = 20.0;
//			break;
//		}
//
//		long durationInHours = Duration
//				.between(booking.getTimeDuration().getStartDateTime(), booking.getTimeDuration().getEndDateTime())
//				.toHours();
//		rentalCharges = durationInHours * ratePerHour;
//
//		return rentalCharges;
//	}
//
//	private double calculateGST(double rentalCharges) {
//		return rentalCharges * 0.18;
//	}
//
//	private double calculateRefundableDeposit(double rentalCharges) {
//		return rentalCharges * 0.10;
//	}
//
//	private BigDecimal calculateTotalAmount(double rentalCharges, double gst, double refundableDeposit) {
//		BigDecimal totalAmount = BigDecimal.valueOf(rentalCharges + gst + refundableDeposit);
//		return totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
//	}

	@Override
	public List<Booking> getUserPayments() {
		return bookingRepository.findAll();
	}

	@Override
	public List<Booking> getAllBookings(Long hubId) {
		return bookingRepository.findByHub_HubId(hubId);
	}

	@Override
	public List<Booking> getNewBookings(Long hubId) {
		return bookingRepository.findByHub_HubIdAndDriverIsNull(hubId);
	}

	@Override
	public List<Booking> getTodaysBookings(Long hubId) {
		// Get today's date
		LocalDate today = LocalDate.now();

		// Get bookings for today
		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime endOfDay = today.atStartOfDay().plusDays(1).minusSeconds(1);

		return bookingRepository.findByTimeDurationStartDateTimeBetween(startOfDay, endOfDay);
	}

//  Graph
	@Override
	public List<Booking> getWeeklyBookings() {
		LocalDateTime startOfWeek = LocalDateTime.now().with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
		LocalDateTime endOfWeek = startOfWeek.plusDays(6).with(LocalTime.MAX).truncatedTo(ChronoUnit.DAYS);
		return bookingRepository.findByTimeDurationStartDateTimeBetween(startOfWeek, endOfWeek);
	}

	@Override
	public Map<String, Integer> generateWeeklyBookingGraph() {
		Map<String, Integer> graphData = new HashMap<>();
		List<Booking> weeklyBookings = getWeeklyBookings();
		// Logic to generate graph data
		// For demonstration purposes, let's assume we're counting bookings for each day
		for (Booking booking : weeklyBookings) {
			String day = booking.getTimeDuration().getStartDateTime().getDayOfWeek().toString();
			graphData.put(day, graphData.getOrDefault(day, 0) + 1);
		}
		return graphData;
	}

	@Override
	public BigDecimal calculateWeeklyPayment() {
		List<Booking> weeklyBookings = getWeeklyBookings();
		BigDecimal totalPayment = BigDecimal.ZERO;

		// Iterate through each booking in the week
		for (Booking booking : weeklyBookings) {
			// Retrieve the payment amount for each booking
			BigDecimal bookingPayment = booking.getTotalAmount(); // Assuming there's a method to get the payment
																	// amount
			// Add the payment amount of each booking to the total payment
			totalPayment = totalPayment.add(bookingPayment);
		}

		return totalPayment;
	}

	@Override
	public Map<String, BigDecimal> generateMonthlyBookingGraph() {
		Map<String, BigDecimal> graphData = new HashMap<>();
		List<Booking> allBookings = bookingRepository.findAll();

		for (Booking booking : allBookings) {
			YearMonth yearMonth = YearMonth.from(booking.getTimeDuration().getStartDateTime());
			String monthKey = yearMonth.toString();

			BigDecimal totalAmount = graphData.getOrDefault(monthKey, BigDecimal.ZERO);
			totalAmount = totalAmount.add(booking.getTotalAmount());
			graphData.put(monthKey, totalAmount);
		}
		return graphData;
	}

	public Map<Integer, BigDecimal> generateYearlyBookingGraph() {
		Map<Integer, BigDecimal> graphData = new HashMap<>();
		List<Booking> allBookings = bookingRepository.findAll();

		for (Booking booking : allBookings) {
			Year year = Year.from(booking.getTimeDuration().getStartDateTime());
			int yearKey = year.getValue();

			BigDecimal totalAmount = graphData.getOrDefault(yearKey, BigDecimal.ZERO);
			totalAmount = totalAmount.add(booking.getTotalAmount());
			graphData.put(yearKey, totalAmount);
		}

		return graphData;
	}

	@Override
	public BigDecimal getTotalAmountForCurrentMonth() {
		return bookingRepository.getTotalAmountForCurrentMonth();
	}

	@Override
	public BigDecimal getTotalAmountForCurrentMonthByHub(Long hubId) {
		return bookingRepository.getTotalAmountForCurrentMonthByHub(hubId);
	}

	@Override
	public BigDecimal getTotalAmountForPreviousMonth() {
		return bookingRepository.getTotalAmountForPreviousMonth();
	}

//	@Override
//	public List<WeeklyBookingDTO> getWeeklyBookingDetails() {
//
//		List<WeeklyBookingDTO> weeklyBookings = new ArrayList<>();
//
//		for (int i = 1; i <= 4; i++) {
//			WeeklyBookingDTO weeklyBookingDTO = new WeeklyBookingDTO();
//			weeklyBookingDTO.setWeekNumber(i);
//			weeklyBookingDTO.setTotalBookings((int) (Math.random() * 100)); // Sample total bookings
//			weeklyBookings.add(weeklyBookingDTO);
//		}
//		return weeklyBookings;
//	}

	@Override
	public BookingComparisonDTO getBookingComparisonChart() {

		long totalBookings = bookingRepository.count();

		LocalDate previousDate = LocalDate.now().minusDays(1);
		long previousDailyBookings = bookingRepository.getPreviousDailyBookings(previousDate);

		LocalDate previousWeekStart = LocalDate.now().minusWeeks(1)
				.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate previousWeekEnd = previousWeekStart.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		int previousWeeklyBookings = bookingRepository.getPreviousWeeklyBookings(previousWeekStart);
		long previousMonthlyBookings = bookingRepository.getPreviousMonthlyBookings();

		long currentDailyBookings = bookingRepository.getCurrentDailyBookings();
		long currentWeeklyBookings = bookingRepository.getCurrentWeeklyBookings();
		long currentMonthlyBookings = bookingRepository.getCurrentMonthlyBookings();

		double dailyPercentageChange = calculatePercentageChange(currentDailyBookings, previousDailyBookings);
		double weeklyPercentageChange = calculatePercentageChange(currentWeeklyBookings, previousWeeklyBookings);
		double monthlyPercentageChange = calculatePercentageChange(currentMonthlyBookings, previousMonthlyBookings);

		BookingComparisonDTO comparisonDTO = new BookingComparisonDTO();
		comparisonDTO.setCurrentDailyBookings(currentDailyBookings);
		comparisonDTO.setPreviousDailyBookings(previousDailyBookings);
		comparisonDTO.setDailyPercentageChange(dailyPercentageChange);

		comparisonDTO.setCurrentWeeklyBookings(currentWeeklyBookings);
		comparisonDTO.setPreviousWeeklyBookings(previousWeeklyBookings);
		comparisonDTO.setWeeklyPercentageChange(weeklyPercentageChange);

		comparisonDTO.setCurrentMonthlyBookings(currentMonthlyBookings);
		comparisonDTO.setPreviousMonthlyBookings(previousMonthlyBookings);
		comparisonDTO.setMonthlyPercentageChange(monthlyPercentageChange);

		comparisonDTO.setTotalBookings(totalBookings);

		return comparisonDTO;
	}

	@Override
	public double calculatePercentageChange(long currentBookings, long previousBookings) {
		return ((currentBookings - previousBookings) / (double) previousBookings) * 100;
	}

	@Override
	public BigDecimal getTotalAmountSum() {
		List<Booking> allBookings = bookingRepository.findAll();
		BigDecimal totalSum = BigDecimal.ZERO;

		for (Booking booking : allBookings) {
			totalSum = totalSum.add(booking.getTotalAmount());
		}
		return totalSum;
	}

	public Map<String, BigDecimal> calculateMonthlyEarningsAndExpenses(Date month) {
		// Filter bookings for the specified month
		List<Booking> bookingsOfMonth = bookingRepository.findAll().stream().filter(booking -> {
			TimeDuration timeDuration = booking.getTimeDuration();
			return timeDuration.getStartDateTime().getMonth()
					.equals(month.toInstant().atZone(ZoneId.systemDefault()).getMonth())
					&& timeDuration.getStartDateTime().getYear() == month.toInstant().atZone(ZoneId.systemDefault())
							.getYear();
		}).collect(Collectors.toList());

		BigDecimal totalEarnings = BigDecimal.ZERO;
		BigDecimal totalExpenses = BigDecimal.ZERO;

		// Iterate through bookings and calculate earnings and expenses
		for (Booking booking : bookingsOfMonth) {
			totalEarnings = totalEarnings.add(booking.getTotalAmount());
			// Assuming expenses are stored as negative values
			BigDecimal hubEmployeePayment = BigDecimal
					.valueOf(hubPaymentService.getSumOfAllAmountsOfHubEmployeeForCurrentMonth());
			BigDecimal driverPayment = BigDecimal
					.valueOf(hubPaymentService.getSumOfAllAmountsOfDriverForCurrentMonth());
			BigDecimal repairCost = hubService.getTotalCostOfRepairingForCurrentMonth();
			if (booking.getRefundableDeposit() > 0) {
				totalExpenses = totalExpenses.add(BigDecimal.valueOf(booking.getRefundableDeposit()));

			}
		}

		// Prepare and return the result as a map
		Map<String, BigDecimal> result = new HashMap<>();
		result.put("earnings", totalEarnings);
		result.put("expenses", totalExpenses);

		return result;
	}

	public Map<String, BigDecimal> calculateWeeklyEarningsAndExpenses(Date startDate, Date endDate) {
		// Convert Date objects to LocalDateTime objects
		LocalDateTime startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime endDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		// Query bookings within the specified date range
		List<Booking> bookings = bookingRepository.findByTimeDurationStartDateTimeBetween(startDateTime, endDateTime);

		BigDecimal totalEarnings = BigDecimal.ZERO;
		BigDecimal totalExpenses = BigDecimal.ZERO;

		for (Booking booking : bookings) {
			totalEarnings = totalEarnings.add(booking.getTotalAmount());
			// Assuming expenses are stored as negative values
			BigDecimal hubEmployeePayment = BigDecimal
					.valueOf(hubPaymentService.getSumOfAllAmountsOfHubEmployeeForCurrentMonth());
			BigDecimal driverPayment = BigDecimal
					.valueOf(hubPaymentService.getSumOfAllAmountsOfDriverForCurrentMonth());
			BigDecimal repairCost = hubService.getTotalCostOfRepairingForCurrentMonth();

			totalExpenses = totalExpenses.add(hubEmployeePayment).add(driverPayment).add(repairCost);
			if (booking.getRefundableDeposit() > 0) {
				totalExpenses = totalExpenses.add(BigDecimal.valueOf(booking.getRefundableDeposit()));

			}
		}

		Map<String, BigDecimal> result = new HashMap<>();
		result.put("earnings", totalEarnings);
		result.put("expenses", totalExpenses);

		return result;
	}

	public Map<String, BigDecimal> calculateDailyEarningsAndExpenses(Date date) {
		// Convert Date object to LocalDate object
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		// Calculate start and end of the day
		LocalDateTime startOfDay = localDate.atStartOfDay();
		LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

		// Query bookings for the specified day
		List<Booking> bookings = bookingRepository.findByTimeDurationStartDateTimeBetween(startOfDay, endOfDay);

		Map<String, BigDecimal> dailyResult = new HashMap<>();
		Map<String, BigDecimal> result = new HashMap<>();

		// Sum earnings and expenses
		for (Booking booking : bookings) {
			BigDecimal totalAmount = booking.getTotalAmount();
			String dayOfWeek = booking.getTimeDuration().getStartDateTime().getDayOfWeek().toString();
			dailyResult.put(dayOfWeek, dailyResult.getOrDefault(dayOfWeek, BigDecimal.ZERO).add(totalAmount));
		}

		// Sort the map by day of week
		dailyResult.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.forEachOrdered(x -> result.put(x.getKey(), x.getValue()));

		return result;
	}

	@Override
	public Map<String, BigDecimal> getWeeklyTotalEarningGraph() {
		LocalDate today = LocalDate.now();
		LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		List<Booking> bookings = bookingRepository.findAllWithStartTimeBetween(startOfWeek.atStartOfDay(),
				endOfWeek.atTime(LocalTime.MAX));

		Map<String, BigDecimal> weeklyTotalAmount = bookings.stream()
				.collect(Collectors.groupingBy(
						booking -> booking.getTimeDuration().getStartDateTime().getDayOfWeek().toString(),
						Collectors.reducing(BigDecimal.ZERO, Booking::getTotalAmount, BigDecimal::add)));

		return weeklyTotalAmount;
	}

	@Override
	public Map<String, BigDecimal> getMonthlyTotalEarningGraph() {
		int currentYear = Year.now().getValue();
		List<Booking> bookings = bookingRepository.findAllByYear(currentYear);

		Map<String, BigDecimal> monthlyTotalAmount = bookings.stream()
				.collect(Collectors.groupingBy(
						booking -> booking.getTimeDuration().getStartDateTime().getMonth().toString(), TreeMap::new,
						Collectors.reducing(BigDecimal.ZERO, Booking::getTotalAmount, BigDecimal::add)));

		return monthlyTotalAmount;
	}

	// chnaged jyoti

	@Override
	public List<Booking> getAllOrders() {
		List<Booking> orders = bookingRepository.findAll();
		if (orders.isEmpty()) {
			throw new BusinessException("602", "No orders found.");
		}
		return orders;
	}

	public List<BookedOrderDTO> getBookedOrders() {
		List<Booking> completedOrders = bookingRepository.findByRideOrderStatus(RideOrderStatus.BOOKED);

		List<BookedOrderDTO> completedOrderDTOList = new ArrayList<>();

		for (Booking order : completedOrders) {
			Driver driver = order.getDriver();
			User user = order.getUser();

			BookedOrderDTO completedOrderDTO = new BookedOrderDTO();
			completedOrderDTO.setOrderId(order.getBookingId());
			completedOrderDTO.setDriverName(driver.getName());
			completedOrderDTO.setUserName(user.getUsername());
			// Add more fields if needed
			// Example: completedOrderDTO.setTotalAmount(order.getTotalAmount());

			completedOrderDTOList.add(completedOrderDTO);
		}

		return completedOrderDTOList;
	}

//COMPLETE BOOKING

	public List<BookedOrderDTO> getCompletedOrders() {
		List<Booking> completedOrders = bookingRepository.findByRideOrderStatus(RideOrderStatus.COMPLETE);

		List<BookedOrderDTO> completedOrderDTOList = new ArrayList<>();

		for (Booking order : completedOrders) {
			Driver driver = order.getDriver();
			User user = order.getUser();

			BookedOrderDTO completedOrderDTO = new BookedOrderDTO();
			completedOrderDTO.setOrderId(order.getBookingId());
			completedOrderDTO.setDriverName(driver.getName());

			// Add more fields if needed
			// Example: completedOrderDTO.setTotalAmount(order.getTotalAmount());

			completedOrderDTOList.add(completedOrderDTO);
		}

		return completedOrderDTOList;
	}

	@Override
	public int getTotalNumberOfOrders() {
		List<Booking> listorder = getAllOrders();
		int totalOrders = listorder.size();
		if (totalOrders == 0) {
			throw new BusinessException("NO_DATA_FOUND", "No booking data available.");
		}
		return totalOrders;
	}

	@Override
	public int getTotalCompletedRidesForDriverOnDate(Long driverId, LocalDate date) {
		Driver driver = driverRepository.findById(driverId).orElse(null);
		System.out.println("driver=" + driver);

		if (driver == null) {
			throw new IllegalArgumentException("Driver not found");
		}

		List<Booking> completedRides = bookingRepository.findCompletedRidesForDriver(driverId);
		System.out.println("completedRides=" + completedRides);

		// return completedRides.size();

		List<Booking> completedRidesOnDate = completedRides.stream()
				.filter(order -> order.getTimeDuration().getEndDateTime().toLocalDate().equals(date))
				.collect(Collectors.toList());

		return completedRidesOnDate.size();
	}

	@Override
	public DriverInfoDTO getDriverInfoByOrderId(Long orderId) {
		Booking order = bookingRepository.findById(orderId).orElse(null);
		if (order != null) {
			Driver driver = order.getDriver();
			Vehicle vehicle = driver.getVehicle();

			DriverInfoDTO driverInfoDTO = new DriverInfoDTO();
			driverInfoDTO.setName(driver.getName());
			driverInfoDTO.setPhoneNo(driver.getPhoneNo());
			driverInfoDTO.setVehicleNo(vehicle.getVehicleNo());
			driverInfoDTO.setPrice(vehicle.getPrice());
			driverInfoDTO.setDistance(vehicle.getDistance());
//			driverInfoDTO.setVehicleType(vehicle.getVehicleType());

			return driverInfoDTO;
		}
		return null;
	}

	@Override

	public List<ActiveDriverInfoDto> getOngoingDriversOnDate() {
		List<Booking> bookings = bookingRepository.findAll();
		List<ActiveDriverInfoDto> activeDriverInfoList = new ArrayList<>();
		boolean hasOngoingDrivers = false; // Flag to track if there are ongoing drivers

		for (Booking booking : bookings) {
			Driver driver = booking.getDriver();
			if (driver != null && driver.getStatus() != null && driver.getStatus() == Status.ONGOING) {
				ActiveDriverInfoDto activeDriverInfoDto = new ActiveDriverInfoDto();
				activeDriverInfoDto.setDriverName(driver.getName());
				activeDriverInfoDto.setRideStatus(driver.getStatus().toString());
				activeDriverInfoDto.setPickupLongitude(booking.getPickupLocation().getUserLongitude());
				activeDriverInfoDto.setPickupLatitude(booking.getPickupLocation().getUserLatitude());
				activeDriverInfoDto.setDropUpLatitude(booking.getDropOffLocation().getUserLatitude());
				activeDriverInfoDto.setDropUpLongitude(booking.getDropOffLocation().getUserLongitude());
				activeDriverInfoList.add(activeDriverInfoDto);
				hasOngoingDrivers = true; // Set the flag to true if there's at least one ongoing driver
			}
		}

		if (!hasOngoingDrivers) {
			throw new BusinessException("604", "There are no ongoing drivers on the specified date.");
		}

		return activeDriverInfoList;
	}

	@Override
	public List<IncompleteRideDto> getIncompleteRidesByDate(LocalDate date) {
		// Find all incomplete bookings
		List<Booking> incompleteBookings = bookingRepository.findByRideOrderStatus(RideOrderStatus.IN_COMPLETE);
		System.out.println("incompleteBookings=" + incompleteBookings);

		if (incompleteBookings.isEmpty()) {
			throw new BusinessException("603", "There are no incomplete bookings.");
		}

		// Filter incomplete bookings by the specified date
		List<Booking> filteredBookings = incompleteBookings.stream()
				.filter(booking -> booking.getTimeDuration().getStartDateTime().toLocalDate().isEqual(date))
				.collect(Collectors.toList());
		System.out.println("filteredBookings=" + filteredBookings);
		if (filteredBookings.isEmpty()) {
			throw new BusinessException("603", "There are no change carlist for the specified date.");
		}

		// Map filtered bookings to DTOs
		return filteredBookings.stream()
				.map(booking -> new IncompleteRideDto(booking.getUser().getName(),
						booking.getPickupLocation().getUserLongitude(), booking.getPickupLocation().getUserLatitude(),
						booking.getTimeDuration().getStartDateTime(), booking.getDropOffLocation().getUserLatitude(),
						booking.getDropOffLocation().getUserLongitude(), booking.getTimeDuration().getEndDateTime()))
				.collect(Collectors.toList());
	}

	@Override
	public BookingResponseDto getBookingOrder(Long bookingId) {

		Booking bookingDetails = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + bookingId));

		BookingResponseDto bookingResponseDto = new BookingResponseDto();

		bookingResponseDto.setDropOffLocation(bookingDetails.getDropOffLocation().getUserLatitude());
		bookingResponseDto.setPickupLocation(bookingDetails.getDropOffLocation().getUserLongitude());
		bookingResponseDto.setStartTimeDuration(bookingDetails.getTimeDuration().getStartDateTime());
		bookingResponseDto.setAmount(bookingDetails.getTotalAmount());

		return bookingResponseDto;

	}

	@Override
	public RentalBookingDto bookRental(RentalBooking request, Long userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
		request.setUser(user);

		Driver driver = driverRepository.findById(request.getDriver().getDriverId())
				.orElseThrow(() -> new RuntimeException("Driver not found with ID: " + userId));

		RentalUserLocation pickupLocation = request.getTravelLocation();
		pickupLocation.setUser(user);

		int hours = request.getHours();
		int distance = request.getDistance();
		Vehicle selectedVehicle = request.getVehicle();
		if (selectedVehicle == null) {
			throw new IllegalArgumentException("No vehicle selected");
		}
		selectedVehicle = vehicleRepository.findById(selectedVehicle.getVehicleId())
				.orElseThrow(() -> new RuntimeException("Vehicle not found"));

		RentalPackageType packageType = selectedVehicle.getVehicleServiceType();

		TimeDuration timeDuration = new TimeDuration();
		timeDuration.setStartDateTime(request.getTimeDuration().getStartDateTime());

		// Calculate total amount based on car type, hours, and distance
		BigDecimal totalAmount = calculateBaseAmount(hours, distance, selectedVehicle.getPricePerKm());

		RentalBooking rentalBooking = new RentalBooking();
		rentalBooking.setTravelLocation(pickupLocation);
		rentalBooking.setTimeDuration(timeDuration);
		rentalBooking.setHours(hours);
		rentalBooking.setDistance(distance);
		rentalBooking.setRentalPackageType(packageType);
		rentalBooking.setGst("5%");
		rentalBooking.setAmount(totalAmount);
		rentalBooking.setTotalAmount(totalAmount);
		rentalBooking.setUser(user);
		rentalBooking.setVehicle(selectedVehicle);
		rentalBooking.setRideOrderStatus(RideOrderStatus.BOOKED);
		rentalBooking.setIsConfirm(CourierBookingStatus.NOT_CONFIRMED);
		rentalBooking.setDriver(driver);

		rentalBookingRepository.save(rentalBooking);

		RentalBookingDto rentalBookingDto = new RentalBookingDto();

		rentalBookingDto.setRentalBookingId(rentalBooking.getRentalBookingId());
		rentalBookingDto.setTravelLocation(pickupLocation);
		rentalBookingDto.setTimeDuration(timeDuration);
		rentalBookingDto.setHours(hours);
		rentalBookingDto.setDistance(distance);
		rentalBookingDto.setRentalPackageType(packageType);
		rentalBookingDto.setGst("5%");
		rentalBookingDto.setAmount(totalAmount);
		rentalBookingDto.setTotalAmount(totalAmount);
		rentalBookingDto.setUser(user);
		rentalBookingDto.setVehicle(selectedVehicle);
		rentalBookingDto.setRideOrderStatus(RideOrderStatus.BOOKED);
		rentalBookingDto.setIsConfirm(CourierBookingStatus.NOT_CONFIRMED);
		rentalBookingDto.setDriver(driver);
		return rentalBookingDto;

	}

	@Override
	public RentalBooking saveRentalBooking(RentalBooking rentalBooking) {
		return rentalBookingRepository.save(rentalBooking);
	}

	@Override
	@Modifying
	@Transactional
//	@Scheduled(cron = "*/30 * * * * *") // 30 second
//	@Scheduled(cron = "*/10 * * * * ?") // 10 second // Cron Expression
//	@Scheduled(cron = "0 */2 * * * ?") // 2 minute
//	@Scheduled(cron = "0 */5 * * * ?")  // 5 minute
	@Scheduled(cron = "0 */15 * * * ?") // 15 minute
	public void deleteNotConfirmedRentalBookings() {
		rentalBookingRepository.deleteByIsConfirm(CourierBookingStatus.NOT_CONFIRMED);
	}

	@Override
	public PaymentActivity paymentOrderCreateForRentalBooking(RentalBooking rentalBooking) {

		try {
			RentalBooking bookingDetails = rentalBookingRepository.findById(rentalBooking.getRentalBookingId())
					.orElseThrow(() -> new RuntimeException(
							"RentalBooking not found with ID: " + rentalBooking.getRentalBookingId()));

			BigDecimal totalAmount = bookingDetails.getTotalAmount(); // Assuming totalAmount is a BigDecimal

			// Convert total amount to paise (smallest currency unit) for Razorpay
			int amountInPaise = totalAmount.multiply(BigDecimal.valueOf(100)).intValue();

//			bookingDetails.setNote(rentalBooking.getNote());

			RazorpayClient client = new RazorpayClient(razorPayConfiguration.getRazorpayKey(),
					razorPayConfiguration.getRazorpaySecret());
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amountInPaise);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "receipt #1");

			Order order = client.orders.create(orderRequest);

			LocalDateTime now = LocalDateTime.now();

			// Define a DateTimeFormatter to format the date and time with AM/PM indicator
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
			String formattedDateTime = now.format(formatter);
			LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

			PaymentActivity payment = new PaymentActivity();
			payment.setAmount(order.get("amount").toString());
			payment.setReceipt(order.get("receipt"));
			payment.setOrderId(order.get("id"));
			payment.setOrderStatus(order.get("status").toString());
			// Set the correct payment ID obtained from Razorpay response
			payment.setPayementId(order.get(null)); // Set the correct payment ID here
			payment.setUser(bookingDetails.getUser());
			payment.setLocalDatetime(parsedDateTime);
			payment.setRentalBooking(bookingDetails);
			paymentRepository.save(payment);

			// Save the updated booking details
			rentalBookingRepository.save(bookingDetails);

			return payment; // Return success message
		} catch (RazorpayException e) {
			// Handle Razorpay exception
			e.printStackTrace();
			return null;
		} catch (NumberFormatException e) {
			// Handle number format exception
			e.printStackTrace();
			return null;
		} catch (RuntimeException e) {
			// Handle runtime exception
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public RentalBooking extendRental(Long rentalBookingId, ExtendedRental extendedRental) {
		RentalBooking existingBooking = rentalBookingRepository.findById(rentalBookingId)
				.orElseThrow(() -> new RuntimeException("Rental booking not found with ID: " + rentalBookingId));

		int extraHours = extendedRental.getExtraHours();
		int extraDistance = extendedRental.getExtraDistance();
		BigDecimal additionalAmount = calculateAdditionalAmount(extraHours, extraDistance,
				existingBooking.getVehicle().getPricePerKm());

		int totalHours = existingBooking.getHours() + extraHours;
		int totalDistance = existingBooking.getDistance() + extraDistance;

		BigDecimal totalAmount = existingBooking.getTotalAmount().add(additionalAmount);
		existingBooking.setExtraHours(extraHours);
		existingBooking.setExtraDistance(extraDistance);
		existingBooking.setExtraAmount(additionalAmount);
//		existingBooking.setTotalAmount(totalAmount);
//		existingBooking.setHours(totalHours);
//		existingBooking.setDistance(totalDistance);

		return rentalBookingRepository.save(existingBooking);
	}

	@Override
	public PaymentActivity paymentOrderCreateForExtendRentalBooking(RentalBooking rentalBooking) {

		try {
			RentalBooking bookingDetails = rentalBookingRepository.findById(rentalBooking.getRentalBookingId())
					.orElseThrow(() -> new RuntimeException(
							"RentalBooking not found with ID: " + rentalBooking.getRentalBookingId()));

			BigDecimal totalAmount = bookingDetails.getExtraAmount(); // Assuming totalAmount is a BigDecimal

			// Convert total amount to paise (smallest currency unit) for Razorpay
			int amountInPaise = totalAmount.multiply(BigDecimal.valueOf(100)).intValue();

			// Set booking note if provided
//			bookingDetails.setNote(rentalBooking.getNote());

			RazorpayClient client = new RazorpayClient(razorPayConfiguration.getRazorpayKey(),
					razorPayConfiguration.getRazorpaySecret());
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amountInPaise);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "receipt #1");

			Order order = client.orders.create(orderRequest);

			LocalDateTime now = LocalDateTime.now();

			// Define a DateTimeFormatter to format the date and time with AM/PM indicator
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
			String formattedDateTime = now.format(formatter);
			LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

			PaymentActivity payment = new PaymentActivity();
			payment.setAmount(order.get("amount").toString());
			payment.setReceipt(order.get("receipt"));
			payment.setOrderId(order.get("id"));
			payment.setOrderStatus(order.get("status").toString());
			// Set the correct payment ID obtained from Razorpay response
			payment.setPayementId(order.get(null)); // Set the correct payment ID here
			payment.setUser(bookingDetails.getUser());
			payment.setLocalDatetime(parsedDateTime);
			payment.setRentalBooking(bookingDetails);
			paymentRepository.save(payment);

			// Save the updated booking details
			rentalBookingRepository.save(bookingDetails);

			return payment; // Return success message
		} catch (RazorpayException e) {
			// Handle Razorpay exception
			e.printStackTrace();
			return null;
		} catch (NumberFormatException e) {
			// Handle number format exception
			e.printStackTrace();
			return null;
		} catch (RuntimeException e) {
			// Handle runtime exception
			e.printStackTrace();
			return null;
		}
	}

	private BigDecimal calculateBaseAmount(int hours, int distance, BigDecimal pricePerKm) {
		BigDecimal ratePerKm = pricePerKm;

		BigDecimal totalAmount = ratePerKm.multiply(BigDecimal.valueOf(distance)); // distance

		totalAmount = totalAmount.add(BigDecimal.valueOf(hours * 200)); // Additional charge per hour

		// Calculate GST (5% of the total base amount)
		BigDecimal gstAmount = totalAmount.multiply(BigDecimal.valueOf(0.05));

		// Add GST to the total amount
		totalAmount = totalAmount.add(gstAmount);

		return totalAmount;
	}

	// Method to calculate additional amount based on extra hours and distance
	// including GST
	private BigDecimal calculateAdditionalAmount(int extraHours, int extraDistance, BigDecimal pricePerKm) {
		BigDecimal ratePerKm = pricePerKm;
		BigDecimal additionalAmount = ratePerKm.multiply(BigDecimal.valueOf(extraDistance)); // Calculate additional
																								// amount based on extra
																								// distance

		additionalAmount = additionalAmount.add(BigDecimal.valueOf(extraHours * 200)); // Additional charge per extra
																						// hour

		// Calculate GST (5% of the total additional amount)
		BigDecimal gstAmount = additionalAmount.multiply(BigDecimal.valueOf(0.05));

		// Add GST to the total additional amount
		additionalAmount = additionalAmount.add(gstAmount);

		return additionalAmount;
	}

	// Method to parse the startDateTime string into LocalDateTime
	private LocalDateTime parseStartDateTime(String startDateTimeStr) {
		// Define a formatter for the provided date and time format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
		// Parse the string into LocalDateTime using the formatter
		return LocalDateTime.parse(startDateTimeStr, formatter);
	}

	@Override
	public RentalBooking applyPromoCode(Long rentalBookingId, String promoCode) {
		RentalBooking booking = rentalBookingRepository.findById(rentalBookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found with ID: " + rentalBookingId));

		PromoCode promo = promoCodeRepository.findByCode(promoCode)
				.orElseThrow(() -> new IllegalArgumentException("Invalid promo code"));

		boolean promoCodeAlreadyUsed = rentalBookingRepository.existsByUserAndPromoCode(booking.getUser(), promoCode);
		if (promoCodeAlreadyUsed) {
			throw new IllegalArgumentException("Promo code has already been used by this user.");
		}

		BigDecimal discount = promo.getDiscountPercentage();
		BigDecimal discountedAmount = booking.getAmount().multiply(BigDecimal.ONE.subtract(discount));
		booking.setTotalAmount(discountedAmount);
		booking.setPromoCode(promoCode);

		return rentalBookingRepository.save(booking);
	}

	@Override
	public RentalBooking removePromoCode(Long rentalBookingId) {
		RentalBooking booking = rentalBookingRepository.findById(rentalBookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found with ID: " + rentalBookingId));

		booking.setTotalAmount(booking.getAmount());
		booking.setPromoCode(null);

		return rentalBookingRepository.save(booking);
	}

	@Override
	public RentalBooking getRentalBookingById(Long rentalBookingId) {
		return rentalBookingRepository.findById(rentalBookingId).orElse(null);
	}

//	    public void saveRentalBooking(RentalBooking rentalBooking) {
//	        rentalBookingRepository.save(rentalBooking);
//	    }

	@Override
	public int getTotalCompletedRidesForDriverWithinDateRanges(Long driverId, LocalDate startDate, LocalDate date) {
		Driver driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new IllegalArgumentException("Driver not found"));

		// Fetch all completed rides for the driver
		List<Booking> completedRides = bookingRepository.findCompletedRidesForDriver(driverId);

		// Filter completed rides within the date range
		List<Booking> completedRidesWithinDateRange = completedRides.stream()
				.filter(order -> order.getTimeDuration().getEndDateTime().toLocalDate().isAfter(startDate.minusDays(1))
						&& order.getTimeDuration().getEndDateTime().toLocalDate().isBefore(date.plusDays(1)))
				.collect(Collectors.toList());

		return completedRidesWithinDateRange.size();
	}

	@Override
	public int getTotalCompletedRidesForDriverInWeek(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {
		// Fetch the driver
		Driver driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new IllegalArgumentException("Driver not found"));

		// Fetch all completed rides for the driver
		List<Booking> completedRides = bookingRepository.findCompletedRidesForDriver(driverId);

		// Filter completed rides within the date range
		List<Booking> completedRidesWithinDateRange = completedRides.stream()
				.filter(order -> order.getTimeDuration().getEndDateTime().isAfter(startDate)
						&& order.getTimeDuration().getEndDateTime().isBefore(endDate.plusDays(1)))
				.collect(Collectors.toList());

		return completedRidesWithinDateRange.size();
	}

	public Booking removePromoCodeForShedule(Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

		double distance = CalculateDistance.distance(booking.getPickupLocation().getUserLatitude(),
				booking.getPickupLocation().getUserLongitude(), booking.getDropOffLocation().getUserLatitude(),
				booking.getDropOffLocation().getUserLongitude());

		BigDecimal totalPrice = calculatePrice1(booking.getPickupLocation().getUserLatitude(),
				booking.getPickupLocation().getUserLongitude(), booking.getDropOffLocation().getUserLatitude(),
				booking.getDropOffLocation().getUserLongitude(), booking.getDriver());

		booking.setTotalAmount(totalPrice);
		booking.setPromoCode(null);

		return bookingRepository.save(booking);
	}

	@Override
	public Booking applyPromoCodeForShedule(Long bookingId, String promoCode) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

		// Fetch promo code details from the database
		PromoCode promo = promoCodeRepository.findByCode(promoCode)
				.orElseThrow(() -> new IllegalArgumentException("Invalid promo code"));

		// Apply promo code to the total amount
		BigDecimal discount = promo.getDiscountPercentage();
		BigDecimal discountedAmount = booking.getTotalAmount().multiply(BigDecimal.ONE.subtract(discount));
		booking.setTotalAmount(discountedAmount);
		booking.setPromoCode(promoCode);

		return bookingRepository.save(booking);
	}

	// shedule booking for dour wheeler
	public BookingResponseDto scheduleRide2(long userId, ScheduleRideRequest request) {
		// Find the nearest available driver with the specified vehicle type
		Driver nearestDriver = findNearestAvailableDriverWithVehicleType(request.getPickupLat(), request.getPickupLon(),
				request.getDropoffLat(), request.getDropoffLon(), DriverAndVehicleType.FOUR_WHEELER);

		// Calculate distance between pickup and dropoff locations
		double distance = CalculateDistance.distance(request.getPickupLat(), request.getPickupLon(),
				request.getDropoffLat(), request.getDropoffLon());

		// Calculate total amount
		BigDecimal totalAmount = calculatePrice1(request.getPickupLat(), request.getPickupLon(),
				request.getDropoffLat(), request.getDropoffLon(), nearestDriver);

		// Save user locations to the database
		UserLocation pickupLocation = new UserLocation();
		pickupLocation.setUserLatitude(request.getPickupLat());
		pickupLocation.setUserLongitude(request.getPickupLon());
		userLocationRepository.save(pickupLocation);

		UserLocation dropOffLocation = new UserLocation();
		dropOffLocation.setUserLatitude(request.getDropoffLat());
		dropOffLocation.setUserLongitude(request.getDropoffLon());
		userLocationRepository.save(dropOffLocation);

		// Save time duration to the database
		LocalDateTime startDateTime = request.getStartDateTime();
		LocalDateTime endDateTime = LocalDateTime.now();
		TimeDuration timeDuration = new TimeDuration(startDateTime, endDateTime);
		timeDurationRepository.save(timeDuration);

		// Save booking details without setting the driver yet
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found for ID: " + userId));

		Booking booking = new Booking();
		booking.setUser(user);
		booking.setDriver(nearestDriver);
		booking.setHub(nearestDriver.getHub());
		booking.setTotalAmount(totalAmount);
		booking.setGst(GST_RATE);
		booking.setTimeDuration(timeDuration);
		booking.setPickupLocation(pickupLocation);
		booking.setDropOffLocation(dropOffLocation);
		booking.setRideOrderStatus(RideOrderStatus.BOOKED);

		bookingRepository.save(booking);

		// Return booking details with payment status
		return new BookingResponseDto(booking.getBookingId(), distance, totalAmount, startDateTime);

	}

//	           public void sendNotificationMessage(String contactNo, String messageContent) {
//	        	    Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
	//
//	        	    // Send the notification message via SMS using Twilio
//	        	    Message message = Message.creator(new com.twilio.type.PhoneNumber(contactNo),
//	        	            new com.twilio.type.PhoneNumber(twilioConfig.getTrailNumber()), // Use your Twilio phone number here
//	        	            messageContent).create();
//	        	}
	//
	//
//	           
//	           private void sendNotificationToDriver(Driver driver, long bookingId) {
//	        	    // Get the driver's phone number from the driver object
//	        	    String driverPhoneNumber = driver.getPhoneNo(); // Assuming you have a method to retrieve the phone number
	//
//	        	    // Compose the ride request message
//	        	    String messageBody = "You have a new ride request. Booking ID: " + bookingId;
	//
//	        	    // Send the ride request notification to the driver
//	        	    sendNotificationMessage(driverPhoneNumber, messageBody);
//	        	}

	private Driver findNearestAvailableDriverWithVehicleType(double userLat, double userLon, double dropoffLat,
			double dropoffLon, DriverAndVehicleType driverType) {
		// Fetch all available drivers with the specified vehicle type from the database
		List<Driver> availableDrivers = driverRepository.findByDriverTypeAndStatus(driverType, Status.AVAILABLE);

		// Initialize variables to keep track of the nearest driver and their distance
		Driver nearestDriver = null;
		double minDistance = Double.MAX_VALUE;

		// Iterate through all available drivers and calculate their distances from the
		// user
		for (Driver driver : availableDrivers) {
			double driverLat = driver.getDriverLatitude();
			double driverLon = driver.getDriverLongitude();

			// Calculate distance between user and driver using a distance calculation
			// method
			double distance = CalculateDistance.distance(userLat, userLon, driverLat, driverLon);

			// Update nearest driver if this driver is closer
			if (distance < minDistance) {
				minDistance = distance;
				nearestDriver = driver;
			}
		}

		// Check if a nearest available driver was found
		if (nearestDriver == null) {
			throw new RuntimeException("No available drivers with the specified vehicle type");
		}

		return nearestDriver;
	}

	public BigDecimal calculatePrice1(double pickupLat, double pickupLon, double dropoffLat, double dropoffLon,
			Driver nearestDriver) {
		// Calculate distance between pickup and dropoff locations
		double distance = CalculateDistance.distance(pickupLat, pickupLon, dropoffLat, dropoffLon);

		// Retrieve vehicle associated with the nearest driver
		Vehicle vehicle = nearestDriver.getVehicle();

		// Retrieve price per kilometer from the vehicle
		BigDecimal pricePerKm = vehicle.getPricePerKm();

		// Calculate total amount without GST
		BigDecimal totalAmountWithoutGST = pricePerKm.multiply(BigDecimal.valueOf(distance));

		// Calculate GST amount
		BigDecimal gstAmount = totalAmountWithoutGST.multiply(BigDecimal.valueOf(GST_RATE));

		// Add GST to the total amount
		BigDecimal totalAmountWithGST = totalAmountWithoutGST.add(gstAmount);

		return totalAmountWithGST;
	}

	// shedule booking for two wheeler
	public BookingResponseDto scheduleRide1(long userId, ScheduleRideRequest request) {
		// Find the nearest available driver with the specified vehicle type
		Driver nearestDriver = findNearestAvailableDriverWithVehicleType(request.getPickupLat(), request.getPickupLon(),
				request.getDropoffLat(), request.getDropoffLon(), DriverAndVehicleType.TWO_WHEELER);

		// Calculate distance between pickup and dropoff locations
		double distance = CalculateDistance.distance(request.getPickupLat(), request.getPickupLon(),
				request.getDropoffLat(), request.getDropoffLon());

		// Calculate total amount
		BigDecimal totalAmount = calculatePrice1(request.getPickupLat(), request.getPickupLon(),
				request.getDropoffLat(), request.getDropoffLon(), nearestDriver);

		// Save user locations to the database
		UserLocation pickupLocation = new UserLocation();
		pickupLocation.setUserLatitude(request.getPickupLat());
		pickupLocation.setUserLongitude(request.getPickupLon());
		userLocationRepository.save(pickupLocation);

		UserLocation dropOffLocation = new UserLocation();
		dropOffLocation.setUserLatitude(request.getDropoffLat());
		dropOffLocation.setUserLongitude(request.getDropoffLon());
		userLocationRepository.save(dropOffLocation);

		// Save time duration to the database
		LocalDateTime startDateTime = request.getStartDateTime();
		LocalDateTime endDateTime = LocalDateTime.now();
		TimeDuration timeDuration = new TimeDuration(startDateTime, endDateTime);
		timeDurationRepository.save(timeDuration);

		// Save booking details without setting the driver yet

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found for ID: " + userId));

		Booking booking = new Booking();
		booking.setUser(user);
		booking.setDriver(nearestDriver);
		booking.setHub(nearestDriver.getHub());
		booking.setTotalAmount(totalAmount);
		booking.setGst(GST_RATE);
		booking.setTimeDuration(timeDuration);
		booking.setPickupLocation(pickupLocation);
		booking.setDropOffLocation(dropOffLocation);
		booking.setRideOrderStatus(RideOrderStatus.BOOKED);

		bookingRepository.save(booking);

		// Return booking details with payment status
		return new BookingResponseDto(booking.getBookingId(), distance, totalAmount, startDateTime);

	}

	@Override
	public String sendNotificationToDriver1(Long driverId, Booking bookingDetails) {
		User user = bookingDetails.getUser();
		Driver driver = bookingDetails.getDriver();
		String message = String.format(
				"New booking assigned: Booking ID %d, Username: %s, User Image: %s, User Phone: %s, User ID: %d, Driver ID: %d",
				bookingDetails.getBookingId(), user.getName(), user.getImageProfileLink(), user.getPhoneNo(),
				user.getUserId(), driver.getDriverId());

		System.out.println(message);

		// Assuming MyWebSocketHandler has a static method to send messages
		MyWebSocketHandler myWebSocketHandler = new MyWebSocketHandler();
		myWebSocketHandler.broadcastMessage(message);

		return message;
	}

	@Override
	public int getTotalCompletedRidesForDriverinweek(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {

		Driver driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new IllegalArgumentException("Driver not found"));

		// Fetch all completed rides for the driver
		List<Booking> completedRides = bookingRepository.findCompletedRidesForDriver(driverId);

		// Filter completed rides within the date range
		List<Booking> completedRidesWithinDateRange = completedRides.stream()
				.filter(order -> order.getTimeDuration().getEndDateTime().isAfter(startDate)
						&& order.getTimeDuration().getEndDateTime().isBefore(endDate.plusDays(1)))
				.collect(Collectors.toList());

		return completedRidesWithinDateRange.size();
	}

	@Override
	public int calculateTotalRentalPayment(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {
		int totalRentalPayment = 0;

		for (LocalDateTime date = endDate; !date.toLocalDate().isBefore(startDate.toLocalDate()); date = date
				.minusDays(1)) {

			LocalDateTime dayStartTime = date.toLocalDate().atStartOfDay();
			LocalDateTime dayEndTime = date.toLocalDate().atTime(LocalTime.MAX);
			System.out.println("dayStartTime=" + dayStartTime);
			System.out.println("dayEndTime=" + dayEndTime);

			List<RentalBooking> rentalBookingsForDay = rentalBookingRepository
					.findRentalBookingsForDriverAndDay(driverId, dayStartTime, dayEndTime);
			System.out.println("rentalBookingsForDay=" + rentalBookingsForDay);

			if (rentalBookingsForDay != null && !rentalBookingsForDay.isEmpty()) {

				int workingHours = 0;
				for (RentalBooking rentalBooking : rentalBookingsForDay) {
					LocalDateTime startTime = rentalBooking.getTimeDuration().getStartDateTime();
					LocalDateTime endTime = rentalBooking.getTimeDuration().getEndDateTime();
					workingHours += calculateHoursBetween(startTime, endTime);
					System.out.println("RentalworkingHours=" + workingHours);
				}
				totalRentalPayment += calculateRentalPayment(workingHours);
				System.out.println("RentalPayment=" + totalRentalPayment);
			}
		}

		System.out.println("Total rental payment for the specified period: " + totalRentalPayment);
		return totalRentalPayment;
	}

	private int calculateHoursBetween(LocalDateTime startTime, LocalDateTime endTime) {
		return (int) java.time.Duration.between(startTime, endTime).toHours();
	}

	private int calculateRentalPayment(int workingHours) {

		return (workingHours >= 9) ? 300 : 150;
	}

	@Override
	public Map<LocalDate, Integer> calculateWorkingHours(List<ReturnCar> assignCars, List<ReturnCar> returnCars) {

		Map<LocalDate, Integer> workingHoursMap = new HashMap<>();

		for (ReturnCar assignCar : assignCars) {
			LocalDate day = assignCar.getAssignTime().toLocalDate();

			ReturnCar returnCar = returnCars.stream().filter(rc -> rc.getReturnTime().toLocalDate().equals(day))
					.findFirst().orElse(null);

			if (returnCar != null) {

				LocalDateTime openingTime = assignCar.getAssignTime();
				LocalDateTime returnTime = returnCar.getReturnTime();
				int workingHours = calculateHoursBetween(openingTime, returnTime);
				workingHoursMap.put(day, workingHours);
			}
		}

		return workingHoursMap;

	}

	@Override
	public int calculateTotalPayment(Map<LocalDate, Integer> workingHoursMap, int totalCompletedRides,
			DriverAndVehicleType driverType) {
		int totalPayment = 0;
		int totalRides = totalCompletedRides;

		for (int workingHours : workingHoursMap.values()) {
			int dailyPayment = calculatePayment(workingHours, driverType);
			System.out.println("dailyPayment=" + dailyPayment);
			totalPayment += dailyPayment;
		}

		if (totalRides > 1) {
			int incentive = (totalRides - 1) * 50;
			totalPayment += incentive;
		}

		// totalPayment += totalRentalPayment;

		return totalPayment;
	}

//	private int calculatePayment(int workingHours, DriverAndVehicleType driverType) {
//		if (driverType == DriverAndVehicleType.FOUR_WHEELER) {
//			if (workingHours >= 9) {
//				return 200;
//			} else {
//				return 100;
//			}
//		} else if (driverType == DriverAndVehicleType.TWO_WHEELER) {
//			if (workingHours >= 9) {
//				return 50;
//			} else {
//				return 25;
//			}
//		} else {
//
//			return 0;
//		}}
	private int calculatePayment(int workingHours, DriverAndVehicleType driverType) {
		int hourlyRate;

		if (driverType == DriverAndVehicleType.FOUR_WHEELER) {
			hourlyRate = 100;
		} else if (driverType == DriverAndVehicleType.TWO_WHEELER) {
			hourlyRate = 50;
		} else {
			return 0;
		}

		int pay = workingHours * hourlyRate;
		System.out.println("pay=" + pay);
		return pay;
	}

	@Override
	public void saveTotalPayment(Long driverId, int totalPayment, LocalDate date) {
		Driver driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new IllegalArgumentException("Driver not found"));

		DriverPaymentDetail driverPayment = new DriverPaymentDetail();
		driverPayment.setAmount(String.valueOf(totalPayment));
		driverPayment.setDate(date);
		driverPayment.setDriver(driver);
		driverPayment.setHub(driver.getHub());
		driverPayment.setStatus(com.rido.entity.DriverPaymentDetail.Status.PENDING);

		driverPaymentDetailRepo.save(driverPayment);
	}

	@Override
	public DriverAndVehicleType determineDriverType(Long driverId) {
		Optional<Driver> optionalDriver = driverRepository.findById(driverId);
		if (optionalDriver.isPresent()) {
			Driver driver = optionalDriver.get();
			return driver.getDriverType();
		}

		return null;
	}

	@Override
	public List<RentalBookingDto> getAllRentalBookingsList() {
		List<RentalBooking> rentalBookings = rentalBookingRepository.findAll();

		return rentalBookings.stream().map(this::mapRentalBookingToDto).collect(Collectors.toList());
	}

	private RentalBookingDto mapRentalBookingToDto(RentalBooking rentalBooking) {
		// Map relevant fields from rentalBooking to dto
		return RentalBookingDto.builder().rentalBookingId(rentalBooking.getRentalBookingId())
				.rideOrderStatus(rentalBooking.getRideOrderStatus()).timeDuration(rentalBooking.getTimeDuration())
				.pickupAddress(rentalBooking.getTravelLocation().getPickupAddress())
				.userPickupLatitude(rentalBooking.getTravelLocation().getUserPickupLatitude())
				.userPickupLongitude(rentalBooking.getTravelLocation().getUserPickupLongitude())
				.driver(rentalBooking.getDriver()).userId(rentalBooking.getUser().getUserId())
				.userName(rentalBooking.getUser().getName()).vehicleId(rentalBooking.getVehicle().getVehicleId())
				.hub(rentalBooking.getHub()).rentalPackageType(rentalBooking.getRentalPackageType())
				.promoCode(rentalBooking.getPromoCode()).amount(rentalBooking.getAmount())
				.totalAmount(rentalBooking.getTotalAmount()).hours(rentalBooking.getHours())
				.distance(rentalBooking.getDistance()).gst(rentalBooking.getGst())
				.extraHours(rentalBooking.getExtraHours()).extraDistance(rentalBooking.getExtraDistance())
				.extraAmount(rentalBooking.getExtraAmount()).note(rentalBooking.getNote()).build();
	}

	public void deleteBookingById(long bookingId) {
		Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
		if (optionalBooking.isPresent()) {
			bookingRepository.deleteById(bookingId);
		} else {
			throw new RuntimeException("Booking not found with ID: " + bookingId);
		}
	}

	@Override
	public int getTotalCompletedrentalRidesForDriverinweek(Long driverId, LocalDateTime startDate,
			LocalDateTime endDate) {

		// Fetch the driver to ensure it exists
		driverRepository.findById(driverId).orElseThrow(() -> new IllegalArgumentException("Driver not found"));

		// Fetch all completed rides for the driver
		List<RentalBooking> completedRides = rentalBookingRepository.findCompletedRidesForDriver(driverId);

		// Filter completed rides within the date range
		List<RentalBooking> completedRidesWithinDateRange = completedRides.stream()
				.filter(order -> order.getTimeDuration().getEndDateTime().isAfter(startDate)
						&& order.getTimeDuration().getEndDateTime().isBefore(endDate.plusDays(1)))
				.collect(Collectors.toList());

		return completedRidesWithinDateRange.size();
	}

	@Override
	public PaymentActivityResponseDto paymentOrderCreateForSheduleBooking1(Booking booking) {
		PaymentActivityResponseDto responseDto = new PaymentActivityResponseDto();
		try {
			Booking bookingDetails = bookingRepository.findById(booking.getBookingId())
					.orElseThrow(() -> new RuntimeException("Booking not found with ID: " + booking.getBookingId()));

			BigDecimal totalAmount = bookingDetails.getTotalAmount(); // Assuming totalAmount is a BigDecimal

			// Convert total amount to paise (smallest currency unit) for Razorpay
			int amountInPaise = totalAmount.multiply(BigDecimal.valueOf(100)).intValue();

			// Set booking note if provided
			bookingDetails.setNote(booking.getNote());

			RazorpayClient client = new RazorpayClient(razorPayConfiguration.getRazorpayKey(),
					razorPayConfiguration.getRazorpaySecret());
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amountInPaise);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "receipt #1");

			Order order = client.orders.create(orderRequest);

			LocalDateTime now = LocalDateTime.now();

			// Define a DateTimeFormatter to format the date and time with AM/PM indicator
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
			String formattedDateTime = now.format(formatter);
			LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

			PaymentActivity payment = new PaymentActivity();
			payment.setAmount(order.get("amount").toString());
			payment.setReceipt(order.get("receipt"));
			payment.setOrderId(order.get("id"));
			payment.setOrderStatus(order.get("status").toString());
			payment.setPayementId(order.get("id")); // Set the correct payment ID here
			payment.setUser(bookingDetails.getUser());
			payment.setLocalDatetime(parsedDateTime);
			payment.setBooking(booking);
			paymentRepository.save(payment);

			// Save the updated booking details
			bookingRepository.save(bookingDetails);

			// Send notification to the driver
//			String message = sendNotificationToDriver23(bookingDetails.getDriver().getDriverId(), bookingDetails);

			responseDto.setPaymentActivity(payment);
//			responseDto.setMessageNotification(message);

			return responseDto;
		} catch (RazorpayException e) {
			// Handle Razorpay exception
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// Handle number format exception
			e.printStackTrace();
		} catch (RuntimeException e) {
			// Handle runtime exception
			e.printStackTrace();
		}
		return null;
	}

	public UserNotificationDto saveDriverIdInBooking(Long driverId, long bookingId) {
		// Retrieve the booking entity from the database
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Booking not found with ID: " + bookingId));

		// Retrieve the driver entity from the database
		Driver driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new NotFoundException("Driver not found with ID: " + driverId));

		// Set the driver ID in the booking entity
		booking.setDriver(driver);
		driver.setStatus(Status.ONGOING);

		// Save the updated booking entity
		bookingRepository.save(booking);

		// Send notification to the driver and return the notification message
		return sendNotificationToUser23(driverId, bookingId);
	}

	// Method to send notification to the user
	@Override
	public UserNotificationDto sendNotificationToUser23(Long driverId, Long bookingId) {
		// Fetch booking details based on booking ID
		Booking bookingDetails = bookingRepository.findByBookingId(bookingId);

		// Check if bookingDetails is not null and contains user and driver information
		if (bookingDetails != null && bookingDetails.getUser() != null && bookingDetails.getDriver() != null) {
			// Check if the driver ID matches
			if (bookingDetails.getDriver().getDriverId().equals(driverId)) {
				// Create UserNotificationDto object
				UserNotificationDto notificationDto = new UserNotificationDto();
				notificationDto.setBookingId(bookingDetails.getBookingId());
				notificationDto.setDriverId(driverId);
				notificationDto.setUserId(bookingDetails.getUser().getUserId());
				notificationDto.setName(bookingDetails.getUser().getName());
				notificationDto.setPhonenumber(bookingDetails.getUser().getPhoneNo());
				notificationDto.setProfileImg(bookingDetails.getUser().getImageProfileLink());

				// Convert UserNotificationDto object to JSON
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					String jsonMessage = objectMapper.writeValueAsString(notificationDto);

					// Broadcast the JSON message via WebSocket
					MyWebSocketHandler myWebSocketHandler = new MyWebSocketHandler();
					myWebSocketHandler.broadcastMessage(jsonMessage);
				} catch (JsonProcessingException e) {
					// Handle JSON processing exception
					e.printStackTrace();
				}

				return notificationDto; // Return DTO object
			} else {
				// Handle case where driver ID does not match
				System.out.println("Error: Provided driver ID does not match the driver ID in the booking details.");
			}
		}
		return null; // Return null if booking details are not found or driver ID does not match
	}

	@Override
	public Map<String, String> sendVerificationCodeToUser(long bookingId) {

		Booking booking = bookingRepository.findByBookingId(bookingId);
		if (booking != null) {
			// Get user's phone number from the booking
			String userPhoneNumber = booking.getUser().getPhoneNo();
			// Generate verification code
			String verificationCode = locationImpl.generateVerificationCode();
			// Send verification code to user
			locationImpl.sendVerificationCode(userPhoneNumber, verificationCode);

			// Prepare response
			Map<String, String> response = new HashMap<>();
			response.put("phoneNumber", userPhoneNumber);
			response.put("otp", verificationCode);
			return response;
		} else {
			throw new RuntimeException("Booking not found with ID: " + bookingId);
		}
	}

	@Override
	public List<CourierBookingDto1> getAllBookings() {
		List<Booking> bookings = bookingRepository.findAll();
		return bookings.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	private CourierBookingDto1 convertToDto(Booking booking) {
		return new CourierBookingDto1(booking.getRideOrderStatus(), booking.getTimeDuration().getStartDateTime(),
				booking.getTimeDuration().getEndDateTime(), booking.getTotalAmount());
	}

	@Override
	public List<CourierBookingDto1> getRentalBooking() {
		List<RentalBooking> rentalBookings = rentalBookingRepository.findAll();
		return rentalBookings.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	private CourierBookingDto1 convertToDto(RentalBooking rentalBooking) {
		return new CourierBookingDto1(rentalBooking.getRideOrderStatus(),
				rentalBooking.getTimeDuration().getStartDateTime(), rentalBooking.getTimeDuration().getEndDateTime(),
				rentalBooking.getTotalAmount());
	}

	@Override
	public DriverNotificationDto sendNotificationToDriver23(Long userId, Long bookingId) {
		// Fetch booking details based on user ID and booking ID
		Booking bookingDetails = bookingRepository.findByUser_UserIdAndBookingId(userId, bookingId);

		// Check if bookingDetails is not null and contains user and driver information
		if (bookingDetails != null && bookingDetails.getUser() != null && bookingDetails.getDriver() != null) {
			// Get the driver ID from booking details
			Long driverId = bookingDetails.getDriver().getDriverId();

			// Create DriverNotificationDto object
			DriverNotificationDto notificationDto = new DriverNotificationDto();
			notificationDto.setBookingId(bookingDetails.getBookingId());
			notificationDto.setDriverId(driverId);
			notificationDto.setUserId(userId);
			notificationDto.setName(bookingDetails.getUser().getName());
			notificationDto.setPhoneNumber(bookingDetails.getUser().getPhoneNo());
			notificationDto.setProfileImg(bookingDetails.getUser().getImageProfileLink());

			notificationDto.setUserPickupLatitude(bookingDetails.getPickupLocation().getUserLatitude());
			notificationDto.setUserPickupLogitude(bookingDetails.getPickupLocation().getUserLongitude());
			notificationDto.setUserDropLatitude(bookingDetails.getDropOffLocation().getUserLatitude());
			notificationDto.setUserDropLogitude(bookingDetails.getDropOffLocation().getUserLongitude());

			// Convert DriverNotificationDto object to JSON
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String jsonMessage = objectMapper.writeValueAsString(notificationDto);

				// Broadcast the JSON message via WebSocket
				myWebSocketHandler.broadcastMessage(jsonMessage);
			} catch (JsonProcessingException e) {
				// Handle JSON processing exception
				e.printStackTrace();
			}

			return notificationDto; // Return DTO object
		}
		return null; // Return null if booking details are not found
	}

	@Override
	public DriverNotificationDto sendNotificationToDriverForRental(Long rentalBookingId) {

		RentalBooking rentalBookingDetails = rentalBookingRepository.findById(rentalBookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found with ID: " + rentalBookingId));

		if (rentalBookingDetails != null && rentalBookingDetails.getUser() != null
				&& rentalBookingDetails.getDriver() != null) {

			DriverNotificationDto notificationDto = new DriverNotificationDto();
			notificationDto.setBookingId(rentalBookingDetails.getRentalBookingId());
			notificationDto.setDriverId(rentalBookingDetails.getDriver().getDriverId());
			notificationDto.setUserId(rentalBookingDetails.getUser().getUserId());
			notificationDto.setName(rentalBookingDetails.getUser().getName());
			notificationDto.setPhoneNumber(rentalBookingDetails.getUser().getPhoneNo());
			notificationDto.setProfileImg(rentalBookingDetails.getUser().getImageProfileLink());
			notificationDto.setUserPickupLatitude(rentalBookingDetails.getTravelLocation().getUserPickupLatitude());
			notificationDto.setUserPickupLogitude(rentalBookingDetails.getTravelLocation().getUserPickupLongitude());
			notificationDto.setUserDropLatitude(rentalBookingDetails.getTravelLocation().getUserDropLatitude());
			notificationDto.setUserDropLogitude(rentalBookingDetails.getTravelLocation().getUserDropLongitude());

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String jsonMessage = objectMapper.writeValueAsString(notificationDto);

				myWebSocketHandler.broadcastMessage(jsonMessage);

			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return notificationDto;
		}
		return null;
	}

	@Override
	public List<CourierBookingDto1> getBookingsByHubId(Long hubId) {
		List<Booking> bookings = bookingRepository.findByHub_HubId(hubId);
		return bookings.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@Override
	public List<CourierBookingDto1> getRentalBookingsByHubId(Long hubId) {
		List<RentalBooking> rentalBookings = rentalBookingRepository.findByHub_HubId(hubId);
		return rentalBookings.stream().map(this::convertToDto).collect(Collectors.toList());
	}

}
