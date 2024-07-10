package com.rido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rido.entity.Hub;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.VehicleAssignStatus;
import com.rido.entity.enums.VehicleStatus;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	Vehicle findByVehicleName(String vehicleName);

	public Vehicle findByVehicleNo(String object);

	List<Vehicle> findByVehicleStatus(VehicleStatus vehicleStatus);

	List<Vehicle> findByHub(Hub hub);

	//
	List<Vehicle> findByVehicleStatus(VehicleAssignStatus checkin);

	List<Vehicle> findByHub_HubId(Long hubId);


	List<Vehicle> findByHub_HubIdAndVehicleType(Long hubId, DriverAndVehicleType twoWheeler);

	List<Vehicle> findByAdmin_AdminId(Long adminId);

	List<Vehicle> findByAdminAdminIdAndHubIsNotNullAndVehicleStatus(Long adminId, VehicleStatus available);

	List<Vehicle> findByAdminAdminIdAndHubIsNotNull(Long adminId);
   
}
