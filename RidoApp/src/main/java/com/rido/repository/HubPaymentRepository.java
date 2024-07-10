package com.rido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.HubPayment;

@Repository
public interface HubPaymentRepository extends JpaRepository<HubPayment, Long> {

	List<HubPayment> findByHub_HubIdOrderByDateAsc(Long hubId);


//	List<HubEmployeePayment> findByHubEmployee_HubEmployeeId(Long empId);



	

}
