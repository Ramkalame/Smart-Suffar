package com.rido.entity;

import com.rido.entity.enums.DriverAndVehicleType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sender_receiver_info")
public class SenderReceiverInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String senderName;
    private Double senderLatitude;
    private Double SenderLongitude;
    private String senderLocation;
    private String senderAddress;
    private String senderPhoneNumber;
    private String receiverName;
    private Double receiverLatitude;
    private Double receiverLongitude;
    private String receiverLocation;
    private String receiverAddress;
    private String receiverPhoneNumber;
//    private String vehicleType; 
    
    @Enumerated(EnumType.STRING)
	private DriverAndVehicleType vehicleType;
    private double totalDistance;
    private String expectedTime;

    @ManyToOne
    private User user;

    @OneToOne
    private CourierBooking courierBooking;

    @ManyToOne
    private Courier courier;
}