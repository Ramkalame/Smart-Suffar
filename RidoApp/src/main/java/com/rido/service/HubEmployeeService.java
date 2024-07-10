package com.rido.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rido.Exceptions.EmployeePaymentNotFoundException;
import com.rido.Exceptions.HubNotFoundException;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.EmployeePaymentDto;
import com.rido.dto.HubDataDto;
import com.rido.dto.HubEmployeeDto;
import com.rido.dto.HubEmployeeProfileEditDto;
import com.rido.dto.PasswordChangeRequestDto;
import com.rido.dto.PaymentHistoryDto;
import com.rido.dto.ProfileDto;
import com.rido.entity.Booking;
import com.rido.entity.HubEmployee;
import com.rido.entityDTO.ResponseLogin;

@Service
public interface HubEmployeeService {

	public HubEmployee updateHubEmployeeProfile(Long hubId, HubDataDto hubDataDto, String s3Url,
			String signatureImageUrl, String passbookImageUrl) throws Exception;

	boolean setNewPasswordForHubEmployee(String phoneno, PasswordChangeRequestDto passwordRequest);

	HubEmployeeProfileEditDto getHubEmployeeProfile(Long hubId);

	boolean changePasswordByOldPassword(Long hubId, ChangePasswordRequestDto changepassword)
			throws HubNotFoundException;

	ResponseLogin getByPhoneno(String phoneno);

	String addEmployeeData(Long hubEmployeeId, HubEmployeeDto hubEmployee, String profileimgUrl, String empSignatureUrl,
			String passbookImgUrl);

	List<PaymentHistoryDto> getEmployeePayementHistoryByEmpId(Long empId);

	String createEmployee2Payment(Long hubEmployeeId, String amount, LocalDate date);

	List<EmployeePaymentDto> getEmployeePaymentDetailsByHubId(Long hubId);

//	EmployeePaymentDto getEmployeePaymentDetails(Long employeeId);

	Integer getHubEmployeeCount(Long hubId);

	ProfileDto getProfileByEmail(String email);

//	EmployeePaymentDto getEmployeePaymentDetails(Long hubEmployeeId, LocalDate date);

	EmployeePaymentDto getEmployeePaymentDetails(Long employeeOrderId) throws EmployeePaymentNotFoundException;

	

}
