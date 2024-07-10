package com.rido.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rido.entity.CourierEbikedriverPayment;
import com.rido.entity.DriverPaymentDetail;
@Repository
public interface CourierEbikedriverPaymentRepository extends JpaRepository<CourierEbikedriverPayment, Long> {

	//List<DriverPaymentDetail> findByHubIdAndDate(Long hubId, LocalDate date);
	
	@Query("SELECT cedp FROM CourierEbikedriverPayment cedp WHERE cedp.hub.hubId = :hubId AND cedp.date = :date")
    List<DriverPaymentDetail> findByHubIdAndDate(@Param("hubId") Long hubId, @Param("date") LocalDate date);


}
