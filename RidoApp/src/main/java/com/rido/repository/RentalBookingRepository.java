package com.rido.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.RentalBooking;
import com.rido.entity.User;
import com.rido.entity.enums.CourierBookingStatus;

@Repository
public interface RentalBookingRepository extends JpaRepository<RentalBooking, Long> {

	List<RentalBooking> findByHub_HubId(Long hubId);

	@Query("SELECT rb FROM RentalBooking rb WHERE rb.driver.id = :driverId AND rb.timeDuration.startDateTime >= :dayStartTime AND rb.timeDuration.endDateTime <= :dayEndTime")
	List<RentalBooking> findRentalBookingsForDriverAndDay(Long driverId, LocalDateTime dayStartTime,
			LocalDateTime dayEndTime);

	void deleteByIsConfirm(CourierBookingStatus notConfirmed);

	List<RentalBooking> findByIsConfirmAndTimeDuration_StartDateTimeBefore(CourierBookingStatus isConfirm,
			LocalDateTime startDateTime);

	@Query("SELECT rb FROM RentalBooking rb WHERE rb.driver.driverId = :driverId AND rb.rideOrderStatus = 'COMPLETE'")
	List<RentalBooking> findCompletedRidesForDriver(@Param("driverId") Long driverId);

	boolean existsByUserAndPromoCode(User user, String promoCode);

}
