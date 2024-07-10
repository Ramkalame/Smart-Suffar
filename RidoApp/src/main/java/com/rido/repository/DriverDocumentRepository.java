package com.rido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rido.entity.Driver;
import com.rido.entity.DriverDocument;

public interface DriverDocumentRepository extends JpaRepository<DriverDocument, Long> {

	
	@Query("SELECT d FROM DriverDocument d JOIN FETCH d.driver WHERE d.driver.id = :driverId")
	DriverDocument findByDriverId(Long driverId);

	List<DriverDocument> findByDriver(Driver driver);

	//HUB
    DriverDocument getDriverDocumentByDriver_DriverId(Long driverId);

}
