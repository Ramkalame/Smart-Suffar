package com.rido.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.ReturnCar;
import com.rido.entity.ReturnCar.CarCondition;

@Repository
public interface ReturnCarRepository extends JpaRepository<ReturnCar, Long> {

	List<ReturnCar> findByHub_HubId(Long hubId);

	List<ReturnCar> findByCarCondition(CarCondition worst);

	
	@Query("SELECT rc FROM ReturnCar rc WHERE rc.driver.driverId = :driverId AND rc.returnTime BETWEEN :startDate AND :endDate")
    List<ReturnCar> findByDriverIdAndReturnTimeBetween(@Param("driverId") Long driverId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

//	List<ReturnCar> findByDriverIdAndAssignTimeBetween(Long driverId, LocalDate startDate, LocalDate endDate);
	Optional<ReturnCar> findByDriver_DriverIdAndAssignTimeBetween(Long driverId, LocalDateTime startDate,
			LocalDateTime endDate);

	@Query("SELECT rc FROM ReturnCar rc WHERE rc.driver.driverId = :driverId AND rc.assignTime BETWEEN :startDate AND :endDate")
	List<ReturnCar> findByDriverIdAndAssignTimeBetween(@Param("driverId") Long driverId,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);


	

}
