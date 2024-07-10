package com.rido.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rido.dto.AdminDataDto;
import com.rido.dto.AdminProfileEditDto;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.CourierEbikeDto;
import com.rido.dto.DriverApproveRequestDto;
import com.rido.dto.DriveracceptpaymentDto;
import com.rido.dto.ListOfAssignVehiclesDto;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.ProfileDto;
import com.rido.dto.VehicleCourierEbikeDto;
import com.rido.dto.VehicleDataDto;
import com.rido.entity.Admin;
import com.rido.entity.CarRepair;
import com.rido.entity.CourierEbike;
import com.rido.entity.Driver;
import com.rido.entity.DriverPaymentDetail;
import com.rido.entity.Hub;
import com.rido.entity.User;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.MaintenanceApprovalStatus;
import com.rido.entityDTO.DriverPaymentDto;
import com.rido.entityDTO.ResponseLogin;

@Service
public interface AdminService {

	public Map<String, BigDecimal> getMonthlyTotalExpensesForYear(int year);

	public BigDecimal getTotalExpensesForCurrentMonthByHub(Long hubId);

	public BigDecimal getAvailableAmountForCurrentMonthByHub(Long hubId);

	public BigDecimal getAvailableAmountForCurrentMonth();

	public BigDecimal getTotalExpensesForCurrentMonth();

	public List<Driver> findAllDrivers();

	public List<Driver> findAvailableDriver();

	public List<Driver> findBookedDriver();

	public Optional<User> findUserById(Long userId);

	public List<User> findAllUsers();

	public List<Vehicle> getList();

	public Vehicle findById(Long id);

	// old
//	 public Driver approveDriver(Long driverId, String carNumber);

//	 public DriverApproveRequestDto convertEntityToDto(Long driverId);

	public DriverApproveRequestDto getUnapproverDriverDocumetns(Long DdriverId);

	public List<DriverApproveRequestDto> getRejectedDriverList();

	public Admin getHardcodedAdmin();

//	 public boolean verifyEmailOtp(String email, String otp);

	public boolean verifyEmailOtp(Long adminId, String otp);

	public DriveracceptpaymentDto customerPayment(Long driverId);

	public ProfileDto getProfileByEmail(String email);

	public Object getRevenueOfRido(LocalDate startDate);

	public ResponseLogin getByPhoneno(String phoneno);

	public AdminProfileEditDto getAdminProfile(Long adminId) throws Exception;

	public Admin updateAdminProfile(AdminDataDto adminDataJson, String s3Url) throws Exception;

	public CarRepair carRepairAccepted(Long carRepairId, MaintenanceApprovalStatus maintenanceApprovalStatus);

	public CarRepair carRepairRejected(Long carRepairId, MaintenanceApprovalStatus maintenanceApprovalStatus);

	boolean setNewPasswordForAdmin(Long adminId, PasswordRequestDto passwordDto);
	DriverPaymentDto driverToDto(DriverPaymentDetail driver);


	//public String addVehicle(VehicleDataDto DataDto, String s3Url, Long hubId);

	public List<DriverPaymentDetail> getPendingPaymentsByHubId(Long hubId);

	public Optional<DriverPaymentDetail> getPaymentDetailByHubIdAndDriverId(Long hubId, Long driverId);



	public List<Vehicle> getVehiclesByAdminId(Long adminId);

	public Optional<Driver> findDriverById(Long driverId);

	public List<DriverApproveRequestDto> getApprovedDrivers();

	public DriverApproveRequestDto getApprovedDriverDetails(Long driverId);

	public String deleteDriverById(Long driverId);

	public List<DriverApproveRequestDto> findByUnApproved();

	ByteArrayInputStream getDriverPaymentExcelData(LocalDate date) throws IOException ;
	public List<Vehicle> assignHubToVehicles(List<ListOfAssignVehiclesDto> AssignVehicles);

	public List<CourierEbike> assignHubToCourierVehicles(List<ListOfAssignVehiclesDto> assignVehicles);

	public List<Vehicle> getVehiclesByHubIdAndAdminId(Long hubId, Long adminId);

	public List<Vehicle> getVehiclesWithNoHub(Long adminId);

	public List<Vehicle> getVehiclesWithAvailableHub(Long adminId);

	public List<VehicleCourierEbikeDto> getVehiclesEbikesWithAvailableHub(Long adminId);

	public List<CourierEbike> getCourierEbikeWithAvailableHub(Long adminId);

	public boolean changePassword(Long adminId, ChangePasswordRequestDto changePasswordRequestDto);

	public String addVehicle(VehicleDataDto vehicleDataDto, List<String> vehicleImgUrls, String invoiceUrl,
			Long adminId);

	public List<Hub> getAllHubList(Long adminId);

	public String addCourierEbikeVehicle(CourierEbikeDto ebikeDataDto, List<String> s3Urls, String invoiceUrl, Long adminId);

	public Optional<Admin> getAdminById(Long adminId);

//	public String addVehicle(VehicleDataDto vehicleDataDto, String vehicleImgUrl, String invoiceUrl, Long adminId);

	
}
