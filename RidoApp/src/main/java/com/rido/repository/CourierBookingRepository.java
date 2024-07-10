package com.rido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.CourierBooking;
import com.rido.entity.enums.CourierBookingStatus;
import com.rido.entity.enums.RideOrderStatus;

@Repository
public interface CourierBookingRepository extends JpaRepository<CourierBooking, Long> {

//	 List<CourierBooking> findByCourierDriverId(Long courierId);

	List<CourierBooking> findByCourierDriver_CourierId(Long courierId);

	List<CourierBooking> findByRideOrderStatus(RideOrderStatus complete);

	List<CourierBooking> findByCourierDriver_CourierIdAndRideOrderStatus(Long courierId, RideOrderStatus complete);

//	List<CourierBooking> findByHub_HubId(Long hubId);

	Optional<CourierBooking> findByUser_UserId(Long userId);

	void deleteByIsConfirm(CourierBookingStatus bookingStatus);

	

	
	

}
