package com.rido.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rido.config.TwilioConfig;
import com.rido.entity.Booking;
import com.rido.entity.CancellationReason;
import com.rido.entity.Driver;
import com.rido.entity.User;
import com.rido.entity.enums.RideOrderStatus;
import com.rido.entity.enums.Status;
import com.rido.repository.BookingRepository;
import com.rido.repository.CancellationReasonRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.UserRepository;
import com.rido.service.CancellationService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class CancellationServiceImpl implements CancellationService {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private CancellationReasonRepository cancellationReasonRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private TwilioConfig twilioConfig;
	
	
	
	@Override
    public String cancelRideAndNotifyUser(Long userId, String reason ,Long driverId) {
    	
//    	Find the Driver
          Driver driver = driverRepository.findById(driverId).orElseThrow();	
          User user = userRepository.findById(userId).orElseThrow();
     
          String phoneNo = user.getPhoneNo();
	        // Create a new cancellation reason
	     
	        
	        CancellationReason cancellationReason = new CancellationReason();
	        cancellationReason.setReason(reason);
	        cancellationReason.setUser(user);
	        cancellationReason.setDriver(driver);

//	        driver.setStatus("Available");
	        
	        driverRepository.save(driver);
	        String sendReasonDriver = sendReasonDriver(phoneNo,reason);
	        // Save the cancellation reason
	        cancellationReasonRepository.save(cancellationReason);
			
	        
	        return sendReasonDriver;
	       

       
    }

	public String sendReasonDriver(String contactNo, List<String> reasons) {
		if (contactNo == null || twilioConfig == null || twilioConfig.getAccountSid() == null
				|| twilioConfig.getAuthToken() == null || twilioConfig.getTrailNumber() == null) {
			// Handle null values or misconfiguration
			return "Not found User with this contact number " + contactNo;
		}

		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());

		// Construct the message with all the cancellation reasons
		StringBuilder messageBuilder = new StringBuilder("Reasons for cancellation:");
		for (String reason : reasons) {
			messageBuilder.append("\n- ").append(reason);
		}
		String messageText = messageBuilder.toString();

		// Send the cancellation reason via SMS using Twilio
		Message message = Message.creator(new com.twilio.type.PhoneNumber(contactNo),
				new com.twilio.type.PhoneNumber(twilioConfig.getTrailNumber()), messageText).create();

		return "Cancellation reasons successfully sent: " + messageText;
	}

	@Override
	public String cancelRideAndNotifyUser(Long id, List<String> reasons) {

		Booking booking = bookingRepository.findById(id).orElseThrow();
		User user = booking.getUser();
		Driver driver = booking.getDriver();

		String phoneNo = user.getPhoneNo();

		// Create a new cancellation reason for each selected reason
		for (String reason : reasons) {
			CancellationReason cancellationReason = new CancellationReason();
			cancellationReason.setReason(reason);
			cancellationReason.setUser(user);
			cancellationReason.setDriver(driver);
			booking.setRideOrderStatus(RideOrderStatus.CANCELLED);
			// Save the cancellation reason
			bookingRepository.save(booking);
			cancellationReasonRepository.save(cancellationReason);

		}

		// Update driver status
		driver.setStatus(Status.AVAILABLE);
		driverRepository.save(driver);

		// Send notification to the user
		String sendReasonDriver = sendReasonDriver(phoneNo, reasons);

		return sendReasonDriver;
	}

//	public String sendReasonDriver(String contactNo, List<String> reasons) {
//		if (contactNo == null || twilioConfig == null || twilioConfig.getAccountSid() == null
//				|| twilioConfig.getAuthToken() == null || twilioConfig.getTrailNumber() == null) {
//			// Handle null values or misconfiguration
//			return "Not found User with this contact number " + contactNo;
//		}
//
//		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
//
//		// Construct the message with all the cancellation reasons
//		StringBuilder messageBuilder = new StringBuilder("Reasons for cancellation:");
//		for (String reason : reasons) {
//			messageBuilder.append("\n- ").append(reason);
//		}
//		String messageText = messageBuilder.toString();
//
//		// Send the cancellation reason via SMS using Twilio
//		Message message = Message.creator(new com.twilio.type.PhoneNumber(contactNo),
//				new com.twilio.type.PhoneNumber(twilioConfig.getTrailNumber()), messageText).create();
//
//		return "Cancellation reasons successfully sent: " + messageText;
//	}

	@Override
	public String cancelRideAndNotifyDriver(Long userId, List<String> reasons, Long driverId) {

		// Find the driver
		Driver driver = driverRepository.findById(driverId).orElseThrow();
		User user = userRepository.findById(userId).orElseThrow();

		String phoneNo = driver.getPhoneNo();
		// Create a new cancellation reason
		CancellationReason cancellationReason = new CancellationReason();

		for (String reason : reasons) {

			cancellationReason.setReason(reason);
			cancellationReason.setUser(user);
			cancellationReason.setDriver(driver);
		}
		String sendReasonDriver = sendReasonDriver(phoneNo, reasons);
		// Save the cancellation reason
		cancellationReasonRepository.save(cancellationReason);

		return sendReasonDriver;

	}

	@Override
	public List<CancellationReason> getAllCancellation() {

		return cancellationReasonRepository.findAll();
	}

//	    
//	    @Override
//	    public String cancelRideAndNotifyUser(Long userId, String reason ,Long driverId) {
//	    	
////	    	Find the Driver
//	          Driver driver = driverRepository.findById(driverId).orElseThrow();	
//              User user = userRepository.findById(userId).orElseThrow();
//         
//	          String phoneNo = user.getPhoneNo();
//		        // Create a new cancellation reason
//		     
//		        
//		        CancellationReason cancellationReason = new CancellationReason();
//		        cancellationReason.setReason(reason);
//		        cancellationReason.setUser(user);
//		        cancellationReason.setDriver(driver);
//
//		        driver.setStatus("Available");
//		        
//		        driverRepository.save(driver);
//		        String sendReasonDriver = sendReasonDriver(phoneNo,reason);
//		        // Save the cancellation reason
//		        cancellationReasonRepository.save(cancellationReason);
//				
//		        
//		        return sendReasonDriver;
//	       
//	    }

	public String sendReasonDriver(String contactNo, String reasonCancel) {
		if (contactNo == null || twilioConfig == null || twilioConfig.getAccountSid() == null
				|| twilioConfig.getAuthToken() == null || twilioConfig.getTrailNumber() == null) {
			// Handle null values or misconfiguration
			return "Not found User with this contact number " + contactNo;
		}

		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());

		// Send the cancellation reason via SMS using Twilio
		Message message = Message.creator(new com.twilio.type.PhoneNumber(contactNo),
				new com.twilio.type.PhoneNumber(twilioConfig.getTrailNumber()),
				"Reason for cancellation : " + reasonCancel).create();
		return reasonCancel + "successfully send";
	}

}
