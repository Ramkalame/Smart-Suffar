package com.rido.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.dto.DriverPaymentDetailDto;
import com.rido.entity.DriverPaymentDetail;
import com.rido.entity.DriverPaymentDetail.Status;

@Repository
public interface DriverPaymentDetailRepository extends JpaRepository<DriverPaymentDetail, Long> {

	@Query("SELECT dpd FROM DriverPaymentDetail dpd " + "WHERE dpd.driver.driverId = :driverId "
			+ "AND dpd.hub.hubId = :hubId " + "AND dpd.date = :today")
	List<DriverPaymentDetail> findByDriverIdAndHubIdAndDate(@Param("driverId") Long driverId,
			@Param("hubId") Long hubId, @Param("today") LocalDate today);

	void save(DriverPaymentDetailDto paymentDetail);

	@Query("SELECT SUM(d.amount) FROM DriverPaymentDetail d WHERE d.driver.id = :driverId")
	String getTotalAmountByDriverId(Long driverId);

	List<DriverPaymentDetail> findByHub_HubId(Long hubId);

	@Query("SELECT dpd FROM DriverPaymentDetail dpd WHERE dpd.hub.hubId = :hubId AND dpd.date = :date")
    List<DriverPaymentDetail> findByHubIdAndDate(@Param("hubId") Long hubId, @Param("date") LocalDate date);

	List<DriverPaymentDetail> findByStatus(Status status);

	@Query("SELECT d FROM DriverPaymentDetail d WHERE d.hub.hubId = :hubId AND d.status = 'PENDING'")
    List<DriverPaymentDetail> findPendingPaymentsByHubId(@Param("hubId") Long hubId);

	@Query("SELECT d FROM DriverPaymentDetail d WHERE d.driverPaymentDetailId = :driverPaymentDetailId AND d.status = :status")
    Optional<DriverPaymentDetail> findByDriverPaymentDetailIdAndStatus(
            @Param("driverPaymentDetailId") Long driverPaymentDetailId,
            @Param("status") Status status);

	@Query("SELECT d FROM DriverPaymentDetail d WHERE d.hub.hubId = :hubId AND d.driver.driverId = :driverId")
    Optional<DriverPaymentDetail> findByHubIdAndDriverId(@Param("hubId") Long hubId, @Param("driverId") Long driverId);


}
