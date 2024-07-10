package com.rido.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.Booking;
import com.rido.entity.enums.RideOrderStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Query("SELECT b FROM Booking b WHERE YEAR(b.timeDuration.startDateTime) = :year")
	List<Booking> findAllByYear(int year);

	@Query("SELECT b FROM Booking b WHERE b.timeDuration.startDateTime BETWEEN :start AND :end")
	List<Booking> findAllWithStartTimeBetween(LocalDateTime start, LocalDateTime end);

	@Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE MONTH(b.timeDuration.startDateTime) = MONTH(CURRENT_DATE()) - 1")
	BigDecimal getTotalAmountForPreviousMonth();

	@Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE MONTH(b.timeDuration.startDateTime) = MONTH(CURRENT_DATE())")
	BigDecimal getTotalAmountForCurrentMonth();

	@Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE MONTH(b.timeDuration.startDateTime) = MONTH(CURRENT_DATE()) AND b.hub.hubId = :hubId")
	BigDecimal getTotalAmountForCurrentMonthByHub(@Param("hubId") Long hubId);

	long countByHub_HubIdAndDriverIsNull(Long hubId);

	List<Booking> findByTimeDurationStartDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

	List<Booking> findByHub_HubIdAndTimeDurationStartDateTimeBetween(Long hubId, LocalDateTime startOfDay,
			LocalDateTime endOfDay);

	List<Booking> findByHub_HubId(Long hubId);

	List<Booking> findByHub_HubIdAndDriverIsNull(Long hubId);

	@Query("SELECT COUNT(b) FROM Booking b WHERE DATE(b.timeDuration.startDateTime) = CURRENT_DATE")
	int getCurrentDailyBookings();

	@Query("SELECT COUNT(b) FROM Booking b WHERE DATE(b.timeDuration.startDateTime) = :previousDate")
	int getPreviousDailyBookings(LocalDate previousDate);

	@Query("SELECT COUNT(b) FROM Booking b WHERE YEARWEEK(b.timeDuration.startDateTime) = YEARWEEK(CURRENT_DATE)")
	int getCurrentWeeklyBookings();

	@Query("SELECT COUNT(b) FROM Booking b WHERE YEARWEEK(b.timeDuration.startDateTime) = YEARWEEK(:previousWeek)")
	int getPreviousWeeklyBookings(LocalDate previousWeek);

	@Query("SELECT COUNT(b) FROM Booking b WHERE MONTH(b.timeDuration.startDateTime) = MONTH(CURRENT_DATE)")
	int getCurrentMonthlyBookings();

	@Query("SELECT COUNT(b) FROM Booking b WHERE MONTH(b.timeDuration.startDateTime) = MONTH(CURRENT_DATE()) - 1")
	int getPreviousMonthlyBookings();

	Booking findByUser_UserId(Long userId);

	List<Booking> findByTimeDurationStartDateTimeBetween(Date startDate, Date endDate);

	// List<Booking> findByRideOrderStatus(RideOrderStatus complete) ;
//new
	@Query("SELECT o FROM Booking o WHERE o.driver.driverId = :driverId AND o.rideOrderStatus = RideOrderStatus.COMPLETE")
	List<Booking> findCompletedRidesForDriver(@Param("driverId") Long driverId);

	// List<Booking> findByRideOrderStatus(RideOrderStatus inComplete);
	List<Booking> findByRideOrderStatus(RideOrderStatus rideOrderStatus);

	List<Booking> findByHubEmployee_hubEmployeeIdAndTimeDurationStartDateTimeBetween(Long hubEmpId,
			LocalDateTime startOfDay, LocalDateTime endOfDay);

	Booking findByBookingId(long bookingId);

	List<Booking> findByDriver_DriverId(Long driverId);

	Booking findByUser_UserIdAndDriver_DriverId(Long userId, Long driverId);

	Booking findByUser_UserIdAndBookingId(Long userId, Long bookingId);

	Long countByDriver_DriverId(Long driverId);

}
