package com.rido.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.rido.Exceptions.DriverNotFoundException;
import com.rido.Exceptions.ResourceNotFoundException;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.config.MyWebSocketHandler;
import com.rido.config.RazorPayConfiguration;
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
import com.rido.entity.CourierBooking;
import com.rido.entity.CourierDocument;
import com.rido.entity.CourierEbike;
import com.rido.entity.CourierEbikedriverPayment;
import com.rido.entity.CourierOrder;
import com.rido.entity.ManageOtp;
import com.rido.entity.PromoCode;
import com.rido.entity.ReturnCourierVehicle;
import com.rido.entity.SenderReceiverInfo;
import com.rido.entity.TimeDuration;
import com.rido.entity.User;
import com.rido.entity.UserCourierPayment;
import com.rido.entity.UserIdentity;
import com.rido.entity.enums.ApproveStatus;
import com.rido.entity.enums.CourierBookingStatus;
import com.rido.entity.enums.CourierEbikeDriverPaymentStatus;
import com.rido.entity.enums.CourierOrderStatus;
import com.rido.entity.enums.CourierPaymentStatus;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.RideOrderStatus;
import com.rido.entity.enums.Status;
import com.rido.entity.enums.VehicleAssignStatus;
import com.rido.entity.enums.VehicleStatus;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.CourierBookingRepository;
import com.rido.repository.CourierDocumentRepository;
import com.rido.repository.CourierEbikeRepository;
import com.rido.repository.CourierEbikedriverPaymentRepository;
import com.rido.repository.CourierOrderRepository;
import com.rido.repository.CourierRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.PromoCodeRepository;
import com.rido.repository.ReturnCourierVehicleRepository;
import com.rido.repository.SenderReceiverInfoRepository;
import com.rido.repository.TimeDurationRepository;
import com.rido.repository.UserCourierPaymentRepository;
import com.rido.repository.UserIdentityRepository;
import com.rido.repository.UserRepository;
import com.rido.repository.VehicleRepository;
import com.rido.service.CourierService;
import com.rido.service.LocationService;
import com.rido.service.PromoCodeService;
import com.rido.utils.ApiResponse;
import com.rido.utils.CalculateDistance;
import com.rido.utils.ConvertToHoursAndMinutes;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CourierServiceImpl implements CourierService {

	private static final double GST = 18;
	private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
	private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

	@Autowired
	private SenderReceiverInfoRepository senderReceiverInfoRepository;

	@Autowired
	private UserCourierPaymentRepository userCourierPaymentRepository;

	@Autowired
	private RazorPayConfiguration razorPayConfiguration;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private CourierBookingRepository courierBookingRepository;

	@Autowired
	private CourierEbikedriverPaymentRepository courierEbikedriverPaymentRepo;

	@Autowired
	private CourierOrderRepository courierOrderRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private PromoCodeService promoCodeService;

	@Autowired
	private CourierRepository courierRepository;

	@Autowired
	private ManageOtpRepository manageOtpRepository;

	@Autowired
	private LocationImpl locationImpl;

	@Autowired
	private UserIdentityRepository userIdentityRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserServiceImpl userServiceImp;

	@Autowired
	private LocationService locationService;

	@Autowired
	private CourierDocumentRepository courierDocumentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TimeDurationRepository timeDurationRepository;

	@Autowired
	private ReturnCourierVehicleRepository returnCourierVehicleRepository;

	@Autowired
	private CourierEbikeRepository courierEbikeRepository;

	@Autowired
	private MyWebSocketHandler webSocketHandler;

	@Autowired
	private PromoCodeRepository promoCodeRepository;

	@Override
	public boolean changePhoneNoUser(Long courierId, String newPhoneNo) {

		Optional<Courier> optionalUser = courierRepository.findById(courierId);

		if (optionalUser.isPresent()) {
			Courier existingUser = optionalUser.get();

			if (!existingUser.getPhoneNo().equals(newPhoneNo)) {
				existingUser.setPhoneNo(newPhoneNo);

				// Update the phone number in UserIdentity entity if it exists
				Optional<UserIdentity> optionalUserIdentity = userIdentityRepository
						.findByEmail(existingUser.getEmail());

				if (optionalUserIdentity.isPresent()) {
					UserIdentity existingUserIdentity = optionalUserIdentity.get();
					existingUserIdentity.setPhoneNo(newPhoneNo);
					userIdentityRepository.save(existingUserIdentity); // Save the updated UserIdentity entity
					// Optionally, you can log here to confirm if the UserIdentity is being updated
					// properly
					System.out.println("UserIdentity phone number updated for Courier with ID: 96}"
							+ existingUserIdentity.getPhoneNo());
					courierRepository.save(existingUser); // Save the updated user entity
					// Optionally, you can log here to confirm if the user is being updated properly
					System.out.println("Phone number updated for Courier with ID: 99" + existingUser.getPhoneNo());

				} else {
					// Log an error if UserIdentity is not found for the user
					System.out.println("UserIdentity not found with email: {}" + existingUser.getEmail());
				}

				return true; // Phone number successfully updated
			} else {
				// Log an error if the new phone number is the same as the existing one

				System.out.println("New phone number is the same as the existing phone number: {}" + newPhoneNo);

				System.out.println("New phone number is the same as the existing phone number: " + newPhoneNo);

				return false; // Phone number not updated
			}
		} else {
			// Log an error if the Courier is not found
			System.out.println("Courier not found with ID: {}" + courierId);
			return false; // Phone number not updated
		}

	}

	@Override
	public boolean changeEmailUser(Long courierId, String newEmail) {

		Optional<Courier> optionalUser = courierRepository.findById(courierId);

		if (optionalUser.isPresent()) {
			Courier existingUser = optionalUser.get();

			if (!existingUser.getPhoneNo().equals(newEmail)) {
				existingUser.setEmail(newEmail);

				// Update the phone number in UserIdentity entity if it exists
				Optional<UserIdentity> optionalUserIdentity = userIdentityRepository
						.findByPhoneNo(existingUser.getPhoneNo());

				if (optionalUserIdentity.isPresent()) {
					UserIdentity existingUserIdentity = optionalUserIdentity.get();
					existingUserIdentity.setEmail(newEmail);
					userIdentityRepository.save(existingUserIdentity); // Save the updated UserIdentity entity
					// Optionally, you can log here to confirm if the UserIdentity is being updated
					// properly
					System.out.println(
							"UserIdentity email updated for user with ID: 965}" + existingUserIdentity.getEmail());
					courierRepository.save(existingUser); // Save the updated user entity
					// Optionally, you can log here to confirm if the user is being updated properly
					System.out.println("email updated for user with ID: 955" + existingUser.getEmail());

				} else {
					// Log an error if UserIdentity is not found for the user
					System.out.println("UserIdentity not found with phone number: {}" + existingUser.getPhoneNo());
				}

				return true; // Phone number successfully updated
			} else {
				// Log an error if the new phone number is the same as the existing one
				System.out.println("New phone number is the same as the existing phone number: {}" + newEmail);
				return false; // Phone number not updated
			}
		} else {
			// Log an error if the user is not found
			System.out.println("Courier not found with ID: {}" + courierId);
			return false; // Phone number not updated
		}
	}

	@Override
	public boolean changePassword(Long courierId, ChangePasswordRequestDto changePasswordRequestDto) {
		// Retrieve user from the database

		Courier courier = courierRepository.findById(courierId).orElse(null);

		// Check if the old password matches the stored password
		System.out.println(courier.getPassword() + "courier old pass");

		System.out.println(courier.getPhoneNo() + " phoneno");
		if (courier != null
				&& passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), courier.getPassword())) {

			Optional<UserIdentity> userIdentityOptional = userIdentityRepository.findByPhoneNo(courier.getPhoneNo());
			System.out.println(userIdentityOptional.get() + " courier phoneno line 140");
			// Check if the new password and confirm password match
			if (userIdentityOptional.isPresent()) {
				UserIdentity userIdentity = userIdentityOptional.get();

				// Optional: You might want to log userIdentity to ensure it's retrieved
				// correctly
				System.out.println("UserIdentity: " + userIdentity);

				System.out.println(userIdentity.getPhoneNo());

				if (courier.getPhoneNo().equals(userIdentity.getPhoneNo())) {
					if (changePasswordRequestDto.getNewPassword()
							.equals(changePasswordRequestDto.getConfirmPassword())) {
						String encodedPassword = passwordEncoder.encode(changePasswordRequestDto.getNewPassword());

						courier.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						courierRepository.save(courier);
						userIdentityRepository.save(userIdentity);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public ApiResponse<String> forgetPassword(String phoneNo) {
		String otp = userServiceImp.generateVerificationCode();

		System.out.println("line 680: " + phoneNo);
		Optional<Courier> userOptional = courierRepository.findByPhoneNo(phoneNo);

		System.out.println("line 680: " + userOptional);

		if (userOptional.isEmpty()) {
			return createApiResponse(null, HttpStatus.NOT_FOUND, false, "Courier not found");
		}

		Courier user = userOptional.get();

		Optional<ManageOtp> manageOtpOptional = manageOtpRepository.findByCourier_CourierId(user.getCourierId());

		if (manageOtpOptional.isEmpty()) {
			return createApiResponse(null, HttpStatus.NOT_FOUND, false, "ManageOtp not found");
		}

		ManageOtp manageOtp = manageOtpOptional.get();

		manageOtp.setForgetOtp(otp);
		manageOtpRepository.save(manageOtp);

		locationService.sendVerificationCode(manageOtp.getCourier().getPhoneNo(), otp);
		return createApiResponse(null, HttpStatus.OK, true, "Verification code sent successfully");
	}

	private <T> ApiResponse<T> createApiResponse(T data, org.springframework.http.HttpStatus status, boolean success,
			String message) {
		ApiResponse<T> response = new ApiResponse<>();
		response.setData(data);
		response.setStatus(status);
		response.setSuccess(success);
		response.setMessage(message);
		return response;
	}

	@Override
	public boolean forgetPasswordVerify(Long courierId, String forgotOtp) {

		Optional<ManageOtp> manageOtpOptional = manageOtpRepository.findByCourier_CourierId(courierId);

		// Check if the optional contains a value
		if (manageOtpOptional.isPresent()) {
			ManageOtp manageOtp = manageOtpOptional.get();
			String smsOtp = manageOtp.getForgetOtp();

			// Check if the forgetOtp matches smsOtp
			System.out.println(smsOtp + "smsotp + line no 370");
			if (smsOtp.equals(forgotOtp)) {
				System.out.println(smsOtp + "byemail 667");
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean setNewPassword(Long courierId, PasswordRequestDto passwordDto) {

		Courier courier = courierRepository.findById(courierId).orElse(null);

		if (courier != null) {

			if (userServiceImp.isValidPassword(passwordDto.getNewpassword(), passwordDto.getConfirmPassword())) {

				Optional<UserIdentity> userIdentityOptional = userIdentityRepository
						.findByPhoneNo(courier.getPhoneNo());

				if (userIdentityOptional.isPresent()) {
					UserIdentity userIdentity = userIdentityOptional.get();

					if (courier.getPhoneNo().equals(userIdentity.getPhoneNo())) {

						String encodedPassword = passwordEncoder.encode(passwordDto.getNewpassword());

						courier.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						courierRepository.save(courier);
						userIdentityRepository.save(userIdentity);
						return true;
					}
				}
			}
		}
		return false; // Password update failed ;
	}

	// update
	@Override
	public Courier getCourierProfileDetails(Long hubId, Long courierId) {
		// Assuming you have a method in the repository to retrieve courier by ID and
		// hub ID
		Optional<Courier> optionalCourier = courierRepository.findByHub_hubIdAndCourierId(hubId, courierId);
		return optionalCourier.orElse(null);
	}

	@Override
	@Transactional
	public CourierDocument courierDocumentUpload(Long courierId, CourierDataDto courierDataDto,
			String courierDriverImageUrl, String vehicleImageUrl, String registerCertificateImgUrl,
			String licenceImgUrl, String insuranceImgUrl, String passbookImageUrl) {

		Courier courier = courierRepository.findById(courierId)
				.orElseThrow(() -> new EntityNotFoundException("Courier not found"));

		// Update courier fields
		if (courierDataDto.getWeight() != null && !courierDataDto.getWeight().isEmpty()) {
			double weight = Double.parseDouble(courierDataDto.getWeight());
			courier.setWeight(weight);
		}

		if (courierDataDto.getVehicleNo() != null && !courierDataDto.getVehicleNo().isEmpty()) {
			courier.setVehicleNo(courierDataDto.getVehicleNo());
		}

		courier.setApproveStatus(ApproveStatus.PENDING);

		// Create and update CourierDocument
		CourierDocument courierDocument = new CourierDocument();

		if (courierDataDto.getAccountHolderName() != null && !courierDataDto.getAccountHolderName().isEmpty()) {
			courierDocument.setAccountHolderName(courierDataDto.getAccountHolderName());
		}

		if (courierDataDto.getAccountNo() != null && !courierDataDto.getAccountNo().isEmpty()) {
			courierDocument.setAccountNo(courierDataDto.getAccountNo());
		}

		if (courierDataDto.getIfsccode() != null && !courierDataDto.getIfsccode().isEmpty()) {
			courierDocument.setIFSCcode(courierDataDto.getIfsccode());
		}

		if (courierDataDto.getAadhaarNo() != null && !courierDataDto.getAadhaarNo().isEmpty()) {
			courierDocument.setAadhaarNo(courierDataDto.getAadhaarNo());
		}

		if (courierDriverImageUrl != null && !courierDriverImageUrl.isEmpty()) {
			courierDocument.setCourierDriverImage(courierDriverImageUrl);
		}

		if (vehicleImageUrl != null && !vehicleImageUrl.isEmpty()) {
			courierDocument.setVehicleImage(vehicleImageUrl);
		}

		if (registerCertificateImgUrl != null && !registerCertificateImgUrl.isEmpty()) {
			courierDocument.setRegisterCertificate(registerCertificateImgUrl);
		}

		if (licenceImgUrl != null && !licenceImgUrl.isEmpty()) {
			courierDocument.setLicence(licenceImgUrl);
		}

		if (insuranceImgUrl != null && !insuranceImgUrl.isEmpty()) {
			courierDocument.setInsurance(insuranceImgUrl);
		}

		if (passbookImageUrl != null && !passbookImageUrl.isEmpty()) {
			courierDocument.setPassbook(passbookImageUrl);
		}

		// Persist CourierDocument
		courierDocument = courierDocumentRepository.save(courierDocument);

		// Set the persisted CourierDocument to the Courier and save Courier
		courier.setCourierDocument(courierDocument);
		courierRepository.save(courier);

		return courierDocument;

	}

	// update
	@Override
	public CourierDocumentDto getCourierDocument(Long courierId) {
		Optional<Courier> courierOptional = courierRepository.findById(courierId);

		if (courierOptional.isPresent()) {
			Courier courier = courierOptional.get();
			CourierDocument courierDocument = courier.getCourierDocument();

			CourierDocumentDto dto = new CourierDocumentDto();
			dto.setCourierdocumentId(courierDocument.getCourierdocumentId());
			dto.setCourierDriverImage(courierDocument.getCourierDriverImage());
			dto.setVehicleImage(courierDocument.getVehicleImage());
			dto.setRegisterCertificate(courierDocument.getRegisterCertificate());
			dto.setLicence(courierDocument.getLicence());
			dto.setInsurance(courierDocument.getInsurance());
			dto.setPassbook(courierDocument.getPassbook());
			dto.setAccountNo(courierDocument.getAccountNo());
			dto.setAccountHolderName(courierDocument.getAccountHolderName());
			dto.setCourierId(courier.getCourierId());

			return dto;
		} else {
			// Handle the case where no CourierDocument is found with the provided courierId
			throw new NoSuchElementException("CourierDocument not found for courierId: " + courierId);
		}
	}

	@Override
	public boolean setPasswordCourierByEmail(Long courierId, PasswordRequestDto passwordRequest) {
		Courier courier = courierRepository.findById(courierId).orElse(null);

		if (courier != null) {

			if (userServiceImp.isValidPassword(passwordRequest.getNewpassword(),
					passwordRequest.getConfirmPassword())) {

				Optional<UserIdentity> userIdentityOptional = userIdentityRepository.findByEmail(courier.getEmail());

				if (userIdentityOptional.isPresent()) {
					UserIdentity userIdentity = userIdentityOptional.get();

					if (courier.getEmail().equals(userIdentity.getEmail())) {

						String encodedPassword = passwordEncoder.encode(passwordRequest.getNewpassword());

						courier.setPassword(encodedPassword);
						userIdentity.setPassword(encodedPassword);

						courierRepository.save(courier);
						userIdentityRepository.save(userIdentity);
						return true;
					}
				}
			}
		}
		return false; //
	}

	@Override
	public List<DriverUpdateRequestDto> getAllPendingCourierListForFour(Long hubId) {
		List<Courier> couriers = courierRepository.findByHub_HubIdAndApproveStatusAndVehicleType(hubId,
				ApproveStatus.PENDING, DriverAndVehicleType.FOUR_WHEELER);
		List<DriverUpdateRequestDto> dtos = new ArrayList<>();
		for (Courier courier : couriers) {
			dtos.add(mapCourierToDtosend(courier));
		}
		return dtos;
	}

	@Override
	public List<DriverUpdateRequestDto> getAllApprovedCourierListForFour(Long hubId) {
		List<Courier> couriers = courierRepository.findByHub_HubIdAndApproveStatusAndVehicleType(hubId,
				ApproveStatus.APPROVED, DriverAndVehicleType.FOUR_WHEELER);
		List<DriverUpdateRequestDto> dtos = new ArrayList<>();
		for (Courier courier : couriers) {
			dtos.add(mapCourierToDto(courier));
		}
		return dtos;
	}

	@Override
	public List<DriverUpdateRequestDto> getAllPendingCourierListForTwo(Long hubId) {
		List<Courier> couriers = courierRepository.findByHub_HubIdAndApproveStatusAndVehicleType(hubId,
				ApproveStatus.PENDING, DriverAndVehicleType.TWO_WHEELER);
		List<DriverUpdateRequestDto> dtos = new ArrayList<>();
		for (Courier courier : couriers) {
			dtos.add(mapCourierToDtosend(courier));
		}
		return dtos;
	}

	@Override
	public List<DriverUpdateRequestDto> getAllApprovedCourierListForTwo(Long hubId) {
		List<Courier> couriers = courierRepository.findByHub_HubIdAndApproveStatusAndVehicleType(hubId,
				ApproveStatus.APPROVED, DriverAndVehicleType.TWO_WHEELER);
		List<DriverUpdateRequestDto> dtos = new ArrayList<>();
		for (Courier courier : couriers) {
			dtos.add(mapCourierToDto(courier));
		}
		return dtos;
	}

	@Override
	public List<DriverUpdateRequestDto> getAllApprovedCourierDriverList(Long hubId) {
		List<Courier> couriers = courierRepository.findByHub_HubIdAndApproveStatusAndVehicleStatus(hubId,
				ApproveStatus.APPROVED, VehicleStatus.AVAILABLE);
		List<DriverUpdateRequestDto> dtos = new ArrayList<>();
		for (Courier courier : couriers) {
			dtos.add(mapCourierToDto(courier));
		}
		return dtos;
	}

	private DriverUpdateRequestDto mapCourierToDtosend(Courier courier) {
		DriverUpdateRequestDto dto = new DriverUpdateRequestDto();
		dto.setId(courier.getCourierId());
		dto.setName(courier.getOwnerName());
		dto.setPhoneNo(courier.getPhoneNo());
		dto.setEmail(courier.getEmail());

//		System.out.println("line 446" + courier.getCourierId());
		// Fetch associated courier document

//		System.out.println("line 446" + courierDetails);
		// Check if courier document is present
		CourierDocument courierDocument = courier.getCourierDocument();
		dto.setDriverImage(courierDocument.getCourierDriverImage());

		return dto;
	}

	private DriverUpdateRequestDto mapCourierToDto(Courier courier) {
		DriverUpdateRequestDto dto = new DriverUpdateRequestDto();
		dto.setId(courier.getCourierId());
		dto.setName(courier.getOwnerName());
		dto.setPhoneNo(courier.getPhoneNo());
		dto.setEmail(courier.getEmail());
		dto.setDriverImage(courier.getCourierDocument().getCourierDriverImage());

		return dto;
	}

	public CourierDetailsDto getCourierProfileDetailsById(Long hubId, Long courierId) {
		Optional<Courier> courierOptional = courierRepository.findByHub_hubIdAndCourierId(hubId, courierId);

		if (courierOptional.isPresent()) {
			Courier courier = courierOptional.get();
			return mapCourierToDtobject(courier);
		}

		return null;
	}

//	@Override
//	public CourierDetailsDto getCourierProfilePendingDetailsByIdForFour(Long hubId, Long courierId) {
//
//		Optional<Courier> courier = courierRepository.findByHub_hubIdAndCourierId(hubId, courierId);
//
//		Courier courier2 = courier.get();
//		if (courier2 != null && courier2.getApproveStatus() && courier2.getVehicleType() == ApproveStatus.PENDING && DriverAndVehicleType.FOUR_WHEELER) {
//			return mapCourierToDtobject(courier);
//		} else {
//			return null;
//		}
//	}

	@Override
	public CourierDetailsDto getCourierProfilePendingDetailsByIdForFour(Long hubId, Long courierId) {
		Optional<Courier> courier = courierRepository.findByHub_hubIdAndCourierId(hubId, courierId);
		Courier courier2 = courier.orElse(null); // Use orElse to handle null case

		if (courier2 != null && courier2.getApproveStatus() == ApproveStatus.PENDING
				&& courier2.getVehicleType() == DriverAndVehicleType.FOUR_WHEELER) {
			return mapCourierToDtobject(courier2);
		} else {
			return null;
		}
	}

	@Override
	public CourierDetailsDto getCourierProfileApprovedDetailsByIdForFour(Long hubId, Long courierId) {
		Optional<Courier> courier = courierRepository.findByHub_hubIdAndCourierId(hubId, courierId);
		Courier courier2 = courier.orElse(null); // Use orElse to handle null case

		if (courier2 != null && courier2.getApproveStatus() == ApproveStatus.APPROVED
				&& courier2.getVehicleType() == DriverAndVehicleType.FOUR_WHEELER) {
			return mapCourierToDtobject(courier2);
		} else {
			return null;
		}
	}

	@Override
	public CourierDetailsDto getCourierProfilePendingDetailsByIdForTwo(Long hubId, Long courierId) {

		Optional<Courier> courier = courierRepository.findByHub_hubIdAndCourierId(hubId, courierId);
		Courier courier2 = courier.orElse(null); // Use orElse to handle null case

		if (courier2 != null && courier2.getApproveStatus() == ApproveStatus.PENDING
				&& courier2.getVehicleType() == DriverAndVehicleType.TWO_WHEELER) {
			return mapCourierToDtobject(courier2);
		} else {
			return null;
		}
	}

	@Override
	public CourierDetailsDto getCourierProfileApprovedDetailsByIdForTwo(Long hubId, Long courierId) {

		Optional<Courier> courier = courierRepository.findByHub_hubIdAndCourierId(hubId, courierId);
		Courier courier2 = courier.orElse(null); // Use orElse to handle null case

		if (courier2 != null && courier2.getApproveStatus() == ApproveStatus.APPROVED
				&& courier2.getVehicleType() == DriverAndVehicleType.TWO_WHEELER) {
			return mapCourierToDtobject(courier2);
		} else {
			return null;
		}
	}

	@Override
	public CourierDetailsDto getAvailableCourierDriverProfileDetailsById(Long hubId, Long courierId) {
		Optional<Courier> courier = courierRepository.findByHub_hubIdAndCourierId(hubId, courierId);
		Courier courier2 = courier.orElse(null); // Use orElse to handle null case

		if (courier2 != null && courier2.getApproveStatus() == ApproveStatus.APPROVED
				&& courier2.getVehicleStatus() == VehicleStatus.AVAILABLE) {
			return mapCourierToDtobject(courier2);
		} else {
			return null;
		}
	}

	public CourierDetailsDto courierDriverApprove(Long hubId, Long courierId) {
		Optional<Courier> optionalCourier = courierRepository.findByHub_hubIdAndCourierId(hubId, courierId);

		if (optionalCourier.isPresent()) {
			Courier courier = optionalCourier.get();

			if (courier.getApproveStatus() == ApproveStatus.PENDING) {
				courier.setApproveStatus(ApproveStatus.APPROVED);

				String phoneNo = courier.getPhoneNo();
				String message = "Welcome to Smartsafer. Now connect with users.";

				locationImpl.sendVerificationCode(phoneNo, message);

				courierRepository.save(courier);

				return mapCourierToDtobject(courier);
			}
		}

		return null;
	}

	@Override
	public CourierDetailsDto courierDriverReject(Long hubId, Long courierId) {
		Optional<Courier> optionalCourier = courierRepository.findByHub_hubIdAndCourierId(hubId, courierId);

		if (optionalCourier.isPresent()) {
			Courier courier = optionalCourier.get();

			if (courier.getApproveStatus() == ApproveStatus.PENDING) {
				courier.setApproveStatus(ApproveStatus.REJECTED);

				String phoneNo = courier.getPhoneNo();
				String message = "Your courier registration has been rejected. Please contact support for further assistance.";

				// locationImpl.sendVerificationCode(phoneNo, message);

				courierRepository.save(courier);

				return mapCourierToDtobject(courier); // Corrected argument
			}
		}
		return null;
	}

	private CourierDetailsDto mapCourierToDtobject(Courier courier) {
		Optional<Courier> courierDetails = courierRepository.findById(courier.getCourierId());

		if (courierDetails.isPresent()) {
			CourierDetailsDto dto = new CourierDetailsDto();
			Courier courierDocument = courierDetails.get();

			dto.setCourierdocumentId(courier.getCourierId());
			dto.setCourierDriverImage(courierDocument.getCourierDocument().getCourierDriverImage());
			dto.setName(courier.getOwnerName());
			dto.setEmail(courier.getEmail());
			dto.setPhoneNo(courier.getPhoneNo());

			dto.setAccountHolderName(courierDocument.getCourierDocument().getAccountHolderName());
			dto.setAccountNo(courierDocument.getCourierDocument().getAccountNo());
			dto.setPassbook(courierDocument.getCourierDocument().getPassbook());

			dto.setInsurance(courierDocument.getCourierDocument().getInsurance());
			dto.setVehicleImage(courierDocument.getCourierDocument().getVehicleImage());
			dto.setRegisterCertificate(courierDocument.getCourierDocument().getRegisterCertificate());

			dto.setVehicleNo(courier.getVehicleNo());
			dto.setVehicleType(courier.getVehicleType());
			dto.setWieght(courier.getWeight());
			dto.setLicence(courierDocument.getCourierDocument().getLicence());
			dto.setAadhar(courierDocument.getCourierDocument().getAadhaarNo());
			return dto;
		} else {
			return null;
		}
	}

	// AADARSH KAUSHIK

	// API to display list of vehicles and it's information
	@Override
	public List<CourierDto> availableVehicle(Long userId, SenderReceiverInfoDto senderReceiverInfoDto) {

		senderReceiverInfoDto.setUserId(userId);
		senderReceiverInfoDto.setTotalDistance(Math.round(Double
				.parseDouble(decimalFormat.format(CalculateDistance.distance(senderReceiverInfoDto.getSenderLatitude(),
						senderReceiverInfoDto.getSenderLongitude(), senderReceiverInfoDto.getReceiverLatitude(),
						senderReceiverInfoDto.getReceiverLongitude())))));

		double totalTravelTime = (senderReceiverInfoDto.getTotalDistance() / 40) * 60;
		String hours = ConvertToHoursAndMinutes.convertToHoursAndMinutes((int) totalTravelTime).substring(0, 2);
		String minutes = ConvertToHoursAndMinutes.convertToHoursAndMinutes((int) totalTravelTime).substring(3, 5);
		senderReceiverInfoDto.setExpectedTime(hours + " Hours " + minutes + " minutes");

		// setting the price according to vehicle type
		int price = 30;
		BigDecimal pricePerKilometer = new BigDecimal(price);

		// fetching all vehicle by selected type
		List<Courier> vehicles = courierRepository.findAllByVehicleType(DriverAndVehicleType.FOUR_WHEELER);
		if (vehicles.isEmpty()) {
			throw new ResourceNotFoundException("Vehicles not found", "");
		}
		for (Courier vehicle : vehicles) {
			double distanceFromSender = Double.parseDouble(
					decimalFormat.format((CalculateDistance.distance(senderReceiverInfoDto.getSenderLatitude(),
							senderReceiverInfoDto.getSenderLongitude(), vehicle.getCourierDriverLatitude(),
							vehicle.getCourierDriverLongitude()))));
			vehicle.setDistanceFromSender(distanceFromSender);
			BigDecimal baseFare = pricePerKilometer
					.multiply(BigDecimal.valueOf(senderReceiverInfoDto.getTotalDistance()));
			BigDecimal gstAmount = baseFare.divide(BigDecimal.valueOf(GST), 2, RoundingMode.HALF_UP);
			vehicle.setPrice(baseFare.add(gstAmount));
//			System.out.println(vehicle);
		}

		// filtering the available vehicle and sorting according to distance from sender
		List<Courier> sortedAvailableVehicles = vehicles.stream()
				.filter(courier -> courier.getVehicleStatus().equals(VehicleStatus.AVAILABLE))
				.sorted(Comparator.comparingDouble(Courier::getDistanceFromSender)).toList();

		if (sortedAvailableVehicles.isEmpty()) {
			throw new ResourceNotFoundException("Available drivers not found", "");
		}

		List<CourierDto> availableVehicles = new ArrayList<>();

		for (Courier sortedAvailableVehicle : sortedAvailableVehicles) {
			CourierDto courierDto = new CourierDto();
			courierDto.setCourierId(sortedAvailableVehicle.getCourierId());
			courierDto.setVehicleType(sortedAvailableVehicle.getVehicleType());
			courierDto.setVehicleNo(sortedAvailableVehicle.getVehicleNo());
			courierDto.setPrice(sortedAvailableVehicle.getPrice());
			courierDto.setDistanceFromSender(sortedAvailableVehicle.getDistanceFromSender());
			courierDto.setWeight(sortedAvailableVehicle.getWeight());
			courierDto.setVehicleCategory(sortedAvailableVehicle.getVehicleCategory());
			courierDto.setHubId(sortedAvailableVehicle.getHub().getHubId());

			// finding the vehicle image
//			Optional<CourierDocument> courierDocument = courierDocumentRepository
//					.findById(sortedAvailableVehicle.getCourierId());
//			courierDto.setVehicleImage(courierDocument.get().getVehicleImage());
			courierDto.setVehicleImage(sortedAvailableVehicle.getCourierDocument().getVehicleImage());
			courierDto.setVehicleStatus(sortedAvailableVehicle.getVehicleStatus());
			courierDto.setSenderReceiverInfoDto(senderReceiverInfoDto);

			// calculating time to reach sender
			double reachingTime = (sortedAvailableVehicle.getDistanceFromSender() / 40) * 60;
			String hoursToReach = ConvertToHoursAndMinutes.convertToHoursAndMinutes((int) reachingTime).substring(0, 2);
			String minutesToReach = ConvertToHoursAndMinutes.convertToHoursAndMinutes((int) reachingTime).substring(3,
					5);

			courierDto.setTimeAwayFromSender(hoursToReach + " Hours " + minutesToReach + " minutes");

			availableVehicles.add(courierDto);
		}
		return availableVehicles;
	}

	// API to generate invoice when vehicle is clicked
	@Override
	public CourierBookingDto generateBookingInvoiceWithoutPromoCode(CourierDto courierDto) {

		Optional<Courier> currentCourier = courierRepository.findById(courierDto.getCourierId());
		Optional<User> currentUser = userRepository.findById(courierDto.getSenderReceiverInfoDto().getUserId());

		CourierBooking courierBooking = new CourierBooking();

		// CONVERTING INTO ENTITY
		SenderReceiverInfo senderReceiverInfo = new SenderReceiverInfo();
		senderReceiverInfo.setSenderLatitude(courierDto.getSenderReceiverInfoDto().getSenderLatitude());
		senderReceiverInfo.setSenderLongitude(courierDto.getSenderReceiverInfoDto().getSenderLongitude());
		senderReceiverInfo.setSenderLocation(courierDto.getSenderReceiverInfoDto().getSenderLocation());
		senderReceiverInfo.setSenderAddress(courierDto.getSenderReceiverInfoDto().getSenderAddress());
		senderReceiverInfo.setSenderName(courierDto.getSenderReceiverInfoDto().getSenderName());
		senderReceiverInfo.setSenderPhoneNumber(courierDto.getSenderReceiverInfoDto().getSenderPhoneNumber());
		senderReceiverInfo.setReceiverLatitude(courierDto.getSenderReceiverInfoDto().getReceiverLatitude());
		senderReceiverInfo.setReceiverLongitude(courierDto.getSenderReceiverInfoDto().getReceiverLongitude());
		senderReceiverInfo.setReceiverLocation(courierDto.getSenderReceiverInfoDto().getReceiverLocation());
		senderReceiverInfo.setReceiverAddress(courierDto.getSenderReceiverInfoDto().getReceiverAddress());
		senderReceiverInfo.setReceiverName(courierDto.getSenderReceiverInfoDto().getReceiverName());
		senderReceiverInfo.setReceiverPhoneNumber(courierDto.getSenderReceiverInfoDto().getReceiverPhoneNumber());
		senderReceiverInfo.setVehicleType(courierDto.getSenderReceiverInfoDto().getVehicleType());
		senderReceiverInfo.setTotalDistance(courierDto.getSenderReceiverInfoDto().getTotalDistance());
		senderReceiverInfo.setExpectedTime(courierDto.getSenderReceiverInfoDto().getExpectedTime());
		senderReceiverInfo.setCourier(currentCourier.get());
		senderReceiverInfo.setUser(currentUser.get());

		// SAVING SENDER AND RECEIVER INFORMATION IN REPOSITORY
		SenderReceiverInfo savedSenderReceiverInfo = senderReceiverInfoRepository.save(senderReceiverInfo);

		// SETTING THE COURIER BOOKING ENTITY
		courierBooking.setGst(18);
		courierBooking.setPricePerKm(courierDto.getVehicleType().equals("2 Wheeler") ? 15 : 30);
		courierBooking.setReceiverName(courierDto.getSenderReceiverInfoDto().getReceiverName());
		courierBooking.setSenderName(courierDto.getSenderReceiverInfoDto().getSenderName());
		courierBooking.setBaseAmount(courierDto.getPrice());
		courierBooking.setTotalAmount(courierDto.getPrice());
		courierBooking.setSenderReceiverInfo(savedSenderReceiverInfo);
		courierBooking.setUser(savedSenderReceiverInfo.getUser());
		courierBooking.setCourierDriver(currentCourier.get());
		courierBooking.setIsConfirm(CourierBookingStatus.NOT_CONFIRMED);
		courierBooking.setRideOrderStatus(RideOrderStatus.PENDING);

		// SAVING THE COURIER ENTITY
		CourierBooking savedCourierOrder = courierBookingRepository.save(courierBooking);
		savedSenderReceiverInfo.setCourierBooking(savedCourierOrder);
		senderReceiverInfoRepository.save(savedSenderReceiverInfo);

		// CONVERTING INTO DTO
		CourierBookingDto savedCourierOrderDto = new CourierBookingDto();
		savedCourierOrderDto.setCourierBookingId(savedCourierOrder.getCourierBookingId());
		savedCourierOrderDto.setSenderReceiverInfoId(savedSenderReceiverInfo.getId());
		savedCourierOrderDto.setUserId(savedCourierOrder.getUser().getUserId());
		savedCourierOrderDto.setGst(savedCourierOrder.getGst());
		savedCourierOrderDto.setPricePerKm(savedCourierOrder.getPricePerKm());
		savedCourierOrderDto.setSenderName(savedCourierOrder.getSenderName());
		savedCourierOrderDto.setReceiverName(savedCourierOrder.getReceiverName());
		savedCourierOrderDto.setBaseAmount(savedCourierOrder.getBaseAmount());
		savedCourierOrderDto.setTotalAmount(savedCourierOrder.getTotalAmount());
		savedCourierOrderDto.setCourierId(savedCourierOrder.getCourierDriver().getCourierId());

		return savedCourierOrderDto;
	}

	@Override
	public CourierBookingDto generateBookingInvoiceWithPromoCode(String promoCode, Long courierId) {

		CourierBooking courierBooking = courierBookingRepository.findById(courierId)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found", ""));

		PromoCode currentPromoCode = promoCodeRepository.findByCode(promoCode)
				.orElseThrow(() -> new ResourceNotFoundException("Promo code invalid", ""));

		// SETTING THE COURIER BOOKING ENTITY
		courierBooking.setPromoCode(promoCode);

		// DISCOUNTED AMOUNT
		courierBooking.setTotalAmount(courierBooking.getBaseAmount()
				.subtract(currentPromoCode.getDiscountPercentage().multiply(courierBooking.getBaseAmount())));

		CourierBooking savedCourierOrder = courierBookingRepository.save(courierBooking);

		// CONVERTING INTO DTO
		CourierBookingDto savedCourierOrderDto = new CourierBookingDto();
		savedCourierOrderDto.setCourierBookingId(savedCourierOrder.getCourierBookingId());
		savedCourierOrderDto.setSenderReceiverInfoId(savedCourierOrder.getSenderReceiverInfo().getId());
		savedCourierOrderDto.setUserId(savedCourierOrder.getUser().getUserId());
		savedCourierOrderDto.setGst(savedCourierOrder.getGst());
		savedCourierOrderDto.setPricePerKm(savedCourierOrder.getPricePerKm());
		savedCourierOrderDto.setSenderName(savedCourierOrder.getSenderName());
		savedCourierOrderDto.setReceiverName(savedCourierOrder.getReceiverName());
		savedCourierOrderDto.setBaseAmount(savedCourierOrder.getBaseAmount());
		savedCourierOrderDto.setTotalAmount(savedCourierOrder.getTotalAmount());
		savedCourierOrderDto.setPromoCode(savedCourierOrder.getPromoCode());
		savedCourierOrderDto.setCourierId(savedCourierOrder.getCourierDriver().getCourierId());

		return savedCourierOrderDto;
	}

	@Override
	public CourierBookingDto removePromoCodeFromCourierOrder(Long id) {
		CourierBooking courierBooking = courierBookingRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found", ""));

		courierBooking.setTotalAmount(courierBooking.getBaseAmount());

		CourierBooking savedCourierOrder = courierBookingRepository.save(courierBooking);

		CourierBookingDto savedCourierOrderDto = new CourierBookingDto();
		savedCourierOrderDto.setCourierBookingId(savedCourierOrder.getCourierBookingId());
		savedCourierOrderDto.setSenderReceiverInfoId(savedCourierOrder.getSenderReceiverInfo().getId());
		savedCourierOrderDto.setUserId(savedCourierOrder.getUser().getUserId());
		savedCourierOrderDto.setGst(savedCourierOrder.getGst());
		savedCourierOrderDto.setPricePerKm(savedCourierOrder.getPricePerKm());
		savedCourierOrderDto.setSenderName(savedCourierOrder.getSenderName());
		savedCourierOrderDto.setReceiverName(savedCourierOrder.getReceiverName());
		savedCourierOrderDto.setBaseAmount(savedCourierOrder.getBaseAmount());
		savedCourierOrderDto.setTotalAmount(savedCourierOrder.getTotalAmount());
		savedCourierOrderDto.setCourierId(savedCourierOrder.getCourierDriver().getCourierId());

		return savedCourierOrderDto;
	}

	@Override
	public OrderResponse createOrder(CourierBookingDto courierBookingDto) {

		// FINDING THE BOOKING INVOICE
		CourierBooking courierBooking = courierBookingRepository.findById(courierBookingDto.getCourierBookingId())
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found", ""));
		try {
			com.rido.entity.CourierOrder courierOrder = new com.rido.entity.CourierOrder();
			courierOrder.setCourierBooking(courierBooking);
			courierOrder.setOrderStatus(CourierOrderStatus.IN_PROGRESS);
			courierOrder.setOrderTrackingNumber(UUID.randomUUID().toString());
			courierOrder.setTotalPrice(courierBooking.getTotalAmount());

			// CONVERTING TOTAL AMOUNT TO PAISE
			int totalAmountInPaise = courierBooking.getTotalAmount().multiply(BigDecimal.valueOf(100)).intValue();

			RazorpayClient razorpayClient = new RazorpayClient(razorPayConfiguration.getRazorpayKey(),
					razorPayConfiguration.getRazorpaySecret());

			org.json.JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", totalAmountInPaise);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "Receipt #1");

			// RAZORPAY ORDER CREATION
			Order currentOrder = razorpayClient.orders.create(orderRequest);
			System.out.println("Order created: " + currentOrder.toString());

			/*
			 * DateTimeFormatter dateTimeFormatter =
			 * DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"); String formattedDateTime =
			 * LocalDateTime.now().format(dateTimeFormatter); LocalDateTime parsedDateTime =
			 * LocalDateTime.parse(formattedDateTime, dateTimeFormatter);
			 */

			courierOrder.setOrderId(currentOrder.get("id"));
			com.rido.entity.CourierOrder savedOrder = courierOrderRepository.save(courierOrder);

			// MAPPING THE ORDER DATA INTO MAP
			Map<String, Object> orderData = new HashMap<>();
			orderData.put("id", currentOrder.get("id"));
			orderData.put("amount", currentOrder.get("amount"));
			orderData.put("currency", currentOrder.get("currency"));
			orderData.put("receipt", currentOrder.get("receipt"));
			orderData.put("status", currentOrder.get("status"));
			orderData.put("notes", currentOrder.get("notes"));
			orderData.put("created_at", currentOrder.get("created_at"));

			// SETTING THE ORDER DATA INTO ORDER RESPONSE
			OrderResponse orderResponse = new OrderResponse();
			orderResponse.setOrderTrackingNumber(savedOrder.getOrderTrackingNumber());
			orderResponse.setStatus(savedOrder.getOrderStatus());
			orderResponse.setOrderData(orderData);

			if (currentOrder.get("status").equals("created")) {
				orderResponse.setMessage("Booking created, Please make payment to confirm your order...");
			} else {
				orderResponse.setMessage("Booking not created, try again");
			}
			return orderResponse;
		} catch (RazorpayException razorpayException) {
			logger.error("Error creating razorpay order : {}", razorpayException.getMessage(), razorpayException);
			return null;
		} catch (NumberFormatException numberFormatException) {
			logger.error("Invalid number format : {}", numberFormatException.getMessage(), numberFormatException);
			return null;
		} catch (RuntimeException runtimeException) {
			logger.error("Runtime exception occurred while handling request: {}", runtimeException.getMessage(),
					runtimeException);
			return null;
		}
	}

	@Override
	@Transactional
	public PaymentResponse verifyPayment(Map<String, Object> paymentData) {

		/*
		 * EXAMPLE RAZORPAY PAYMENT RESPONSE IF THE RAZORPAY SIGNATURE IS NULL PAYMENT
		 * IS FAILED, OTHERWISE SUCCESS
		 * 
		 * { "razorpay_order_id": "order_O0QcwAvFDx2n07", "razorpay_payment_id":
		 * "pay_O0Qd35Nd3gMUt9", "razorpay_signature":
		 * "3d3e7f00d93255ae3f0768dc13aedca346d5b1805ebd905c6089393039407ab0" }
		 */

		// DETERMINING PAYMENT STATUS FROM RAZORPAY SIGNATURE
		String razorpaySignature = (String) paymentData.get("razorpay_signature");
		CourierPaymentStatus paymentStatus = (razorpaySignature == null || razorpaySignature.isEmpty())
				? CourierPaymentStatus.FAILED
				: CourierPaymentStatus.SUCCESSFUL;

		// FINDING CURRENT ORDER FROM DATABASE
		CourierOrder runningOrder = courierOrderRepository.findByOrderId((String) paymentData.get("razorpay_order_id"));

		// VERIFYING THE ORDER WHETHER IS IT IS AVAILABLE OR NOT
		if (runningOrder == null) {
			throw new ResourceNotFoundException(
					"Order not found with id : " + (String) paymentData.get("razorpay_order_id"), "");
		}
		Optional<CourierBooking> currentBooking = courierBookingRepository
				.findById(runningOrder.getCourierBooking().getCourierBookingId());

		// UPDATING THE COURIER BOOKING STATUS
		if (paymentStatus.equals(CourierPaymentStatus.SUCCESSFUL)) {
			currentBooking.get().setIsConfirm(CourierBookingStatus.CONFIRMED);
		}

		// UPDATING THE ORDER STATUS
		runningOrder.setOrderStatus(paymentStatus.equals(CourierPaymentStatus.SUCCESSFUL) ? CourierOrderStatus.CONFIRMED
				: CourierOrderStatus.FAILED);

		// SETTING THE PAYMENT ATTRIBUTES
		UserCourierPayment currentPayment = new UserCourierPayment();
		currentPayment.setPaymentId((String) paymentData.get("razorpay_payment_id"));
		currentPayment.setAmount(runningOrder.getTotalPrice());
		currentPayment.setCourierOrder(runningOrder);
		currentPayment.setStatus(paymentStatus);
		currentPayment.setUser(runningOrder.getCourierBooking().getUser());

		UserCourierPayment savedPayment = userCourierPaymentRepository.save(currentPayment);
		courierBookingRepository.save(currentBooking.get());

		// SETTING THE CURRENT PAYMENT TO THE ORDER
		runningOrder.setPayment(savedPayment);
		CourierOrder savedOrder = courierOrderRepository.save(runningOrder);

		if (paymentStatus.equals(CourierPaymentStatus.FAILED)) {

			// DELETING THE PAYMENT RECORD IF PAYMENT FAILED
			userCourierPaymentRepository.delete(savedPayment);

			// DELETING THE ORDER RECORD IF PAYMENT FAILED
			courierOrderRepository.delete(savedOrder);

			// DELETING THE BOOKING RECORD IF PAYMENT FAILED
			courierBookingRepository.delete(runningOrder.getCourierBooking());

			// DELETING THE SENDER AND RECEIVER INFORMATION RECORD IF PAYMENT FAILED
			if (runningOrder.getCourierBooking().getSenderReceiverInfo() != null) {
				// DELETING THE SENDER AND RECEIVER INFO SAFELY
				senderReceiverInfoRepository.delete(runningOrder.getCourierBooking().getSenderReceiverInfo());
			}
		}
		return new PaymentResponse((String) paymentData.get("razorpay_payment_id"), paymentStatus);
	}

	// JYOTI

	public List<CourierTaskListDto> getCourierTasksByCourierId(Long courierId) {

		List<CourierBooking> courierBookings = courierBookingRepository.findByCourierDriver_CourierId(courierId);
		// Map the filtered bookings to DTOs
		return mapToDto(courierBookings);
	}

	private List<CourierTaskListDto> mapToDto(List<CourierBooking> courierBookings) {
		List<CourierTaskListDto> dtos = new ArrayList<>();
		for (CourierBooking booking : courierBookings) {
			CourierTaskListDto dto = new CourierTaskListDto();
			dto.setCourierBookingId(booking.getCourierBookingId());
			dto.setSenderName(booking.getSenderName());
			dto.setReciverName(booking.getReceiverName());
			dto.setSenderLocation(booking.getSenderReceiverInfo().getSenderLocation());
			dto.setReciverLocation(booking.getSenderReceiverInfo().getReceiverLocation());
			dto.setSenderPhoneNumber(booking.getSenderReceiverInfo().getSenderPhoneNumber());
			dto.setReciverPhoneNumber(booking.getSenderReceiverInfo().getReceiverPhoneNumber());
			dto.setExpectedTime(booking.getSenderReceiverInfo().getExpectedTime());
			dto.setUserId(booking.getSenderReceiverInfo().getUser().getUserId());
			// Add the DTO object to the list
			dtos.add(dto);
		}
		return dtos;
	}

	public String sendOtpByCourierForUser(Long bookingId) {

		// Generate verification code
		String verificationCode = locationImpl.generateRandomOtp();

		CourierBooking courierBooking = courierBookingRepository.findById(bookingId)
				.orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));

		// Extract phone number from SenderReceiverInfo
		String contactNo = courierBooking.getSenderReceiverInfo().getSenderPhoneNumber();

		SenderReceiverInfo senderInfo = senderReceiverInfoRepository
				.findById(courierBooking.getSenderReceiverInfo().getId())
				.orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: "));

		// Check if existing OTP record exists for the user (optional)
		Optional<ManageOtp> existingOtp = manageOtpRepository.findByUser_UserId(senderInfo.getUser().getUserId());

		// Create a new ManageOtp object if none exists
		ManageOtp manageOtp;
		if (existingOtp.isEmpty()) {
			manageOtp = new ManageOtp();
		} else {
			manageOtp = existingOtp.get();
		}

		// Set ManageOtp properties
		manageOtp.setUser(senderInfo.getUser());
		manageOtp.setUpdateOtp(verificationCode);

		// Save the ManageOtp record
		manageOtpRepository.save(manageOtp);

		// Send verification code via SMS
		locationImpl.sendVerificationCode(contactNo, verificationCode);

		return verificationCode;
	}

	// update
	@Override
	public CourierProfileDto getCourierProfileById(Long courierId) {

		Courier courierProfile = courierRepository.findById(courierId).orElseThrow();
		CourierProfileDto dto = new CourierProfileDto();
		dto.setProfileImg(courierProfile.getCourierDocument().getCourierDriverImage());
		dto.setFullName(courierProfile.getOwnerName());
		dto.setEmail(courierProfile.getEmail());
		dto.setAadharNo(courierProfile.getCourierDocument().getAadhaarNo());
		dto.setDlNumber(courierProfile.getCourierDocument().getLicence());
		dto.setPanCardNo(courierProfile.getCourierDocument().getPanCardNo());
		dto.setPhoneNo(courierProfile.getPhoneNo());
		dto.setAddress(courierProfile.getAddress());
		dto.setAccountNo(courierProfile.getCourierDocument().getAccountNo());
		dto.setAccountholderName(courierProfile.getCourierDocument().getAccountHolderName());
		dto.setIfscCode(courierProfile.getCourierDocument().getIFSCcode());
		dto.setBranchName(courierProfile.getCourierDocument().getBranchName());
		return dto;
	}

	// update
	@Override
	@Transactional
	public void editCourierProfile(Long courierId, String userImageUrl, String name) {

		Courier editcourier = courierRepository.findById(courierId).get();

		CourierDocument courierDocument = new CourierDocument();
		System.out.println(userImageUrl);
		if (userImageUrl != null && !userImageUrl.isEmpty()) {
			courierDocument.setCourierDriverImage(userImageUrl);
			courierDocument = courierDocumentRepository.save(courierDocument);
			editcourier.setCourierDocument(courierDocument);

		}
		System.out.println(name);
		if (name != null && !name.isEmpty()) {
			editcourier.setOwnerName(name);
		}
		courierRepository.save(editcourier);
	}

	public boolean verifyOtpForCourier1(Long userId, String enterOtp) {
		Optional<CourierBooking> courierbooking = courierBookingRepository.findById(userId);

		if (courierbooking.isPresent()) {

			CourierBooking courierBooking2 = courierbooking.get();

			User user = courierBooking2.getUser();

			Optional<ManageOtp> otpOptional = manageOtpRepository.findByUser_UserId(user.getUserId());

			if (otpOptional.isPresent()) {
				ManageOtp otp = otpOptional.get();

				if (otp.getUpdateOtp().equals(enterOtp)) {
					// Get the current date and time

//				CourierBooking courierbooking = courierBookingRepository.findByUser_UserId(userId).get();

					LocalDateTime currentDateTime = LocalDateTime.now();
					System.out.println(LocalDateTime.now());

					TimeDuration timeDuration = new TimeDuration();
					timeDuration.setStartDateTime(currentDateTime);

					timeDuration = timeDurationRepository.save(timeDuration);

					courierBooking2.setTimeDuration(timeDuration);

					courierBookingRepository.save(courierBooking2);

					return true;
				}
			}

		}
		return false;

	}

	@Override
	public boolean goForNextRide(Long courierId) {
		Optional<CourierBooking> optionalCourierBooking = courierBookingRepository.findById(courierId);

		if (optionalCourierBooking.isEmpty()) {
			return false; // No bookings found for the courierId
		}

		CourierBooking courierBooking = optionalCourierBooking.get();
		courierBooking.setRideOrderStatus(RideOrderStatus.COMPLETE);

		TimeDuration timeDuration = courierBooking.getTimeDuration();
		if (timeDuration != null) {
			timeDuration.setEndDateTime(LocalDateTime.now());
		}

		Courier courier = courierBooking.getCourierDriver();
		if (courier != null) {
			courier.setVehicleStatus(VehicleStatus.AVAILABLE);
			courierRepository.save(courier);
		}

		courierBookingRepository.save(courierBooking);
		return true;
	}

	@Override
	public List<CourierRideHistoryDto> getCourierRideHistory(Long courierId) {
		List<CourierBooking> completedBookings = courierBookingRepository
				.findByCourierDriver_CourierIdAndRideOrderStatus(courierId, RideOrderStatus.COMPLETE);
		List<CourierRideHistoryDto> rideHistoryList = new ArrayList<>();

		for (CourierBooking booking : completedBookings) {
			CourierRideHistoryDto dto = MapEntityToDto(booking);
			rideHistoryList.add(dto);
		}

		return rideHistoryList;
	}

	public CourierRideHistoryDto MapEntityToDto(CourierBooking courierBooking) {
		CourierRideHistoryDto dto = new CourierRideHistoryDto();
		TimeDuration time = courierBooking.getTimeDuration();
		dto.setStartTime(time.getStartDateTime());
		dto.setEndTime(time.getEndDateTime());
		dto.setId(courierBooking.getCourierBookingId());
		dto.setPrice(courierBooking.getTotalAmount());
		SenderReceiverInfo senderReceiverInfo = courierBooking.getSenderReceiverInfo();
		if (senderReceiverInfo != null) {
			dto.setSenderAddress(senderReceiverInfo.getSenderAddress());
			dto.setReceiveraddress(senderReceiverInfo.getReceiverAddress());
			dto.setStatus(courierBooking.getRideOrderStatus());
		}

		return dto;
	}

	@Override
	@Modifying
	@Transactional
	@Scheduled(cron = "0 0 */2 * * ?") // every 2 hours

//	@Scheduled(cron = "*/5 * * * * ?") // every 5 second Cron Expression
	public void deleteNotConfirmedBookings() {
		courierBookingRepository.deleteByIsConfirm(CourierBookingStatus.NOT_CONFIRMED);
	}

	@Override
	public Object returnCourierVehicle(String vehicleNo, Long courierId, String bikeCondition, String reason) {
		CourierEbike existingCourierEbike = courierEbikeRepository.findByVehicleNo(vehicleNo);
		Courier courier = courierRepository.findById(courierId).get();
		if (ReturnCourierVehicle.BikeCondition.WORST.equals(bikeCondition.toUpperCase())) {
			existingCourierEbike.setVehicleStatus(VehicleStatus.MAINTENANCE);
		}
		existingCourierEbike.setVehicleStatus(VehicleStatus.AVAILABLE);
		List<ReturnCourierVehicle> returnCourierVehicleList = returnCourierVehicleRepository.findAll();
		Collections.reverse(returnCourierVehicleList);
		ReturnCourierVehicle returnCourierVehicle = returnCourierVehicleList.stream()
				.filter(details -> details.getCourier().getCourierId().equals(courierId)).findFirst().get();

		if (returnCourierVehicle.equals(null)) {
			throw new ResourceNotFoundException("There is no courier booking details with this courier : " + courierId,
					"601");
		}
		returnCourierVehicle.setCarReturnTime(LocalDateTime.now());
		System.out.println("return time=" + LocalDateTime.now());
		returnCourierVehicle.setReason(reason);
		courier.setVehicleAssignStatus(VehicleAssignStatus.CHECKOUT);
		returnCourierVehicle.setBikeCondition(ReturnCourierVehicle.BikeCondition.valueOf(bikeCondition.toUpperCase()));
		courierRepository.save(courier);
		courierEbikeRepository.save(existingCourierEbike);
		returnCourierVehicleRepository.save(returnCourierVehicle);
		return "Vehicle returned to the hub";
	}

	@Override
	public Object assignBikeToCourier(Long courierEbikeId, Long courierId, String bikeCondition, Long hubId) {
		CourierEbike existingVehicle = courierEbikeRepository.findById(courierEbikeId).orElseThrow(
				() -> new ResourceNotFoundException("Vehicle with this id : " + courierEbikeId + " is not present",
						"601"));

		Courier courier = courierRepository.findById(courierId).orElseThrow(
				() -> new ResourceNotFoundException("Courier with this id : " + courierId + " is not present", "601"));

		existingVehicle.setVehicleStatus(VehicleStatus.ENGAGED);
		courierEbikeRepository.save(existingVehicle);

		courier.setVehicleAssignStatus(VehicleAssignStatus.CHECKIN);
		courierRepository.save(courier);

		ReturnCourierVehicle returnCourierVehicle = ReturnCourierVehicle.builder().carAssignTime(LocalDateTime.now())
				.carReturnTime(null).reason(null).courier(courier)
				.hub(hubRepository.findById(hubId).orElseThrow(
						() -> new ResourceNotFoundException("Hub with this id : " + hubId + " is not present", "602")))
				.courierEbike(existingVehicle)
				.bikeCondition(ReturnCourierVehicle.BikeCondition.valueOf(bikeCondition.toUpperCase())).build();
		returnCourierVehicleRepository.save(returnCourierVehicle);

		return returnCourierVehicle;
	}

	@Override
	public List<Courier> findCourierForAssignBike() {

		List<Courier> availableDriversForAssiginBike = courierRepository.findAll().stream()
				.filter(courier -> courier.getVehicleAssignStatus().equals(VehicleAssignStatus.CHECKOUT))
				.collect(Collectors.toList());
		if (availableDriversForAssiginBike.isEmpty()) {
			throw new ResourceNotFoundException("There is no driver available to assign", "601");
		}
		return availableDriversForAssiginBike;

	}

	@Override
	public Map<LocalDate, Integer> calculateEbikeWorkingHours(List<ReturnCourierVehicle> assignCars,
			List<ReturnCourierVehicle> returnCars) {
		Map<LocalDate, Integer> workingHoursMap = new HashMap<>();

		for (ReturnCourierVehicle assignCar : assignCars) {
			LocalDate day = assignCar.getCarAssignTime().toLocalDate();

			ReturnCourierVehicle returnCar = returnCars.stream()
					.filter(rc -> rc.getCarReturnTime().toLocalDate().equals(day)).findFirst().orElse(null);

			if (returnCar != null) {

				LocalDateTime openingTime = assignCar.getCarAssignTime();
				LocalDateTime returnTime = returnCar.getCarReturnTime();
				int workingHours = calculateHoursBetween(openingTime, returnTime);
				workingHoursMap.put(day, workingHours);
			}
		}

		return workingHoursMap;
	}

	private int calculateHoursBetween(LocalDateTime openingTime, LocalDateTime returnTime) {
		return (int) java.time.Duration.between(openingTime, returnTime).toHours();
	}

	@Override
	public int calculateTotalPayment(Map<LocalDate, Integer> workingHoursMap) {
	    int totalPayment = 0;

	    for (int workingHours : workingHoursMap.values()) {
	        int dailyPayment = calculatePayment(workingHours);
	        totalPayment += dailyPayment;
	    }
	    return totalPayment;
	}

	private int calculatePayment(int workingHours) {
	    int hourlyRate = 200; 
	    return workingHours * hourlyRate;
	}

	@Override
	public void courierEbikeDocumentUpload(Long courierId, CourierEbikeDataDto courierEbikeDataDto,
			String courierDriverImageUrl, String licenceImgUrl, String passbookImageUrl) {
		CourierDocument data = new CourierDocument();

		Courier courier = courierRepository.findById(courierId).get();
//		courier.setApproveStatus(ApproveStatus.PENDING);
//		courier.setCourierDocument(data);
//		data.setCourier(courier);
//		courierRepository.save(courier);

		if (courierEbikeDataDto.getAccountHolderName() != null
				&& !courierEbikeDataDto.getAccountHolderName().isEmpty()) {
			data.setAccountHolderName(courierEbikeDataDto.getAccountHolderName());
		}

		if (courierEbikeDataDto.getAccountNo() != null && !courierEbikeDataDto.getAccountNo().isEmpty()) {
			data.setAccountNo(courierEbikeDataDto.getAccountNo());
		}

		if (courierEbikeDataDto.getIFSCcode() != null && !courierEbikeDataDto.getIFSCcode().isEmpty()) {
			data.setIFSCcode(courierEbikeDataDto.getIFSCcode());
		}

		if (courierEbikeDataDto.getAadhaarNo() != null && !courierEbikeDataDto.getAadhaarNo().isEmpty()) {
			data.setAadhaarNo(courierEbikeDataDto.getAadhaarNo());
		}

		if (courierEbikeDataDto.getBranchName() != null && !courierEbikeDataDto.getBranchName().isEmpty()) {
			data.setBranchName(courierEbikeDataDto.getBranchName());
		}

		if (courierDriverImageUrl != null && !courierDriverImageUrl.isEmpty()) {
			data.setCourierDriverImage(courierDriverImageUrl);
		}
		if (licenceImgUrl != null && !licenceImgUrl.isEmpty()) {
			data.setLicence(licenceImgUrl);
		}

		if (passbookImageUrl != null && !passbookImageUrl.isEmpty()) {
			data.setPassbook(passbookImageUrl);
		}

		courierDocumentRepository.save(data);
		courier.setApproveStatus(ApproveStatus.PENDING);
		courier.setCourierDocument(data);
		courierRepository.save(courier);

	}

	@Override
	public void saveTotalPayment(Long courierId, int totalPayment, LocalDate date) {
		Courier courier = courierRepository.findById(courierId)
				.orElseThrow(() -> new IllegalArgumentException("Courier not found"));

		CourierEbikedriverPayment driverPayment = new CourierEbikedriverPayment();
		driverPayment.setAmount(String.valueOf(totalPayment));
		driverPayment.setDate(date);
		driverPayment.setCourier(courier);
		driverPayment.setHub(courier.getHub());
		driverPayment.setPaymentStatus(CourierEbikeDriverPaymentStatus.PENDING);

		courierEbikedriverPaymentRepo.save(driverPayment);
	}

	public List<TransportAllListDto> getTransportListByHubId(Long hubId) {
		List<TransportAllListDto> transportAllListDtos = new ArrayList<>();

		List<Courier> couriers = courierRepository.findByHub_HubId(hubId);

		// Iterate through couriers
		for (Courier courier : couriers) {

			List<CourierBooking> courierBookings = courierBookingRepository
					.findByCourierDriver_CourierId(courier.getCourierId());
			System.out.println(courierBookings + "jkl");
			for (CourierBooking courierBooking : courierBookings) {
				// Check if SenderReceiverInfo is not null
				if (courierBooking.getSenderReceiverInfo() != null) {
					TransportAllListDto dto = new TransportAllListDto();

					dto.setDriverName(courier.getOwnerName());
					dto.setSenderName(courierBooking.getSenderReceiverInfo().getSenderName());
					dto.setSenderAddress(courierBooking.getSenderReceiverInfo().getSenderAddress());
					dto.setReceiverAddress(courierBooking.getSenderReceiverInfo().getReceiverAddress());
					dto.setSenderPhoneNumber(courierBooking.getSenderReceiverInfo().getSenderPhoneNumber());
					dto.setTotalDistance(courierBooking.getSenderReceiverInfo().getTotalDistance());
					dto.setExpectedTime(courierBooking.getSenderReceiverInfo().getExpectedTime());
					dto.setVehicleType(courierBooking.getSenderReceiverInfo().getVehicleType());
					dto.setIsConfirm(courierBooking.getIsConfirm());
					dto.setStartDateTime(courierBooking.getTimeDuration().getStartDateTime());

					transportAllListDtos.add(dto);
				}
			}
		}

		return transportAllListDtos;
	}

	@Override
	public List<TransportAllListDto> getTransportListByHubIdAndVehicleType(Long hubId,
			DriverAndVehicleType vehicleType) {
		List<TransportAllListDto> transportAllListDtos = new ArrayList<>();
		List<Courier> couriers = courierRepository.findByHub_HubId(hubId);

		for (Courier courier : couriers) {
			List<CourierBooking> courierBookings = courierBookingRepository
					.findByCourierDriver_CourierId(courier.getCourierId());

			// Iterate through courier bookings
			for (CourierBooking courierBooking : courierBookings) {
				// Check if SenderReceiverInfo is not null and vehicleType matches the requested
				// type
				if (courierBooking.getSenderReceiverInfo() != null
						&& courierBooking.getSenderReceiverInfo().getVehicleType() == vehicleType) {

					TransportAllListDto dto = new TransportAllListDto();
					dto.setDriverName(courier.getOwnerName());
					dto.setSenderName(courierBooking.getSenderReceiverInfo().getSenderName());
					dto.setSenderAddress(courierBooking.getSenderReceiverInfo().getSenderAddress());
					dto.setReceiverAddress(courierBooking.getSenderReceiverInfo().getReceiverAddress());
					dto.setSenderPhoneNumber(courierBooking.getSenderReceiverInfo().getSenderPhoneNumber());
					dto.setTotalDistance(courierBooking.getSenderReceiverInfo().getTotalDistance());
					dto.setExpectedTime(courierBooking.getSenderReceiverInfo().getExpectedTime());
					dto.setVehicleType(courierBooking.getSenderReceiverInfo().getVehicleType());
					dto.setIsConfirm(courierBooking.getIsConfirm());
					dto.setStartDateTime(courierBooking.getTimeDuration().getStartDateTime());
					// Add TransportAllListDto to the list
					transportAllListDtos.add(dto);
				}
			}
		}

		return transportAllListDtos;
	}

	public List<CourierEbikeDto> getCourierEbikeListByHubId(Long hubId) {
		List<CourierEbike> courierEbikeList = courierEbikeRepository.findByHub_HubId(hubId);

		return courierEbikeList.stream().map(this::mapCourierEbikeToDto).collect(Collectors.toList());
	}

	@Override
	public CourierEbikeDto getCourierEbikeByIdAndHubId(Long id, Long hubId) {
		Optional<CourierEbike> optionalCourierEbike = courierEbikeRepository.findById(id);

		if (optionalCourierEbike.isPresent()) {
			CourierEbike courierEbike = optionalCourierEbike.get();
			return mapCourierEbikeToDto(courierEbike);
		} else {
			return null; // Return null or throw an exception if not found
		}
	}

	private CourierEbikeDto mapCourierEbikeToDto(CourierEbike courierEbike) {
		CourierEbikeDto dto = new CourierEbikeDto();
		dto.setCourierEbikeId(courierEbike.getCourierEbikeId());
		//dto.setBattery(courierEbike.getBattery());
		//dto.setChargingTime(courierEbike.getChargingTime());
		dto.setInsuranceNo(courierEbike.getInsuranceNo());
		dto.setVehicleName(courierEbike.getVehicleName());
		dto.setVehicleNo(courierEbike.getVehicleNo());
		dto.setWeight(courierEbike.getWeight());
		dto.setTopSpeed(courierEbike.getTopSpeed());
		dto.setRc(courierEbike.getRc());
		dto.setEbikeImage(courierEbike.getEbikeImage());
		return dto;

	}

	
	
	
	@Override
	public Object acceptParcelRequest(Long courierBookingId) throws UserNotFoundException {
		CourierBooking courierBookingRequest = courierBookingRepository.findById(courierBookingId).orElseThrow(
				() -> new ResourceNotFoundException("There is no courier booking  request available", "601"));
		String senderMobileNumber = courierBookingRequest.getSenderReceiverInfo().getReceiverPhoneNumber();
		User existingUser = userRepository.findByPhoneNo(senderMobileNumber).orElseThrow(
				() -> new UserNotFoundException("user not found with this phone number : " + senderMobileNumber));
		Long userId = existingUser.getUserId();
		webSocketHandler.broadcastMessage( "Parcel request accepted with ID: " + courierBookingId);
		return courierBookingRequest;
	}

	@Override
	public Object setCourierDriverOngoing(Long courierId) {
		Courier existingCourierDriver = courierRepository.findByCourierId(courierId)
				.orElseThrow(() -> new DriverNotFoundException("There is no driver available here"));
		existingCourierDriver.setStatus(Status.ONGOING);
		courierRepository.save(existingCourierDriver);
		return existingCourierDriver;
	}

	public List<CourierDto> getAllTwoWheelers(Long userId, SenderReceiverInfoDto senderReceiverInfoDto) {

		senderReceiverInfoDto.setUserId(userId);
		senderReceiverInfoDto.setTotalDistance(Math.round(Double
				.parseDouble(decimalFormat.format(CalculateDistance.distance(senderReceiverInfoDto.getSenderLatitude(),
						senderReceiverInfoDto.getSenderLongitude(), senderReceiverInfoDto.getReceiverLatitude(),
						senderReceiverInfoDto.getReceiverLongitude())))));

		// fetching all vehicle by selected type
		List<CourierEbike> eBikeList = courierEbikeRepository.findAllByVehicleStatus(VehicleStatus.AVAILABLE);

		if (eBikeList.isEmpty()) {
			throw new ResourceNotFoundException("Vehicles not available", "");
		}

		List<CourierDto> availableTwoWheelerList = new ArrayList<>();

		for (CourierEbike availableVehicle : eBikeList) {

			Optional<Courier> currentDriverOpt = courierRepository
					.findByCourierId(availableVehicle.getCourier().getCourierId());

			if (currentDriverOpt.isPresent()) {

				double totalTravelTime = (senderReceiverInfoDto.getTotalDistance() / availableVehicle.getTopSpeed())
						* 60;
				String hours = ConvertToHoursAndMinutes.convertToHoursAndMinutes((int) totalTravelTime).substring(0, 2);
				String minutes = ConvertToHoursAndMinutes.convertToHoursAndMinutes((int) totalTravelTime).substring(3,
						5);
				senderReceiverInfoDto.setExpectedTime(hours + " Hours " + minutes + " minutes");

				Courier currentDriver = currentDriverOpt.get();

				// Calculate distance from sender
				double distanceFromSender = CalculateDistance.distance(senderReceiverInfoDto.getSenderLatitude(),
						senderReceiverInfoDto.getSenderLongitude(), currentDriver.getCourierDriverLatitude(),
						currentDriver.getCourierDriverLongitude());
				currentDriver.setDistanceFromSender(Double.parseDouble(decimalFormat.format(distanceFromSender)));

				// Calculate price including GST
				BigDecimal baseFare = availableVehicle.getPricePerKm()
						.multiply(BigDecimal.valueOf(senderReceiverInfoDto.getTotalDistance()));
				BigDecimal gstAmount = baseFare.multiply(BigDecimal.valueOf(GST)).divide(BigDecimal.valueOf(100), 2,
						RoundingMode.HALF_UP);
				currentDriver.setPrice(baseFare.add(gstAmount));

				// Create and populate CourierDto
				CourierDto courierDto = new CourierDto();
				courierDto.setCourierId(currentDriver.getCourierId());
				courierDto.setVehicleType(currentDriver.getVehicleType());
				courierDto.setVehicleNo(availableVehicle.getVehicleNo());
				courierDto.setPrice(currentDriver.getPrice());
				courierDto.setDistanceFromSender(currentDriver.getDistanceFromSender());
				courierDto.setWeight(availableVehicle.getWeight());
				courierDto.setVehicleCategory(currentDriver.getVehicleCategory());
				courierDto.setHubId(currentDriver.getHub().getHubId());
				courierDto.setVehicleImage(availableVehicle.getEbikeImage());
				courierDto.setVehicleStatus(availableVehicle.getVehicleStatus());
				courierDto.setSenderReceiverInfoDto(senderReceiverInfoDto);

				// Calculate time to reach sender
				double reachingTime = (currentDriver.getDistanceFromSender() / 40.0) * 60;
				String reachingTimeFormatted = ConvertToHoursAndMinutes.convertToHoursAndMinutes((int) reachingTime);
				String hoursToReach = reachingTimeFormatted.substring(0, 2);
				String minutesToReach = reachingTimeFormatted.substring(3, 5);
				courierDto.setTimeAwayFromSender(hoursToReach + " Hours " + minutesToReach + " minutes");

				availableTwoWheelerList.add(courierDto);
			}
		}

		availableTwoWheelerList.sort(Comparator.comparing(CourierDto::getDistanceFromSender));

		return availableTwoWheelerList;
	}

	public String sendOtpByCourierForReciver(Long bookingId) {
		// Generate verification code
		String verificationCode = locationImpl.generateRandomOtp();

		// Get the CourierBooking by its ID
		CourierBooking courierBooking = courierBookingRepository.findById(bookingId)
				.orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));

		// Extract phone number from SenderReceiverInfo
		String receiverPhoneNumber = courierBooking.getSenderReceiverInfo().getReceiverPhoneNumber();

		// Send verification code via SMS
		locationImpl.sendVerificationCode(receiverPhoneNumber, verificationCode);

		// Return the verification code
		return verificationCode;
	}

	public Map<String, String> sendSuccessMessage(Long bookingId) {
		// Get the CourierBooking by its ID
		CourierBooking courierBooking = courierBookingRepository.findById(bookingId)
				.orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));

		// Get sender and receiver phone numbers from SenderReceiverInfo
		String receiverPhoneNumber = courierBooking.getSenderReceiverInfo().getReceiverPhoneNumber();
		String senderPhoneNumber = courierBooking.getSenderReceiverInfo().getSenderPhoneNumber();

		// Message content
		String senderMessage = "Your courier has been dropped off at your destination.";
		String receiverMessage = "You have received a courier.";

		// Send messages to sender and receiver
		locationImpl.sendVerificationCode(senderPhoneNumber, senderMessage);
		locationImpl.sendVerificationCode(receiverPhoneNumber, receiverMessage);

		// Construct and return response data
		Map<String, String> responseData = new HashMap<>();
		responseData.put("sender", senderPhoneNumber + ": " + senderMessage);
		responseData.put("receiver", receiverPhoneNumber + ": " + receiverMessage);
		return responseData;
	}

	@Override
	public ResponseLogin getCourierByPhoneNo(String phoneNo) {

		Courier courier = courierRepository.findByPhoneNo(phoneNo).orElseThrow();

		if (courier != null) {

			ResponseLogin response = new ResponseLogin();
			response.setUserId(courier.getCourierId());
			response.setEmail(courier.getEmail());
			response.setPhoneNo(courier.getPhoneNo());
			response.setName(courier.getOwnerName());
			response.setHubId(courier.getHub().getHubId());
			response.setApproveStatus(courier.getApproveStatus());
			response.setDriverType(courier.getVehicleType());
			response.setStatus(courier.getStatus());
//			response.setUserName(user.getUsername());

			return response;

		}

		return null;
	}

}