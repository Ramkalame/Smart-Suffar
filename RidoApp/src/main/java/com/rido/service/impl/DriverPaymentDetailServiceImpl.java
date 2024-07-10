package com.rido.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rido.Exceptions.BusinessException;
import com.rido.dto.DriverPaymentDetailDto;
import com.rido.entity.AssignCar;
import com.rido.entity.Driver;
import com.rido.entity.DriverPaymentDetail;
import com.rido.entity.DriverPaymentDetail.Status;
import com.rido.entity.ReturnCar;
import com.rido.repository.AssignCarRepository;
import com.rido.repository.CarRepairRepository;
import com.rido.repository.CourierEbikedriverPaymentRepository;
import com.rido.repository.DriverPaymentDetailRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.ReturnCarRepository;
import com.rido.service.BookingService;
import com.rido.service.DriverPaymentDetailService;

@Service
public class DriverPaymentDetailServiceImpl implements DriverPaymentDetailService {

	@Autowired
	private DriverPaymentDetailRepository driverPaymentDetailRepo;

	@Autowired
	private DriverRepository driverRepository;
	
	@Autowired
	private ReturnCarRepository returnCarRepository;
	
	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private AssignCarRepository assignCarRepository;
	
	@Autowired
	private CourierEbikedriverPaymentRepository courierEbikedriverpaymentRepo;

//	private double calculateTotalAmount(double totalCompletedRides, double perRideAmount, double thresholdRides,
//			double IncentiveOnRide) {
//		double totalAmount = totalCompletedRides * perRideAmount;
//		System.out.println("totalAmount" + totalAmount);
//
//		// Check if the total completed rides exceed the threshold
//		if (totalCompletedRides > thresholdRides) {
//			double extraRides = totalCompletedRides - thresholdRides;
//			System.out.println("extra ride" + extraRides);
//			System.out.println("incentive on ride" + IncentiveOnRide);
//			double additionalIncentive = extraRides * IncentiveOnRide;
//
//			System.out.println(additionalIncentive);
//			totalAmount = totalAmount + additionalIncentive;
//		}
//		return totalAmount;
//	}

//	private String calculateTotalAmount(double totalCompletedRides, double perRideAmount, double thresholdRides,
//			double IncentiveOnRide,Status status) {
//		double totalAmount = totalCompletedRides * perRideAmount;
//
//// Check if the total completed rides exceed the threshold
//		if (totalCompletedRides > thresholdRides) {
//			double extraRides = totalCompletedRides - thresholdRides;
//			double additionalIncentive = extraRides * IncentiveOnRide;
//			totalAmount += additionalIncentive;
//		}
//
//// Convert totalAmount to String before returning
//		return String.valueOf(totalAmount);
//	}
	
	private String calculateTotalAmounts(double totalCompletedRides) 
			 {
		double totalAmount = totalCompletedRides * 100;

// Check if the total completed rides exceed the threshold
		if (totalCompletedRides > 3) {
			double extraRides = totalCompletedRides - 3;
			double additionalIncentive = extraRides * 2;
			totalAmount += additionalIncentive;
		}

// Convert totalAmount to String before returning
		return String.valueOf(totalAmount);
	}
	
	

