package com.rido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.HubEmployeePayment;

@Repository
public interface HubEmployeePaymentRepository extends JpaRepository<HubEmployeePayment, Long> {

	@Query("SELECT FUNCTION('MONTH', p.localDatetime), SUM(p.amount) / 100 FROM FROM HubEmployeePayment p WHERE FUNCTION('YEAR', p.localDatetime) = :year GROUP BY FUNCTION('MONTH', p.localDatetime)")
	List<Object[]> findMonthlySumsForHubEmployee(int year);

	@Query("SELECT SUM(p.amount) / 100 FROM HubEmployeePayment p WHERE MONTH(p.localDatetime) = MONTH(CURRENT_DATE())")
	Double getSumOfAmountsForCurrentMonth();

	@Query("SELECT SUM(p.amount) / 100 FROM HubEmployeePayment p WHERE MONTH(p.localDatetime) = MONTH(CURRENT_DATE()) AND p.hub.hubId = :hubId")
	Double getSumOfAmountsForCurrentMonthByHub(@Param("hubId") Long hubId);

//	List<HubEmployeePayment> findByHub_HubId(Long hubid);

//	List<HubEmployeePayment> findByHubEmployeeId_HubEmployeeId(Long hubEmployeeId);

	List<HubEmployeePayment> findByHub_HubId(Long hubId);

//	HubEmployeePayment findByHubEmployee_HubEmployeeId(Long hubEmployeeId);

//	List<HubEmployeePayment> findByHubEmployee_HubEmployeeId(Long hubEmployeeId);

	List<HubEmployeePayment> findAllByHubEmployee_HubEmployeeId(Long hubEmployeeId);

//	HubEmployeePayment findByHubEmployee_HubEmployeeIdAndDate(Long hubEmployeeId, LocalDate date);

//	List<HubEmployeePayment> findByHub_HubIdAndAllPaymentStatus(Long hubId, AllPaymentStatus allPaymentStatus);

	Optional<HubEmployeePayment> findByEmployeeOrderId(Long employeeOrderId);

	HubEmployeePayment findByOrderId(String orderId);

}
