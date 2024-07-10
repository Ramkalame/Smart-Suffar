package com.rido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.HubEmployee;

@Repository
public interface HubEmployeeRepository extends JpaRepository<HubEmployee, Long> {

	Optional<HubEmployee>  findByPhoneNo(String phoneNo);

	List<HubEmployee> findByHub_HubId(Long hubId);
	
	  Optional<HubEmployee> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByPhoneNo(String phoneNumber);


}
