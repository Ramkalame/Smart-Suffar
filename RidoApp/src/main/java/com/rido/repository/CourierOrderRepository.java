package com.rido.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rido.entity.CourierOrder;

public interface CourierOrderRepository extends JpaRepository<CourierOrder,Long> {
    CourierOrder findByOrderId(String razorpayOrderId);
}