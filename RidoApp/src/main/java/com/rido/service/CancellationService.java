package com.rido.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rido.entity.CancellationReason;

@Service
public interface CancellationService {

	
//	 public void sendCancellationMessage(User user);
	public String cancelRideAndNotifyUser(Long id, List<String> reasons);

	
	public List<CancellationReason> getAllCancellation();

	String cancelRideAndNotifyDriver(Long userId, List<String> reasons, Long driverId);


	String cancelRideAndNotifyUser(Long userId, String reason, Long driverId);





}
