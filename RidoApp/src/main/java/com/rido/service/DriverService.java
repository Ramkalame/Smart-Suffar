package com.rido.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rido.Exceptions.UserNotFoundException;
import com.rido.dto.CarMaintenanceDto;
import com.rido.dto.CarRepairDto;
import com.rido.dto.DriverChangePasswordRequestDto;
import com.rido.dto.DriverDataDto;
import com.rido.dto.DriverDocumentResponseDto;
import com.rido.dto.HubAddressDTO;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.PaymentHistoryDto;
import com.rido.entity.Driver;
import com.rido.entity.Hub;
import com.rido.entity.ReturnCar.CarCondition;
import com.rido.entityDTO.ResponseLogin;

@Service
public interface DriverService {

	public List<Driver> getAvailableDrivers();

//	RWI120
	public String acceptRideRequest(Long userId, Long driverId);

	public boolean verifySmsOtpDriver(String contactNo, String smsOtp, Long driverId);

	public boolean driverChangePassword(Long driverId, DriverChangePasswordRequestDto driverChangePasswordRequestDto);

	Driver editCabDriverProfile(Driver driver) throws UserNotFoundException;

	Driver findById(Long id);

	public String forgetPassword(String phoneNo);

	// get profile
	public Driver getDriverById(Long id);

	public void addDriverDocument(Long driverId, String driverPhotoUrl, String dLUrl, String driverSignatureUrl,
			String adharCardUrl, String dpassbookUrl);

	public Driver signWithPhoneDriver(Driver driver);

	public Driver authenticate(String phoneNo, String password);

	public List<Driver> findOngoingDriver();


	public ResponseLogin getByPhoneno(String phoneno);

	public boolean setNewPassword(Long driverId, PasswordRequestDto passwordDto);

// update bank details
	public String updateDriverBankDetails(Long driverId, Long accountNo, String IFSCcode, String accountHolderName, String branchName);

	// public DriverDocument getDriverDocument(Long driverId);

	public boolean verifySmsOtp(Long driverId, String otp);

	public boolean forgetPasswordVerify(Long driverId, String forgetOtp);

	// public Driver updatedriver(Long driverId, DriverDataDto driverDataDto,
	// MultipartFile file) throws IOException;

	public String addVerifyDriverDocument(Long driverId, DriverDataDto driverDataDto, String s3Url) throws IOException;

	DriverDocumentResponseDto getBankDetailsByDriverId(Long driverId);

	public String getVehicleNumberByDriverId(Long driverId);

	

	public Driver updateDriverProfile(Long driverId, DriverDataDto driverDataDto, String s3Url) throws Exception;

	

	public List<HubAddressDTO> findAllByDriverId(Long driverId);

	// public String returnCar(Long driverId, LocalDateTime returnTime, CarCondition
	// carCondition, String message);

//	public String changeVehicle(Long driverId, LocalDateTime returnTime, String reason);

	// public String changeVehicle(Long driverId, LocalDateTime returnTime, String
	// reason);

	// public List<CarChangeDto> getAllDriverDetailsWithChangeReason();

	public String returnCar(Long driverId, CarCondition carCondition, String message);

	public String repairVehicle(Long driverId, String reason);

	public void changePhoneNo(Long driverId, String newPhoneNo);

	public List<CarRepairDto> findDriverListsWithChangeReasonsByHubId(Long hubId);

	public List<CarRepairDto> findDriverListwithReturnConditionByHubId(Long hubId);


//	=============================================

	public List<Driver> findNearbyDrivers(double latitude, double longitude, double radius);

	public List<Driver> findDriversNearHub(Hub hub, double radius);

//	public List<Driver> assignDriversToHub(Long hubId, double latitude, double longitude, int i);
	public List<Driver> assignDriversToHub(Long hubId, double latitude, double longitude, int radius);

	public List<CarMaintenanceDto> getAllCarChangesAndWorstReturns();

	public int getTotalCarChangesAndWorstReturns();

	public List<CarRepairDto> getAllCarRepairs();


	public boolean changePhoneNoDriver(Long driverId, String newPhoneNo);

	public boolean changeEmailDriver(Long driverId, String newEmail);

	List<Driver> getTwoWheelerDriverList();

	List<Driver> getFourWheelerDriverList();

	int getTotalOngoingDrivers();

	public List<PaymentHistoryDto> getDriverPaymentHistory1(Long driverId);

	public Long getTotalBookingsByDriverId(Long driverId);

	

}
