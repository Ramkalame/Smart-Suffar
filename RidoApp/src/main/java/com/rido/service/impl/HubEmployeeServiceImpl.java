package com.rido.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rido.Exceptions.EmployeePaymentNotFoundException;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.EmployeePaymentDto;
import com.rido.dto.HubDataDto;
import com.rido.dto.HubEmployeeDto;
import com.rido.dto.HubEmployeeProfileEditDto;
import com.rido.dto.PasswordChangeRequestDto;
import com.rido.dto.PaymentHistoryDto;
import com.rido.dto.ProfileDto;
import com.rido.entity.HubEmployee;
import com.rido.entity.HubEmployeePayment;
import com.rido.entity.HubEmployeePaymentDetails;
import com.rido.entity.ManageOtp;
import com.rido.entity.UserIdentity;
import com.rido.entity.enums.AllPaymentStatus;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.BookingRepository;
import com.rido.repository.HubEmployeePaymentDetailsRepository;
import com.rido.repository.HubEmployeePaymentRepository;
import com.rido.repository.HubEmployeeRepository;
import com.rido.repository.HubPaymentRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.UserIdentityRepository;
import com.rido.service.HubEmployeeService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class HubEmployeeServiceImpl implements HubEmployeeService {

	@Autowired
	private HubEmployeeRepository hubEmployeeRepository;
	@Autowired
	private ManageOtpRepository manageOtpRepository;

	@Autowired
	private HubPaymentRepository hubPaymentRepository;

	@Autowired
	HubEmployeePaymentRepository hubEmployeePaymentRepository;
	
	@Autowired
	private HubEmployeePaymentDetailsRepository hubEmployeePaymentDetailsRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserIdentityRepository userIdentityRepository;
	
	@Autowired
	private BookingRepository bookingRepository;

	@Override
	public HubEmployee updateHubEmployeeProfile(Long hubId, HubDataDto hubDataDto, String s3Url,
			String signatureImageUrl, String passbookImageUrl) throws Exception {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		Optional<HubEmployee> optionalHub = hubEmployeeRepository.findById(hubId);
		if (optionalHub.isPresent()) {
			HubEmployee hub = optionalHub.get();
			// Update driver's profile data
			if (hubDataDto.getName() != null && !hubDataDto.getName().isEmpty()) {
				hub.setName(hubDataDto.getName());
			}
			if (hubDataDto.getEmail() != null && !hubDataDto.getEmail().isEmpty()) {
				hub.setEmail(hubDataDto.getEmail());
			}
			if (hubDataDto.getPhoneNumber() != null && !hubDataDto.getPhoneNumber().isEmpty()) {
				hub.setPhoneNo(hubDataDto.getPhoneNumber());
			}
			if (hubDataDto.getUidNo() != null && !hubDataDto.getUidNo().isEmpty()) {
				hub.setUidNo(hubDataDto.getUidNo());
			}

			if (s3Url != null && !s3Url.isEmpty()) {
				hub.setProfileImgLink(s3Url);
			}

			if (signatureImageUrl != null && !signatureImageUrl.isEmpty()) {
				hub.setSignatuePic(signatureImageUrl);
			}

			if (passbookImageUrl != null && !passbookImageUrl.isEmpty()) {
				hub.setPassbookPic(passbookImageUrl);
			}
			// Update other profile data as needed
			// Save the updated driver entity
			return hubEmployeeRepository.save(hub);
		} else {
			throw new Exception("Hub not found with id: " + hubId);
		}

	}

	@Override
	public boolean setNewPasswordForHubEmployee(String PhoneNo, PasswordChangeRequestDto passwordRequest) {
		// TODO Auto-generated method stub
		Optional<HubEmployee> hubEmployee = hubEmployeeRepository.findByPhoneNo(PhoneNo);

		if (hubEmployee.isPresent()) {

			HubEmployee hubemployee = hubEmployee.get();
			verifyEmailOtp(hubemployee.getHubEmployeeId(), passwordRequest.getOtp());
			if (passwordRequest.getNewPassword().equals(passwordRequest.getConfirmnewPassword()))
				hubemployee.setPassword(passwordRequest.getNewPassword());

			hubEmployeeRepository.save(hubemployee);
			return true;
		}

		return false;
	}

	public boolean verifyEmailOtp(Long HubEmpId, String otp) {

		Optional<ManageOtp> manageOtp = manageOtpRepository.findByHubEmployee_hubEmployeeId(HubEmpId);

		System.out.println(manageOtp + "byemail 106");
		if (manageOtp != null) {
			ManageOtp manageOtp2 = manageOtp.get();
			String emailOtp = manageOtp2.getForgetOtp();
			if (emailOtp.equals(otp)) {

				return true;
			}

		}

		return false;

	}

	@Override
	public HubEmployeeProfileEditDto getHubEmployeeProfile(Long hubId) {
		Optional<HubEmployee> optionalHubEmployee = hubEmployeeRepository.findById(hubId);

		if (optionalHubEmployee.isPresent()) {
			HubEmployee hubEmployee = optionalHubEmployee.get();

			HubEmployeeProfileEditDto profileDto = new HubEmployeeProfileEditDto();
			profileDto.setName(hubEmployee.getName());
			profileDto.setEmail(hubEmployee.getEmail());
			profileDto.setUserName(hubEmployee.getUsername());
			profileDto.setPhoneNumber(hubEmployee.getPhoneNo());
			profileDto.setProfilePic(hubEmployee.getProfileImgLink());

			return profileDto;
		} else {
			// Handle case when no HubEmployee is found with the provided hubId
			throw new EntityNotFoundException("HubEmployee not found for hubId: " + hubId);
		}
	}

	@Override
	public boolean changePasswordByOldPassword(Long HubEmpId, ChangePasswordRequestDto changePasswordRequestDto) {
		// Retrieve user from the database
		HubEmployee HubEmp = hubEmployeeRepository.findById(HubEmpId).orElse(null);

		if (HubEmp != null
				&& passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), HubEmp.getPassword())) {
			Optional<UserIdentity> userIdentityOptional = userIdentityRepository.findByPhoneNo(HubEmp.getPhoneNo());

			if (userIdentityOptional.isPresent()) {
				UserIdentity userIdentity = userIdentityOptional.get();
				if (HubEmp.getPhoneNo().equals(userIdentity.getPhoneNo())) {
					if (changePasswordRequestDto.getNewPassword()
							.equals(changePasswordRequestDto.getConfirmPassword())) {
						String encodedPassword = passwordEncoder.encode(changePasswordRequestDto.getNewPassword());

						HubEmp.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						hubEmployeeRepository.save(HubEmp);
						userIdentityRepository.save(userIdentity);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public ResponseLogin getByPhoneno(String phoneno) {
		// TODO Auto-generated method stub
		Optional<HubEmployee> hubemp = hubEmployeeRepository.findByPhoneNo(phoneno);
		if (hubemp.isPresent()) {
			// Map User to ResponseLogin if needed
			ResponseLogin response = new ResponseLogin();
			response.setUserId(hubemp.get().getHubEmployeeId());
			response.setEmail(hubemp.get().getEmail());
			response.setPhoneNo(hubemp.get().getPhoneNo());

			return response;
		} else {
			return null;
		}
	}

	@Override
	public String addEmployeeData(Long hubEmployeeId, HubEmployeeDto hubEmployee, String profileimgUrl,
			String empSignatureUrl, String passbookImgUrl) {
		// TODO Auto-generated method stub

		Optional<HubEmployee> optionalHumEmp = hubEmployeeRepository.findById(hubEmployeeId);

		if (optionalHumEmp.isPresent()) {
			HubEmployee existingEmp = optionalHumEmp.get();

			// Update driver details
			existingEmp.setName(hubEmployee.getName());
			existingEmp.setAdharNo(hubEmployee.getAdharNo());

			existingEmp.setPanNo(hubEmployee.getPanNo());
			existingEmp.setPhoneNo(hubEmployee.getPhoneNo());

			existingEmp.setAddress(hubEmployee.getAddress());
			existingEmp.setEmpSignature(empSignatureUrl);
			existingEmp.setProfileImgLink(profileimgUrl);
			existingEmp.setPassbookImg(passbookImgUrl);
			// Save updated driver details
			HubEmployee savedHubEmp = hubEmployeeRepository.save(existingEmp);

			System.out.println(savedHubEmp);

			return "Data saved successfully";
		} else {
			// Handle the case where the driver with the given ID is not found
			throw new EntityNotFoundException("Driver with ID " + hubEmployeeId + " not found.");
		}

	}

	@Override
	public List<PaymentHistoryDto> getEmployeePayementHistoryByEmpId(Long hubEmployeeId) {
		List<HubEmployeePayment> listOfHubEmployee = hubEmployeePaymentRepository
				.findAllByHubEmployee_HubEmployeeId(hubEmployeeId);
		List<PaymentHistoryDto> paymentHistoryDtoList = new ArrayList<>();

		// Convert HubEmployeePayment objects to PaymentHistoryDto objects
		for (HubEmployeePayment payment : listOfHubEmployee) {
			PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();
			paymentHistoryDto.setAmount(payment.getAmount());
			paymentHistoryDto.setLocaldatetime(payment.getLocalDatetime());
			paymentHistoryDtoList.add(paymentHistoryDto);
		}

		return paymentHistoryDtoList;
	}

	@Override
	public String createEmployee2Payment(Long hubEmployeeId, String amount, LocalDate date) {
		try {
			// Retrieve the hub employee information from the database using the employeeId
			HubEmployee hubEmp = hubEmployeeRepository.findById(hubEmployeeId).orElse(null);
			if (hubEmp == null) {
				return "HubEmployee not found";
			}

			// Create a new hub employee payment entity
			HubEmployeePaymentDetails newPaymentDetail = new HubEmployeePaymentDetails();
			newPaymentDetail.setHubEmployee(hubEmp);
			newPaymentDetail.setDate(new Date()); // Convert LocalDate to Date
			newPaymentDetail.setAmount(amount);// setAmount(amount);

			newPaymentDetail.setAllPaymentStatus(AllPaymentStatus.PENDING);
			newPaymentDetail.setHub(hubEmp.getHub());
			// Save the hub employee payment entity to the database
			hubEmployeePaymentDetailsRepository.save(newPaymentDetail);

			return null; // Return null if the operation is successful
		} catch (Exception e) {
			e.printStackTrace(); // Log the exception
			return "Error creating payment"; // Return an error message if an exception occurs
		}
	}

	// list
	@Override
	public List<EmployeePaymentDto> getEmployeePaymentDetailsByHubId(Long hubId) {
//	    List<HubEmployeePayment> employeePayments = hubEmployeePaymentRepository.findByHub_HubId(hubId);
		List<HubEmployeePaymentDetails> employeePayments = hubEmployeePaymentDetailsRepository
				.findByHub_HubIdAndAllPaymentStatus(hubId, AllPaymentStatus.PENDING);
		return employeePayments.stream().map(this::mapToDto).collect(Collectors.toList());
	}

	private EmployeePaymentDto mapToDto(HubEmployeePaymentDetails payment) {
		EmployeePaymentDto dto = new EmployeePaymentDto();
		HubEmployee employee = payment.getHubEmployee(); // Assuming HubEmployeePayment has a field 'employee'
															// representing the relationship
		dto.setEmployeeOrderId(payment.getEmployeeOrderId());
		dto.setEmployeeName(employee.getName());
		dto.setAddress(employee.getAddress());
		dto.setDate(payment.getDate());
		dto.setTotalAmount(payment.getAmount());
		dto.setAllPaymentStatus(payment.getAllPaymentStatus());
		dto.setEmailAddress(employee.getEmail());
		dto.setPhoneNo(employee.getPhoneNo());
		dto.setHubEmployee(payment.getHubEmployee());
		dto.setHub(payment.getHub());

		return dto;
	}

//	@Override
//	public EmployeePaymentDto getEmployeePaymentDetails(Long hubEmployeeId, LocalDate date) {
//		HubEmployeePayment employeePayment = hubEmployeePaymentRepository
//				.findByHubEmployee_HubEmployeeIdAndDate(hubEmployeeId, date);
//
//		if (employeePayment == null) {
//			return null;
//		}
//		HubEmployee employee = employeePayment.getHubEmployee();
//		EmployeePaymentDto dto = new EmployeePaymentDto();
//		dto.setEmployeeName(employee.getName());
//		dto.setAddress(employee.getAddress());
//		dto.setProfileImgLink(employee.getProfileImgLink());
//		dto.setEmailAddress(employee.getEmail());
//		dto.setPhoneNo(employee.getPhoneNo());
//
////        dto.setDate(employeePayment.getDate());
//		dto.setTotalAmount(employeePayment.getAmount());
////        dto.setAllPaymentStatus(employeePayment.getAllPaymentStatus());
//		return dto;
//	}

	@Override
	public EmployeePaymentDto getEmployeePaymentDetails(Long employeeOrderId) throws EmployeePaymentNotFoundException {
		HubEmployeePaymentDetails employeePayment = hubEmployeePaymentDetailsRepository.findByEmployeeOrderId(employeeOrderId)
				.orElseThrow(() -> new EmployeePaymentNotFoundException(
						"Payment details not found for employee order ID: " + employeeOrderId));
		return mapToDto(employeePayment);
	}

	@Override
	public Integer getHubEmployeeCount(Long hubId) {
		List<HubEmployee> countemployee = hubEmployeeRepository.findByHub_HubId(hubId);
		return countemployee.size();
	}

	@Override
	public ProfileDto getProfileByEmail(String email) {
		Optional<HubEmployee> optionalHub = hubEmployeeRepository.findByEmail(email);

		System.out.println("256" + optionalHub);

		if (optionalHub.isPresent()) {
			HubEmployee hub = optionalHub.get();
			ProfileDto profileDto = new ProfileDto();
			profileDto.setName(hub.getName());
			profileDto.setProfileImgLink(hub.getProfileImgLink());
			profileDto.setEmail(hub.getEmail());
			profileDto.setId(hub.getHubEmployeeId()); // Assuming id is a String in your ProfileDto

			return profileDto;
		} else {
			throw new RuntimeException("hub employee not found with email: " + email);
		}
	}
	
	
	
}
