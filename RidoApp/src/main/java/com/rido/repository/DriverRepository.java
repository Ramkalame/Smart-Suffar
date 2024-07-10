package com.rido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.Driver;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.Status;
import com.rido.entity.enums.VehicleAssignStatus;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

	Driver findByName(String name);

	List<Driver> findByStatus(Status status);

	Driver findByPhoneNo(String phoneNo);

//	boolean existsByPhoneNo(String phoneNo);

//	List<Driver> findAllByVehicleAssignStatus(VehicleAssignStatus checkout);

	Optional<Driver> findByUsername(String username);

	Optional<Driver> findByEmail(String email);

//    Optional<Driver> findByPhoneNo(String phoneNo);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Boolean existsByPhoneNo(String phoneNo);

	Optional<Driver> findByUsernameOrEmailOrPhoneNo(String username, String email, String phoneNo);

	List<Driver> findByVehicleAssignStatus(VehicleAssignStatus checkin);

	List<Driver> findByHub_HubId(Long hubId);

//new
	List<Driver> findByHub_HubIdAndVehicleAssignStatus(Long hubId, VehicleAssignStatus checkin);

	List<Driver> findByDriverTypeAndStatus(DriverAndVehicleType driverType, Status available);

	@Query("SELECT d FROM Driver d JOIN d.hub h WHERE h.admin.adminId = :adminId AND d.hub.hubId = :hubId")
	List<Driver> findByAdminIdAndHubId(@Param("adminId") Long adminId, @Param("hubId") Long hubId);

	List<Driver> findByHubHubId(Long hubId);
	
	

//    Boolean existsByPhoneNumber(String phoneNumber);

//    Optional<Driver> findByUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);

}
