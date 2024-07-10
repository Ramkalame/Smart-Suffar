package com.rido.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rido.Exceptions.HubNotFoundException;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.dto.AssignCarRequestDto;
import com.rido.dto.BookingDTO;
import com.rido.dto.CarRepairRequestDto;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.CourierBookingDto1;
import com.rido.dto.DriverNameAvailableDto;
import com.rido.dto.DriverReturnVehicleResponseDto;
import com.rido.dto.DriverRunningVehicleResponseDto;
import com.rido.dto.GetEmployeeListDto;
import com.rido.dto.HubDataDto;
import com.rido.dto.HubDriverPaymentDetailsDto;
import com.rido.dto.HubLocationDto;
import com.rido.dto.HubManagerDto;
import com.rido.dto.HubManagerPaymentHistoryDto;
import com.rido.dto.HubMangerProfileEditDto;
import com.rido.dto.PasswordChangeRequestDto;
import com.rido.dto.PaymentHistoryDto;
import com.rido.dto.ProfileDto;
import com.rido.dto.TodayBookingDashboardDto;
import com.rido.dto.VehicleDataDto;
import com.rido.dto.VehicleNameAvailableDto;
import com.rido.entity.Booking;
import com.rido.entity.CarRepair;
import com.rido.entity.CarRepairDetailCost;
import com.rido.entity.Driver;
import com.rido.entity.Hub;
import com.rido.entity.ReturnCar;
import com.rido.entity.Vehicle;
import com.rido.entityDTO.ResponseLogin;

@Service
public interface HubService {

	public BigDecimal getTotalCostOfRepairingForCurrentMonth();

	public BigDecimal getTotalCostOfRepairingForCurrentMonthByHub(Long hubId);

	List<CarRepairDetailCost> getAllCarRepairDetailCosts();

	public String carRepairDetailCostSend(Long carChangeId, String invoiceImg, BigDecimal totalCostOfRepairing);

	public List<HubLocationDto> getHubLocation();

	public Driver approveDriver(Long driverId);

	List<Hub> getHubList();

	List<DriverNameAvailableDto> getAvailableDriversName();

	List<VehicleNameAvailableDto> getAvailableCars();

//	public void assignCar(Long vehicleId);

	List<Driver> getAvailableDrivers();

	List<Booking> getAllBookings();

	public List<Booking> getTodaysBookings();
//old
//	public List<DriverRunningVehicleResponseDto> runningVehicleDetails();

	// public List<DriverRunningVehicleResponseDto> runningCerDetails();

	public List<HubDriverPaymentDetailsDto> totalearninbydriver(Long driverId);

	public List<GetEmployeeListDto> getHubEmployeeList(Long hubId);

	public String paySalaryToHubEmployee(String amount, Long hubEmployeeId);

	public List<CarRepairRequestDto> carProblems(Long hubId) throws Exception;

	public List<CarRepairRequestDto> carRepairRequest(Long hubId) throws Exception;

	public Hub updateHubProfile(HubDataDto hubDataDto, String s3Url, String signatureImageUrl, String passbookImageUrl)
			throws Exception;

	public String setPasswordByEmail(String email, PasswordChangeRequestDto hub);

	public boolean verifyEmailOtp(Long HubId, String otp);

//hub
//	List<HubManagerDto> getActiveHubs();

	HubManagerDto getHubDetails(Long hubId);

	public List<Vehicle> getListOfHubsVehicle(Long hubId);

	public Vehicle getVehicleOfHubByVehicleId(Long hubId, Long vehicleId);

	HubMangerProfileEditDto getHubMangerProfile(Long hubId);

	boolean changePasswordByOldPassword(Long hubId, ChangePasswordRequestDto changepassword)
			throws HubNotFoundException;

//new
	List<DriverRunningVehicleResponseDto> runningVehicleDetailsByHub(Long hubId);

	List<HubManagerPaymentHistoryDto> getPaymentHistoryByHubId(Long hubId);

	List<HubManagerDto> getAllHubs();

	public ProfileDto getProfileByEmail(String email);

	public ResponseLogin getByPhoneno(String phoneno);

	public List<Hub> findNearbyHubs(double latitude, double longitude, double radius);

	public Hub getHubById(Long hubId);

	public List<TodayBookingDashboardDto> getTodayBookingDashboard(Long hubid);

	public List<PaymentHistoryDto> getEmployeePayementHistoryByHubId(Long hubId);

//	public CarRepair carRepairApproval(Long carRepairId, BigDecimal approximateAmount, String damageCarImg);
//	public CarRepair carRepairApproval(Long carRepairId, String damageCarImg);

	public List<BookingDTO> getCourierListDetails(Long hubId) throws UserNotFoundException;

	ReturnCar assignCarToDriver(Long hubId, AssignCarRequestDto request);

	public List<VehicleDataDto> getTwoWheelarlist(Long hubId);

	public List<VehicleDataDto> getFourWheelarlist(Long hubId);

	public Object getRentalBookingDetails();

	public Object getCourierBookingDetails();

	public Object getRentalBookingDetailsOfHub(Long hubId);

	public Object getCourierBookingDetailsOfHub(Long hubId);

	List<DriverReturnVehicleResponseDto> returnVehiclesDetails();

	Object getScheduleBookingDetails();

	public List<CourierBookingDto1> getAllCourierBookings();

	CarRepair carRepairApproval(Long carRepairId, List<String> damageCarImgs, String damageCarVideo, String hubMessage) ;

	
}
