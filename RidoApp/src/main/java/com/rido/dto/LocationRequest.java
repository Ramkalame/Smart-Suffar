package com.rido.dto;

import org.springframework.stereotype.Component;

@Component
public class LocationRequest {

    private String currentLocation;
    private String destinationLocation;
	public String getCurrentLocation() {
		return currentLocation;
	}
	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}
	public String getDestinationLocation() {
		return destinationLocation;
	}
	public void setDestinationLocation(String destinationLocation) {
		this.destinationLocation = destinationLocation;
	}

    // getters and setters
    
    
}
