package com.rido.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rido.dto.DriverPaymentDetailDto;
import com.rido.entity.DriverPaymentDetail;

@Service
public interface DriverPaymentDetailService {

	// String createPayment(Long driverId, Double perRideAmount,int
	// totalCompletedRides);

//	String createPayment(Long driverId,  int totalCompletedRides,
//			DriverPaymentDetailDto paymentDetailDto);

	// String createPayment(Long driverId, double totalCompletedRides,
	// DriverPaymentDetail paymentDetail);

	List<DriverPaymentDetailDto> getAllDriverPaymentDetails();

	// DriverPaymentDetailDto getDriverProfile(Long driverId);

	// String createPayment(Long driverId, int totalCompletedRides);

	List<DriverPaymentDetailDto> getDriverPaymentDetailsByHubId(Long hubId);

	public String getTotalAmountByDriverId(Long driverId);

	DriverPaymentDetailDto getDriverProfile(Long driverId);

	String createPaymentForHub(Long hubId, Long driverId, double totalCompletedRides, LocalDate date
			);

	DriverPaymentDetail getDriverPaymentDetailById(Long driverPaymentDetailId);

	List<DriverPaymentDetailDto> getAllDriverPaymentDetailsByHubId(Long hubId);

	Map<String, Object> calculateDriverPayment(Long driverId, LocalDate date);

	List<DriverPaymentDetail> getPaymentsByHubAndDate(Long hubId, LocalDate date);

	List<DriverPaymentDetail> getPendingPayments();

	Optional<DriverPaymentDetail> getPendingDriverPaymentDetailById(Long driverPaymentDetailId);

	List<DriverPaymentDetail> getcourierebikePaymentsByHubAndDate(Long hubId, LocalDate date);

	

}