package com.rido.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rido.entity.enums.CourierOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courier_orders")
public class CourierOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String orderTrackingNumber;
    private BigDecimal totalPrice;

    //(IN_PROGRESS,CONFIRMED,FAILED)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255)")
    private CourierOrderStatus orderStatus;

    @CreationTimestamp
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private UserCourierPayment payment;

    @OneToOne
    private CourierBooking courierBooking;
}