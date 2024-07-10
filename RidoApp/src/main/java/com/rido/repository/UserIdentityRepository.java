package com.rido.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.UserIdentity;
@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentity, Long> {

	
	   Optional<UserIdentity> findByUsername(String username);

	    Optional<UserIdentity> findByEmail(String email);

	    Optional<UserIdentity> findByPhoneNo(String phoneNumber);

	    Boolean existsByUsername(String username);

	    Boolean existsByEmail(String email);

	    Boolean existsByPhoneNo(String phoneNumber);

	    Optional<UserIdentity> findByUsernameOrEmailOrPhoneNo(String username, String email, String phoneNumber);

//		Optional<UserIdentity> findByUser_UserIdAndPhoneNo(Long userId, String phoneNumber);

//		Optional<UserIdentity> findByPhoneNo();

}