	@Override
	public List<DriverPaymentDetailDto> getAllDriverPaymentDetails() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public List<DriverPaymentDetailDto> getDriverPaymentDetailsByHubId(Long hubId) {
//		List<DriverPaymentDetail> driverPaymentDetails = driverPaymentDetailRepo.findByHub_HubId(hubId);
//		System.out.println("driverPaymentDetails=" + driverPaymentDetails);
//		return driverPaymentDetails.stream().map(this::convertToDto).collect(Collectors.toList());
//	}
//
//	// Helper method to convert entity to DTO
//	private DriverPaymentDetailDto convertToDto(DriverPaymentDetail driverPaymentDetail) {
//		DriverPaymentDetailDto dto = new DriverPaymentDetailDto();
//		dto.setDriverName(driverPaymentDetail.getDriverName());
//		dto.setAddress(driverPaymentDetail.getAddress());
//		dto.setDate(driverPaymentDetail.getDate());
//		dto.setDriverid(driverPaymentDetail.getDriver().getDriverId());
//		dto.setTotalAmount(driverPaymentDetail.getAmount());
//
//		return dto;
//	}

//	@Override
//	public Double getTotalAmountByDriverId(Long driverId) {
//		Double totalAmount = driverPaymentDetailRepo.getTotalAmountByDriverId(driverId);
//		if (totalAmount != null) {
//			return totalAmount;
//		} else {
//			return 0.0;
//		}
//	}
	@Override
	public String getTotalAmountByDriverId(Long driverId) {
		String totalAmount = driverPaymentDetailRepo.getTotalAmountByDriverId(driverId);
		if (totalAmount != null) {
			return String.valueOf(totalAmount);
		} else {
			return "0";
		}
	}

	@Override
	public DriverPaymentDetailDto getDriverProfile(Long driverId) {
		Driver driver = driverRepository.findById(driverId).orElse(null);
		if (driver == null) {
			return null;
		}
//        // Create and populate DriverProfileDto
		DriverPaymentDetailDto driverProfileDto = new DriverPaymentDetailDto();
		driverProfileDto.setDriverid(driverId);
		driverProfileDto.setDriverName(driver.getName());
		driverProfileDto.setAddress(driver.getAddress());
		driverProfileDto.setPhoneNo(driver.getPhoneNo());
		driverProfileDto.setEmailAddress(driver.getEmail());
		driverProfileDto.setProfileImgLink(driver.getProfileImgLink());
		//driverProfileDto.setPerRideAmount(0);
		// driverProfileDto.setPerRideAmount(0);

		return driverProfileDto;
	}

	@Override
	public String createPaymentForHub(Long hubId, Long driverId, double totalCompletedRides, LocalDate date
			) {
		try {
			// Retrieve the driver information from the database using the driverId
			Driver driver = driverRepository.findById(driverId).orElse(null);
			if (driver == null) {
				return "Driver not found";
			}
			System.out.println("hii");

			if (!driver.getHub().getHubId().equals(hubId)) {
				return "Driver does not belong to the specified hub";
			}

			String totalAmount=calculateTotalAmounts( totalCompletedRides);
			DriverPaymentDetail newPaymentDetail = new DriverPaymentDetail();
			//newPaymentDetail.setDriverName(driver.getName());
			//newPaymentDetail.setAddress(driver.getAddress());
			newPaymentDetail.setDriver(driver);
			newPaymentDetail.setDate(date); // Set the date to the current date or use the provided date

//			newPaymentDetail.setIncentiveOnRide(paymentDetail.getIncentiveOnRide());
//			newPaymentDetail.setPerRideAmount(paymentDetail.getPerRideAmount());
//			newPaymentDetail.setThresholdRides(paymentDetail.getThresholdRides());
			newPaymentDetail.setHub(driver.getHub());
			newPaymentDetail.setStatus(Status.PENDING);
			
			newPaymentDetail.setAmount(totalAmount);

			driverPaymentDetailRepo.save(newPaymentDetail);

			return null; // Return null if the operation is successful
		} catch (Exception e) {
			e.printStackTrace(); // Log the exception
			return "Error creating payment"; // Return an error message if an exception occurs
		}
	}

	

	@Override
	public List<DriverPaymentDetailDto> getDriverPaymentDetailsByHubId(Long hubId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DriverPaymentDetailDto> getAllDriverPaymentDetailsByHubId(Long hubId) {
		List<DriverPaymentDetail> allDriverPaymentDetailsForHub = driverPaymentDetailRepo.findByHub_HubId(hubId);
		System.out.println("allDriverPaymentDetailsForHub="+allDriverPaymentDetailsForHub);
		
		if (allDriverPaymentDetailsForHub == null || allDriverPaymentDetailsForHub.isEmpty()) {
            throw new BusinessException("601", "No driver payment details found for hub ID: " + hubId);
        }
        return allDriverPaymentDetailsForHub.stream()
                .filter(detail -> detail.getStatus() == Status.PENDING)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper method to convert entity to DTO
    private DriverPaymentDetailDto convertToDto(DriverPaymentDetail driverPaymentDetail) {
        DriverPaymentDetailDto dto = new DriverPaymentDetailDto();
       // dto.setDriverName(driverPaymentDetail.getDriverName());
        //dto.setAddress(driverPaymentDetail.getAddress());
        dto.setDate(driverPaymentDetail.getDate());
        dto.setDriverid(driverPaymentDetail.getDriver().getDriverId());
        dto.setStatus(driverPaymentDetail.getStatus());
        dto.setTotalAmount(driverPaymentDetail.getAmount());

        return dto;
    }



	@Override
	public DriverPaymentDetail getDriverPaymentDetailById(Long driverPaymentDetailId) {
		Optional<DriverPaymentDetail> detail = driverPaymentDetailRepo.findById(driverPaymentDetailId);
		return detail.orElseThrow(() -> new BusinessException("NOT_FOUND", "Driver payment detail not found with ID: " + driverPaymentDetailId));
	}



	@Override
	public Map<String, Object> calculateDriverPayment(Long driverId, LocalDate date) {
		LocalDateTime startDate = LocalDateTime.of(date.minusDays(5), LocalTime.MIDNIGHT);
        LocalDateTime endDate = LocalDateTime.of(date, LocalTime.MAX);

        int totalCompletedRides = bookingService.getTotalCompletedRidesForDriverInWeek(driverId, startDate, endDate);

        List<AssignCar> assignCars = assignCarRepository.findByDriverIdAndOpeningTimeBetween(driverId, startDate, endDate);
        List<ReturnCar> returnCars = returnCarRepository.findByDriverIdAndReturnTimeBetween(driverId, startDate, endDate);

        Map<LocalDate, Integer> workingHoursMap = calculateWorkingHours(assignCars, returnCars);
        int totalPayment = calculateTotalPayment(workingHoursMap, totalCompletedRides);
        saveTotalPayment(driverId, totalPayment);

        Map<String, Object> response = new HashMap<>();
        response.put("dailyPayments", workingHoursMap);
        response.put("totalPayment", totalPayment);

        return response;
    }

    private Map<LocalDate, Integer> calculateWorkingHours(List<AssignCar> assignCars, List<ReturnCar> returnCars) {
        Map<LocalDate, Integer> workingHoursMap = new HashMap<>();

        for (AssignCar assignCar : assignCars) {
            LocalDate day = assignCar.getOpeningTime().toLocalDate();

            ReturnCar returnCar = returnCars.stream()
                    .filter(rc -> rc.getReturnTime().toLocalDate().equals(day))
                    .findFirst()
                    .orElse(null);

            if (returnCar != null) {
                LocalDateTime openingTime = assignCar.getOpeningTime();
                LocalDateTime returnTime = returnCar.getReturnTime();
                int workingHours = calculateHoursBetween(openingTime, returnTime);
                workingHoursMap.put(day, workingHours);
            }
        }

        return workingHoursMap;
    }

    private int calculateHoursBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return (int) Duration.between(startTime, endTime).toHours();
    }

    private int calculatePayment(int workingHours) {
        return workingHours >= 9 ? 200 : 100;
    }

    private int calculateTotalPayment(Map<LocalDate, Integer> workingHoursMap, int totalCompletedRides) {
        int totalPayment = 0;

        for (int workingHours : workingHoursMap.values()) {
            totalPayment += calculatePayment(workingHours);
        }

        if (totalCompletedRides > 1) {
            int incentive = (totalCompletedRides - 1) * 50;
            totalPayment += incentive;
        }

        return totalPayment;
    }

    private void saveTotalPayment(Long driverId, int totalPayment) {
        DriverPaymentDetail driverPayment = new DriverPaymentDetail();
        driverPayment.setAmount(String.valueOf(totalPayment));
        driverPaymentDetailRepo.save(driverPayment);
    }



	@Override
	public List<DriverPaymentDetail> getPaymentsByHubAndDate(Long hubId, LocalDate date) {
		return driverPaymentDetailRepo.findByHubIdAndDate(hubId, date);
	}



	@Override
	public List<DriverPaymentDetail> getPendingPayments() {
		return driverPaymentDetailRepo.findByStatus(Status.PENDING);
	}



	@Override
	public Optional<DriverPaymentDetail> getPendingDriverPaymentDetailById(Long driverPaymentDetailId) {
		return driverPaymentDetailRepo.findByDriverPaymentDetailIdAndStatus(driverPaymentDetailId, Status.PENDING);
	}



	@Override
	public List<DriverPaymentDetail> getcourierebikePaymentsByHubAndDate(Long hubId, LocalDate date) {
		return courierEbikedriverpaymentRepo.findByHubIdAndDate(hubId, date);
	}



	
	}
	
	 
	
	


