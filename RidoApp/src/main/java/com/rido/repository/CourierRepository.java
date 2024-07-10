package com.rido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.Courier;
import com.rido.entity.enums.ApproveStatus;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.VehicleStatus;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {

	Optional<Courier> findByPhoneNo(String phoneNo);

	Optional<Courier> findByHub_hubIdAndCourierId(Long hubId, Long courierId);

	List<Courier> findByHub_HubId(Long hubId);

	List<Courier> findByHub_HubIdAndApproveStatusAndVehicleStatus(Long hubId, ApproveStatus approveStatus,
			VehicleStatus vehicleStatus);

	List<Courier> findByHub_HubIdAndApproveStatusAndVehicleType(Long hubId, ApproveStatus approveStatus,
			DriverAndVehicleType vehicleType);

	// AADARSH
	List<Courier> findAllByVehicleType(DriverAndVehicleType vehicleType);

	Optional<Courier> findByCourierId(Long courierId);

	boolean existsByEmail(String email);

	boolean existsByPhoneNo(String phoneNumber);

	boolean existsByOwnerName(String username);

//	List<Courier> findByHub_Id(Long hubId);

}
