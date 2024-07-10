package com.rido.dto;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.rido.entity.Driver;
import com.rido.entity.Hub;
import com.rido.entity.RentalUserLocation;
import com.rido.entity.TimeDuration;
import com.rido.entity.User;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.CourierBookingStatus;
import com.rido.entity.enums.RentalPackageType;
import com.rido.entity.enums.RideOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class RentalBookingDto {

	private Long rentalBookingId;

	private RideOrderStatus rideOrderStatus;

	private TimeDuration timeDuration;

	private RentalUserLocation travelLocation;

	private String pickupAddress;

	private double userPickupLatitude;

	private double userPickupLongitude;

	private String dropAddress;

	private double userDropLatitude;

	private double userDropLongitude;

	private Driver driver;

	private String driverName;

	private User user;

	private String userName;

	private Vehicle vehicle;

	private Long userId;

	private Long vehicleId;

	private Hub hub;

	private RentalPackageType rentalPackageType;

	private String promoCode;

	private BigDecimal amount;

	private BigDecimal totalAmount;

	private int hours;

	private int distance;

	private String gst;

	private int extraHours;

	private int extraDistance;

	private BigDecimal extraAmount;

	private String note;

	private CourierBookingStatus isConfirm;

}
