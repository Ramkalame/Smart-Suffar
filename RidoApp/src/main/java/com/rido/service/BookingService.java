package com.rido.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

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
import com.rido.entity.PaymentActivity;
import com.rido.entity.RentalBooking;
import com.rido.entity.ReturnCar;
import com.rido.entity.enums.DriverAndVehicleType;

@Service
public interface BookingService {

	public Map<String, BigDecimal> getMonthlyTotalEarningGraph();

	public Map<String, BigDecimal> getWeeklyTotalEarningGraph();

	public Map<Integer, BigDecimal> generateYearlyBookingGraph();

	public Map<String, BigDecimal> generateMonthlyBookingGraph();

	public BigDecimal getTotalAmountSum();

	public double calculatePercentageChange(long currentBookings, long previousBookings);

	public BookingComparisonDTO getBookingComparisonChart();

//    public List<WeeklyBookingDTO> getWeeklyBookingDetails();

	public BigDecimal getTotalAmountForPreviousMonth();

	public BigDecimal getTotalAmountForCurrentMonthByHub(Long hubId);

	public List<Booking> getUserPayments();

	public BigDecimal getTotalAmountForCurrentMonth();

	public List<Booking> getNewBookings(Long hubId);

	Booking calculateBooking(Booking booking, Long userId, Long hubId);

	List<Booking> getAllBookings(Long hubId);

	public List<Booking> getTodaysBookings(Long hubId);

	public List<Booking> getWeeklyBookings();

	public Map<String, Integer> generateWeeklyBookingGraph();

	public BigDecimal calculateWeeklyPayment();

	public Map<String, BigDecimal> calculateMonthlyEarningsAndExpenses(Date month);

	public Map<String, BigDecimal> calculateWeeklyEarningsAndExpenses(Date startDate, Date endDate);

	public Map<String, BigDecimal> calculateDailyEarningsAndExpenses(Date date);

	public List<Booking> getAllOrders();

//new 
	public List<BookedOrderDTO> getBookedOrders();

	public List<BookedOrderDTO> getCompletedOrders();

	public int getTotalCompletedRidesForDriverOnDate(Long driverId, LocalDate date);

	public DriverInfoDTO getDriverInfoByOrderId(Long id);

	public List<ActiveDriverInfoDto> getOngoingDriversOnDate();

	public List<IncompleteRideDto> getIncompleteRidesByDate(LocalDate date);

	public BookingResponseDto getBookingOrder(Long bookingId);

	

	RentalBookingDto bookRental(RentalBooking request, Long userId);

//	RentalBooking applyPromoCode(Long bookingId, String promoCode);

//	RentalBooking removePromoCode(Long rentalBookingId);

	public int getTotalCompletedRidesForDriverWithinDateRanges(Long driverId, LocalDate startDate, LocalDate date);

	public int getTotalCompletedRidesForDriverInWeek(Long driverId, LocalDateTime startDate, LocalDateTime endDate);

	RentalBooking getRentalBookingById(Long rentalBookingId);

	RentalBooking extendRental(Long bookingId, ExtendedRental extendedRental);

	public Booking removePromoCodeForShedule(Long bookingId);

	public Booking applyPromoCodeForShedule(Long bookingId, String promoCode);

	PaymentActivity paymentOrderCreateForExtendRentalBooking(RentalBooking rentalBooking);

//	PaymentActivity paymentOrderCreateForRentalBooking(RentalBooking rentalBooking);

//	PaymentActivity createPaymentOrderForEbikeRentalBooking(EBikeBooking eBikeBooking);

	

	public BookingResponseDto scheduleRide1(long userId, ScheduleRideRequest request);

	public BookingResponseDto scheduleRide2(long userId, ScheduleRideRequest request);

	public int getTotalCompletedRidesForDriverinweek(Long driverId, LocalDateTime startDate, LocalDateTime endDate);

	public int calculateTotalRentalPayment(Long driverId, LocalDateTime startDate, LocalDateTime endDate);

	public Map<LocalDate, Integer> calculateWorkingHours(List<ReturnCar> assignCars, List<ReturnCar> returnCars);

	public DriverAndVehicleType determineDriverType(Long driverId);

	public void saveTotalPayment(Long driverId, int totalPayment, LocalDate date);

	public int calculateTotalPayment(Map<LocalDate, Integer> workingHoursMap, 
			int totalCompletedRides, DriverAndVehicleType driverType);

	int getTotalNumberOfOrders();

	RentalBooking saveRentalBooking(RentalBooking rentalBooking);

	List<RentalBookingDto> getAllRentalBookingsList();

	

	public void deleteBookingById(long bookingId);

	RentalBooking applyPromoCode(Long rentalBookingId, String promoCode);

	RentalBooking removePromoCode(Long rentalBookingId);

	PaymentActivity paymentOrderCreateForRentalBooking(RentalBooking rentalBooking);

	void deleteNotConfirmedRentalBookings();

	public int getTotalCompletedrentalRidesForDriverinweek(Long driverId, LocalDateTime startDate,
			LocalDateTime endDate);

	public PaymentActivityResponseDto paymentOrderCreateForSheduleBooking1(Booking booking);

	String sendNotificationToDriver1(Long driverId, Booking bookingDetails);

	public UserNotificationDto saveDriverIdInBooking(Long driverId, long bookingId);

	public Map<String, String> sendVerificationCodeToUser(long bookingId);

	public List<CourierBookingDto1> getAllBookings();

	public List<CourierBookingDto1> getRentalBooking();

	public List<CourierBookingDto1> getRentalBookingsByHubId(Long hubId);

	public List<CourierBookingDto1> getBookingsByHubId(Long hubId);

	
	


	public DriverNotificationDto sendNotificationToDriver23(Long userId, Long bookingId);

	
	public UserNotificationDto sendNotificationToUser23(Long driverId, Long bookingId);

	DriverNotificationDto sendNotificationToDriverForRental(Long rentalBookingId);



}
