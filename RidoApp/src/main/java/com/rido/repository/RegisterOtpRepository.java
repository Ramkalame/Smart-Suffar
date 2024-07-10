package com.rido.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.RegisterOtp;

@Repository
public interface RegisterOtpRepository extends JpaRepository<RegisterOtp, Long> {

	Optional<RegisterOtp> findByPhoneNo(String phoneNo);

	Optional<RegisterOtp> findByEmail(String email);

}