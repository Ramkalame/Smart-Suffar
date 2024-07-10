package com.rido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rido.entity.UserCourierPayment;

public interface UserCourierPaymentRepository extends JpaRepository<UserCourierPayment,Long> {

	List<UserCourierPayment> findByUser_UserId(Long userId);
}