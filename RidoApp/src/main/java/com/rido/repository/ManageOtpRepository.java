package com.rido.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.ManageOtp;

@Repository
public interface ManageOtpRepository extends JpaRepository<ManageOtp, Long> {

	 Optional<ManageOtp>  findByAdmin_AdminId(Long adminId);

	Optional<ManageOtp> findByDriver_DriverId(Long driverId);

    Optional<ManageOtp> findByUser_UserId(Long userId);
    
 
    
    Optional<ManageOtp> findByHub_HubId(Long hubId);
    
    Optional<ManageOtp>  findByHubEmployee_hubEmployeeId(Long HubEmpId);

	Optional<ManageOtp> findByCourier_CourierId(Long courierId);
	
	

	
    

}
