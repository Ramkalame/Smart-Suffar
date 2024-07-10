package com.rido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.DriverPayment;

@Repository
public interface DriverPaymentRepository extends JpaRepository<DriverPayment, Long> {

	@Query("SELECT FUNCTION('MONTH', p.localDatetime), SUM(p.amount) / 100 FROM FROM DriverPayment p WHERE FUNCTION('YEAR', p.localDatetime) = :year GROUP BY FUNCTION('MONTH', p.localDatetime)")
	List<Object[]> findMonthlySumsForDriver(int year);

	@Query("SELECT SUM(p.amount) / 100 FROM DriverPayment p WHERE MONTH(p.localDatetime) = MONTH(CURRENT_DATE())")
	Double getSumOfAmountsForCurrentMonth();

	@Query("SELECT SUM(p.amount) / 100 FROM DriverPayment p WHERE MONTH(p.localDatetime) = MONTH(CURRENT_DATE()) AND p.hub.hubId = :hubId")
	Double getSumOfAmountsForCurrentMonthByHub(@Param("hubId") Long hubId);

	DriverPayment findByOrderId(String orderId);

}
