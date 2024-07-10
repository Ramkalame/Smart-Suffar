package com.rido.utils;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.rido.entity.TimeDuration;

public class CalculateDistance {
	
	private static final double EARTH_RADIUS_KM = 6371.0;
//	 private static final double GST_RATE = 0.18;
  
  public static double distance(double lat1, double lon1, double lat2, double lon2) {
        // Earth radius in kilometers
        // Convert latitude and longitude from degrees to radians
        double radLat1 = Math.toRadians(lat1);
        double radLon1 = Math.toRadians(lon1);
        double radLat2 = Math.toRadians(lat2);
        double radLon2 = Math.toRadians(lon2);

        // Calculate the change in coordinates
        double deltaLat = radLat2 - radLat1;
        double deltaLon = radLon2 - radLon1;

        // Haversine formula
        double a = Math.pow(Math.sin(deltaLat/2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(deltaLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c; // Distance in kilometers
    }
	
//	public static double distance(double lat1, double lon1, double lat2, double lon2,  LocalDateTime startDateTime) {
//          // Earth radius in kilometers
//          // Convert latitude and longitude from degrees to radians
//      double radLat1 = Math.toRadians(lat1);
//      double radLon1 = Math.toRadians(lon1);
//      double radLat2 = Math.toRadians(lat2);
//      double radLon2 = Math.toRadians(lon2);
//
//      // Calculate the change in coordinates
//      double deltaLat = radLat2 - radLat1;
//      double deltaLon = radLon2 - radLon1;
//
//      // Haversine formula
//
//      double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(deltaLon / 2), 2);
//      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//      double distance = EARTH_RADIUS_KM * c;
//      
//    
//      return distance; // Distance in kilometers
//  }
}