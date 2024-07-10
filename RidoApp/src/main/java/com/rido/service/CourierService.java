package com.rido.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rido.Exceptions.UserNotFoundException;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.CourierBookingDto;
import com.rido.dto.CourierDataDto;
import com.rido.dto.CourierDetailsDto;
import com.rido.dto.CourierDocumentDto;
import com.rido.dto.CourierDto;
import com.rido.dto.CourierEbikeDataDto;
import com.rido.dto.CourierEbikeDto;
import com.rido.dto.CourierProfileDto;
import com.rido.dto.CourierRideHistoryDto;
import com.rido.dto.CourierTaskListDto;
import com.rido.dto.DriverUpdateRequestDto;
import com.rido.dto.OrderResponse;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.PaymentResponse;
import com.rido.dto.SenderReceiverInfoDto;
import com.rido.dto.TransportAllListDto;
import com.rido.entity.Courier;
import com.rido.entity.CourierDocument;
import com.rido.entity.ReturnCourierVehicle;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entityDTO.ResponseLogin;
import com.rido.utils.ApiResponse;

@Service
public interface CourierService {

	public boolean changePhoneNoUser(Long courierId, String newPhoneNo);

	public boolean changeEmailUser(Long courierId, String newEmail);

	public boolean changePassword(Long courierId, ChangePasswordRequestDto changePasswordRequestDto);

	public  ApiResponse<String> forgetPassword(String phoneNo);

	public boolean forgetPasswordVerify(Long userId, String forgotOtp);

	public boolean setNewPassword(Long userId, PasswordRequestDto passwordRequest);

	Courier getCourierProfileDetails(Long hubId, Long courierId);

	public CourierDocumentDto getCourierDocument(Long courierId);

	public boolean setPasswordCourierByEmail(Long courierId, PasswordRequestDto passwordRequest);

//	public List<DriverUpdateRequestDto> getAllPendingCourierList(Long hubId);
//
//	public List<DriverUpdateRequestDto> getAllApprovedCourierList(Long hubId);

	public CourierDetailsDto getCourierProfileDetailsById(Long hubId, Long courierId);

//	public CourierDetailsDto getCourierProfilePendingDetailsById(Long hubId, Long courierId);

	public CourierDetailsDto courierDriverApprove(Long hubId, Long courierId);

	public CourierDetailsDto courierDriverReject(Long hubId, Long courierId);

	public List<CourierTaskListDto> getCourierTasksByCourierId(Long courierId);

	// AADARSH KAUSHIK

	List<CourierDto> availableVehicle(Long userId, SenderReceiverInfoDto senderReceiverInfoDto);

	CourierBookingDto generateBookingInvoiceWithoutPromoCode(CourierDto courierDto);

	CourierBookingDto generateBookingInvoiceWithPromoCode(String promoCode, Long courierId);

	CourierBookingDto removePromoCodeFromCourierOrder(Long id);

	OrderResponse createOrder(CourierBookingDto courierBookingDto);

	PaymentResponse verifyPayment(Map<String, Object> paymentData);

	public String sendOtpByCourierForUser(Long userId);

	public CourierProfileDto getCourierProfileById(Long courierId);

	public void editCourierProfile(Long courierId, String userImageUrl, String name);

	public boolean verifyOtpForCourier1(Long userId, String enterOtp);

	public boolean goForNextRide(Long CourierBookingId);

	public List<CourierRideHistoryDto> getCourierRideHistory(Long courierId);

	void deleteNotConfirmedBookings();

	CourierDocument courierDocumentUpload(Long CourierId, CourierDataDto courierDataDto, String courierDriverImageUrl,
			String vehicleImageUrl, String registerCertificateImgUrl, String licenceImgUrl, String insuranceImgUrl,
			String passbookImageUrl);

	public Object returnCourierVehicle(String vehicleNo, Long courierId, String carCondition, String reason);

	public Object assignBikeToCourier(Long vehicleId, Long courierId, String bikeCondition, Long hubId);

	List<DriverUpdateRequestDto> getAllPendingCourierListForFour(Long hubId);

	List<DriverUpdateRequestDto> getAllApprovedCourierListForFour(Long hubId);

	List<DriverUpdateRequestDto> getAllPendingCourierListForTwo(Long hubId);

	List<DriverUpdateRequestDto> getAllApprovedCourierListForTwo(Long hubId);

	CourierDetailsDto getCourierProfilePendingDetailsByIdForFour(Long hubId, Long courierId);

	CourierDetailsDto getCourierProfileApprovedDetailsByIdForFour(Long hubId, Long courierId);

	CourierDetailsDto getCourierProfilePendingDetailsByIdForTwo(Long hubId, Long courierId);

	CourierDetailsDto getCourierProfileApprovedDetailsByIdForTwo(Long hubId, Long courierId);

	public List<Courier> findCourierForAssignBike();

	public Map<LocalDate, Integer> calculateEbikeWorkingHours(List<ReturnCourierVehicle> assignCars,
			List<ReturnCourierVehicle> returnCars);

	public int calculateTotalPayment(Map<LocalDate, Integer> workingHoursMap);

	public void courierEbikeDocumentUpload(Long courierId, CourierEbikeDataDto courierEbikeDataDto,
			String courierDriverImageUrl, String licenceImgUrl, String passbookImageUrl);

	public void saveTotalPayment(Long courierId, int totalPayment, LocalDate date);

	public List<TransportAllListDto> getTransportListByHubId(Long hubId);

	public List<TransportAllListDto> getTransportListByHubIdAndVehicleType(Long hubId,
			DriverAndVehicleType vehicleType);

	List<DriverUpdateRequestDto> getAllApprovedCourierDriverList(Long hubId);

	CourierDetailsDto getAvailableCourierDriverProfileDetailsById(Long hubId, Long courierId);

	List<CourierEbikeDto> getCourierEbikeListByHubId(Long hubId);

	CourierEbikeDto getCourierEbikeByIdAndHubId(Long id, Long hubId);

	public Object acceptParcelRequest(Long courierBookingId) throws UserNotFoundException;

	public Object setCourierDriverOngoing(Long courierId);

	List<CourierDto> getAllTwoWheelers(Long userId, SenderReceiverInfoDto senderReceiverInfoDto);

	public String sendOtpByCourierForReciver(Long bookingId);
//today

	public Map<String, String> sendSuccessMessage(Long bookingId);


	ResponseLogin getCourierByPhoneNo(String phoneNo);


}