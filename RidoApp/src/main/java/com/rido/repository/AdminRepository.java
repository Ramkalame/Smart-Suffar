package com.rido.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rido.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

	public Admin findByEmail(String email);

//	public Admin findByAdminId(Long adminId);

	Optional<Admin> findByUsername(String username);

//	Optional<Admin> findByEmail(String email);

	Optional<Admin> findByPhoneNo(String phoneNo);

	Optional<Admin> findByAdminUniqeId(String adminUniqeId);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Boolean existsByPhoneNo(String phoneNo);

	Optional<Admin> findByUsernameOrEmailOrPhoneNo(String username, String email, String phoneNo);

	Optional<Admin> findByAdminId(Long adminId);

	@Query("SELECT MAX(a.adminUniqeId) FROM Admin a")
	String findMaxAdminUniqueId();

}
