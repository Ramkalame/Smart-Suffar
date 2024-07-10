package com.rido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rido.entity.Hub;

@Repository
public interface HubRepository extends JpaRepository<Hub, Long> {

	Optional<Hub> findByHubName(String hubName);

	Hub findByEmail(String email);

//	List<Hub> findByStatus(Status available);

	Hub findByHubId(Long hubId);

	Hub findByPhoneNo(String phoneno);

	Optional<Hub> findByManagerNameAndHubName(String managerName, String hubName);

	List<Hub> findByAdmin_AdminId(Long adminId);

	List<Hub> findByAdminAdminId(Long adminId);

	boolean existsByEmail(String email);

	boolean existsByPhoneNo(String phoneNumber);

	@Query("SELECT MAX(h.hubUniqeId) FROM Hub h")
	String findMaxHubUniqueId();

}
