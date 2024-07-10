package com.rido.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.CarRepairDetailCost;

@Repository
public interface CarRepairDetailCostRepository extends JpaRepository<CarRepairDetailCost, Long> {

	@Query("SELECT FUNCTION('MONTH', c.dateOfCarRepaired), SUM(c.totalCostOfRepairing) FROM CarRepairDetailCost c WHERE FUNCTION('YEAR', c.dateOfCarRepaired) = :year GROUP BY FUNCTION('MONTH', c.dateOfCarRepaired)")
	List<Object[]> findMonthlySumsForCarRepair(int year);

	@Query("SELECT COALESCE(SUM(c.totalCostOfRepairing), 0) FROM CarRepairDetailCost c WHERE FUNCTION('MONTH', c.dateOfCarRepaired) = FUNCTION('MONTH', CURRENT_DATE()) AND FUNCTION('YEAR', c.dateOfCarRepaired) = FUNCTION('YEAR', CURRENT_DATE())")
	BigDecimal getTotalCostOfRepairingForCurrentMonth();

	@Query("SELECT COALESCE(SUM(c.totalCostOfRepairing), 0) FROM CarRepairDetailCost c WHERE FUNCTION('MONTH', c.dateOfCarRepaired) = FUNCTION('MONTH', CURRENT_DATE()) AND FUNCTION('YEAR', c.dateOfCarRepaired) = FUNCTION('YEAR', CURRENT_DATE()) AND c.hub.hubId = :hubId")
	BigDecimal getTotalCostOfRepairingForCurrentMonthByHub(@Param("hubId") Long hubId);

}
