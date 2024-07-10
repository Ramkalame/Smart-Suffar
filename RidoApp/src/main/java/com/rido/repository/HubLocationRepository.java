package com.rido.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rido.entity.Hub;
import com.rido.entity.HubLocation;

public interface HubLocationRepository extends JpaRepository<HubLocation, Long>{

//	HubLocation findByHub_HubId(Hub hub);
	HubLocation findByHub(Hub hub);

}
