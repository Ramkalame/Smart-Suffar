package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class BookingComparisonDTO {
	
    private long currentDailyBookings;
    private long previousDailyBookings;
    private double dailyPercentageChange;

    private long currentWeeklyBookings;
    private long previousWeeklyBookings;
    private double weeklyPercentageChange;

    private long currentMonthlyBookings;
    private long previousMonthlyBookings;
    private double monthlyPercentageChange;
    
    private long totalBookings;
//    private BigDecimal totalBookingAmount;

}
