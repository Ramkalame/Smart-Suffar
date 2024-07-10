package com.rido.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNo(String phoneNumber);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByPhoneNo(String phoneNumber);

    Optional<User> findByUsernameOrEmailOrPhoneNo(String username, String email, String phoneNumber);

	Optional<User> findByUserId(Long userId);
   
//    Boolean existsByPhoneNo(String phoneNumber);
    
//    Optional<User> findByUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);
    
}
