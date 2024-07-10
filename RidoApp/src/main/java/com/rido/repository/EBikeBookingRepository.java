package com.rido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rido.entity.EBike;
import com.rido.entity.EBikeBooking;
import com.rido.entity.enums.VehicleStatus;
@Repository
public interface EBikeBookingRepository extends JpaRepository<EBikeBooking, Long> {

	 

//	 @Query("SELECT e FROM EBike e WHERE e.hub.hubId = :hubId AND e.vehicleStatus = :status")
//	List<EBike> findByHubIdAndVehicleStatus(Long hubId, VehicleStatus available);
//	 
	 @Query("SELECT e FROM EBike e WHERE e.hub.hubId = :hubId AND e.vehicleStatus = :vehicleStatus")
	    List<EBike> findByHubIdAndVehicleStatus(Long hubId, VehicleStatus vehicleStatus);
    
//	@Query("SELECT eB FROM EBikeBooking eB WHERE e.hub.hubId=:hubId ") 
//	Optional<User> findByHubHubId(@Param("hubId")Long hubId);
//
//	

	//List<EBikeBooking> findBookingsByHubId(Long hubId);
	
	 @Query("SELECT e FROM EBikeBooking e WHERE e.hub.hubId = :hubId")
	    List<EBikeBooking> findBookingsByHubId(Long hubId);

	//EBike findByBookingId(Long bookingId);
	}
	


