package com.rido.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.RentalUserLocation;

@Repository
public interface RentalUserLocationRepository extends JpaRepository<RentalUserLocation, Long> {

}
