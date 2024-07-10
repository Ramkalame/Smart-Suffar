package com.rido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.CarRepair;
import com.rido.entity.enums.CarRepairStatus;
import com.rido.entity.enums.MaintenanceApprovalStatus;

@Repository
public interface CarRepairRepository extends JpaRepository<CarRepair, Long> {

	List<CarRepair> findByMaintenanceApprovalStatus(MaintenanceApprovalStatus status);

	CarRepair findByCarRepairId(Long carRepairId);

	List<CarRepair> findByMessageNotNull();

	List<CarRepair> findByHub_HubId(Long hubId);

	List<CarRepair> findByCarRepairStatus(CarRepairStatus pending);

}