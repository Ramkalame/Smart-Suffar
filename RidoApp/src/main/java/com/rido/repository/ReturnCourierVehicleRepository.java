package com.rido.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.ReturnCar;
import com.rido.entity.ReturnCourierVehicle;
@Repository
public interface ReturnCourierVehicleRepository extends JpaRepository<ReturnCourierVehicle, Long>{

	ReturnCourierVehicle findByCourier_CourierId(Long courierId);

	
	 @Query("SELECT rcv FROM ReturnCourierVehicle rcv " +
	           "WHERE rcv.courier.id = :courierId " +
	           "AND rcv.carAssignTime BETWEEN :startDate AND :endDate")
	    List<ReturnCourierVehicle> findByCourierIdAndAssignTimeBetween(
	            @Param("courierId") Long courierId,
	            @Param("startDate") LocalDateTime startDate,
	            @Param("endDate") LocalDateTime endDate);
	 
	 
	 @Query("SELECT rcv FROM ReturnCourierVehicle rcv " +
	           "WHERE rcv.courier.id = :courierId " +
	           "AND rcv.carReturnTime BETWEEN :startDate AND :endDate")
	    List<ReturnCourierVehicle> findByCourierIdAndReturnTimeBetween(
	            @Param("courierId") Long courierId,
	            @Param("startDate") LocalDateTime startDate,
	            @Param("endDate") LocalDateTime endDate);

}
