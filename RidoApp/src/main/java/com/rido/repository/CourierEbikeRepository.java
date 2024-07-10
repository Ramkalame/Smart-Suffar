package com.rido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.CourierEbike;
import com.rido.entity.enums.VehicleStatus;
@Repository
public interface CourierEbikeRepository extends JpaRepository<CourierEbike, Long> {

	List<CourierEbike> findByHub_HubId(Long hubId);

	List<CourierEbike> findAllByVehicleStatus(VehicleStatus vehicleStatus);


	@Query("SELECT e FROM CourierEbike e WHERE e.admin.adminId = :adminId AND e.hub IS NOT NULL")
    List<CourierEbike> findByAdminIdAndHubIsNotNull(Long adminId);
//	Optional<CourierEbike> findByIdAndHub_HubId(Long id, Long hubId);
    
	@Query("SELECT e FROM CourierEbike e WHERE e.admin.adminId = :adminId AND e.hub IS NULL")
	List<CourierEbike> findByAdminIdAndHubIstNull(Long adminId);

	CourierEbike findByVehicleNo(String vehicleNo);

	CourierEbike findByAdmin_AdminId(Long adminId);

	List<CourierEbike> findByAdminAdminIdAndHubIsNotNull(Long adminId);

}
