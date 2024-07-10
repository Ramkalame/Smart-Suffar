package com.rido.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rido.Exceptions.HubNotFoundException;
import com.rido.dto.HubPaymentRequestDto;
import com.rido.entity.Hub;
import com.rido.entity.HubPayment;
import com.rido.repository.DriverPaymentRepository;
import com.rido.repository.HubEmployeePaymentRepository;
import com.rido.repository.HubPaymentRepository;
import com.rido.repository.HubRepository;
import com.rido.service.HubPaymentService;

@Service
public class HubPaymentServiceImpl implements HubPaymentService {

    @Autowired
    private HubRepository hubRepository;

    @Autowired
    private HubPaymentRepository hubPaymentRepository;
    
    @Autowired
    private HubEmployeePaymentRepository hubEmployeePaymentRepository;
    
    @Autowired
    private DriverPaymentRepository driverPaymentRepository;

    @Override
    public void hubPayment(Long hubId, HubPaymentRequestDto request) throws HubNotFoundException {
        Optional<Hub> optionalHub = hubRepository.findById(hubId);
        if (optionalHub.isPresent()) {
            Hub hub = optionalHub.get();
            HubPayment hubPayment = new HubPayment();
            hubPayment.setHub(hub);
            hubPayment.setManagerName(hub.getManagerName());
            hubPayment.setPhoneNo(hub.getPhoneNo());
//            hubPayment.setHubName(hub.getHubName());
            hubPayment.setAmount(request.getAmount());
            hubPayment.setDate(LocalDateTime.now());
            hubPayment.setStatus("Paid");
            hubPaymentRepository.save(hubPayment);
        } else {
            throw new HubNotFoundException("Hub with id " + hubId + " not found.");
        }
    }

	@Override
	public List<HubPayment> getHubPaymentHistory() {
		// TODO Auto-generated method stub
		return hubPaymentRepository.findAll();
	}
	
    @Override
    public Double getSumOfAllAmountsOfHubEmployeeForCurrentMonth() {
        return hubEmployeePaymentRepository.getSumOfAmountsForCurrentMonth();
    }
    @Override
    public Double getSumOfAllAmountsOfHubEmployeeForCurrentMonthByHub(Long hubId) {
        return hubEmployeePaymentRepository.getSumOfAmountsForCurrentMonthByHub(hubId);
    }
    @Override
    public Double getSumOfAllAmountsOfDriverForCurrentMonth() {
        return driverPaymentRepository.getSumOfAmountsForCurrentMonth();
    }
    @Override
    public Double getSumOfAllAmountsOfDriverForCurrentMonthByHub(Long hubId) {
        return driverPaymentRepository.getSumOfAmountsForCurrentMonthByHub(hubId);
    }

	@Override
	public void hubPaymentForAdmin(Long adminId, HubPaymentRequestDto request) {
        List<Hub> hubs = hubRepository.findByAdminAdminId(adminId);
        for (Hub hub : hubs) {
            HubPayment hubPayment = new HubPayment();
            hubPayment.setHub(hub);
            hubPayment.setManagerName(hub.getManagerName());
            hubPayment.setPhoneNo(hub.getPhoneNo());
            hubPayment.setAmount(request.getAmount());
            hubPayment.setDate(LocalDateTime.now());
            hubPayment.setStatus("Paid");
            hubPaymentRepository.save(hubPayment);
        }
    }
	}
	


