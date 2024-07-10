package com.rido.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rido.entity.User;
import com.rido.entity.UserLocation;

public interface UserLocationRepository  extends JpaRepository<UserLocation, Long> {

	UserLocation findByUserIdAndUserLatitudeAndUserLongitude(User user, double lat1, double lon1);

}
