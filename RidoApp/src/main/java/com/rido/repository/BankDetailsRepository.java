package com.rido.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rido.entity.BankDetails;

public interface BankDetailsRepository  extends JpaRepository<BankDetails, Long>{

	

//	Optional<BankDetails> findByDriverId(Long driverId);

	Optional<BankDetails> findByDriver_DriverId(Long driverId);

//	DriverDocumentResponseDto getBankDetailsByDriver_DriverId(Long driverId);

//	DriverDocumentResponseDto getBankDetailsByDriverId(Long driverId);

	
//	DriverDocumentResponseDto findByDriver_DriverId1(Long driverId);
	

	

}
