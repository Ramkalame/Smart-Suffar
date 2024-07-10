package com.rido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.HubEmployeePaymentDetails;
import com.rido.entity.enums.AllPaymentStatus;

@Repository
public interface HubEmployeePaymentDetailsRepository extends JpaRepository<HubEmployeePaymentDetails, Long> {

	List<HubEmployeePaymentDetails> findByHub_HubIdAndAllPaymentStatus(Long hubId, AllPaymentStatus allPaymentStatus);

	Optional<HubEmployeePaymentDetails> findByEmployeeOrderId(Long employeeOrderId);

}
