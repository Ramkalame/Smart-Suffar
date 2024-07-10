package com.rido.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rido.Exceptions.HubNotFoundException;
import com.rido.dto.HubManagerPaymentHistoryDto;
import com.rido.dto.HubPaymentRequestDto;
import com.rido.entity.HubPayment;

@Service
public interface HubPaymentService {
	
    public Double getSumOfAllAmountsOfDriverForCurrentMonthByHub(Long hubId);
	
    public Double getSumOfAllAmountsOfHubEmployeeForCurrentMonthByHub(Long hubId);
	
	public Double getSumOfAllAmountsOfHubEmployeeForCurrentMonth();
	
	public Double getSumOfAllAmountsOfDriverForCurrentMonth();

	void hubPayment(Long hubId, HubPaymentRequestDto request) throws HubNotFoundException;

	public List<HubPayment> getHubPaymentHistory();

	public void hubPaymentForAdmin(Long adminId, HubPaymentRequestDto request);


}
