package com.rido.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.razorpay.RazorpayException;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.dto.ChangePasswordRequestDto;
import com.rido.dto.ContactUsRequestDto;
import com.rido.dto.PasswordRequestDto;
import com.rido.dto.UserAddressRequestDto;
import com.rido.dto.UserEmailResponseDto;
import com.rido.dto.UserLocationChooseDto;
import com.rido.dto.UserSetNameRequestDto;
import com.rido.dto.UserUpdateRequestDto;
import com.rido.dto.VehicleFareDTO;
import com.rido.entity.Driver;
import com.rido.entity.User;
import com.rido.entity.UserCourierPayment;
import com.rido.entityDTO.ResponseLogin;

@Service
public interface UserService {

	public ResponseLogin getByEmail(String email);

	public ResponseLogin getByPhoneno(String phoneno);

	public boolean setName(Long userId, UserSetNameRequestDto userSetNameRequestDto);

	public boolean changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto);

	public String contactUs(ContactUsRequestDto contactUsRequestDto);

	public boolean verifyEmailOtp(Long userId, String otp);

//	public User signwithPhone(User user);

	public UserEmailResponseDto signwithEmail(User user);

	public User signwithGoogle(User user);

//	public String generateOtp(String otp);

	String getotpByphone(String phoneNo);

	boolean verifyotp(String phoneNo, String userOTP);

	public User edituserprofile(Long id, User user);

	public String forgetPasswordgenerateOtp();

//	public String forgetPasswordByPhoneNo(String mobileNo);

	public boolean setNewPassword(Long userId, PasswordRequestDto passworddto);

	public List<VehicleFareDTO> calculateFares(double distance);

	public String updateUserProfile(Long id, UserUpdateRequestDto userDataDto, String s3Url)
			throws UserNotFoundException;

	public String updateUserLocation(Long userId, UserLocationChooseDto userlocation) throws UserNotFoundException;

	public String userPickupAddress(Long userId, UserAddressRequestDto userpickupaddress);

	public UserLocationChooseDto userLocation(Long userId) throws UserNotFoundException;

	public String forgetPassword(String phoneNo);

	public boolean forgetPasswordVerify(Long userId, String forgotOtp);

//	public void updateEmailByUserId(Long userId, String newEmail);

	public List<Driver> bookCar(Long userId, double latitude, double longitude);

	public void changePhoneNo(Long userId, String newPhoneNo, String email);

//	public void changePhoneNoDriver(Long userId, String newPhoneNo);

	public boolean changePhoneNoUser(Long userId, String newPhoneNo);

	public boolean changeEmailUser(Long userId, String newEmail);

	public Object cancelBooking(Long bookingId , String reason) throws RazorpayException;

	Object cancelRentalBooking(Long rentalBookingId, String reason) throws RazorpayException;

	public List<UserCourierPayment> getCourierBookingPaymentHistory(Long userId);

}
