package com.rido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.PaymentActivity;
import com.rido.entity.User;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentActivity, Long> {

	PaymentActivity findByOrderId(String orderId);

	PaymentActivity findByMyOrderId(Long orderId);

	List<PaymentActivity> findByDriver_DriverId(Long driverId);

	Optional<PaymentActivity> findByUser_UserId(Long customerId);

	User findByUser_UserId(User customerId);

	PaymentActivity findByRentalBookingRentalBookingId(Long rentalBookingId);

	PaymentActivity findByBookingBookingId(Long bookingId);

}
