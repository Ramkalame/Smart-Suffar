package com.rido.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.AssignCar;

@Repository
public interface AssignCarRepository extends JpaRepository<AssignCar, Long> {

	@Query("SELECT ac FROM AssignCar ac WHERE ac.driverId.driverId = :driverId AND ac.openingTime BETWEEN :startDate AND :endDate")
    List<AssignCar> findByDriverIdAndOpeningTimeBetween(
            @Param("driverId") Long driverId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}