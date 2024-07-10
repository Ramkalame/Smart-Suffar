package com.rido.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rido.entity.enums.CourierPaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_courier_payments")
public class UserCourierPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentId;
    private BigDecimal amount;
    private String description;

    //(SUCCESSFUL,FAILED)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255)")
    private CourierPaymentStatus status;

    @CreationTimestamp
    private LocalDateTime localDateTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    private CourierOrder courierOrder;

    @ManyToOne
    private User user;
}