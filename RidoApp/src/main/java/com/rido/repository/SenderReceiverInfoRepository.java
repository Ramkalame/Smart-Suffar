package com.rido.repository;

import com.rido.entity.Courier;
import com.rido.entity.SenderReceiverInfo;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rido.entity.Courier;
import com.rido.entity.SenderReceiverInfo;

public interface SenderReceiverInfoRepository extends JpaRepository<SenderReceiverInfo, Long> {

	Optional<Courier> findBySenderPhoneNumber(String contactNo);

	Optional<Courier> findByUser_UserId(Long userId);

//	Optional<SenderReceiverInfo> findByUserId_AndSenderPhoneNumber(Long userId);

}