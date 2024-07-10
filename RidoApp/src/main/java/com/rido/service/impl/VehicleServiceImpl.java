package com.rido.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.rido.dto.CourierEbikeDto;
import com.rido.dto.VehicleDataDto;
import com.rido.entity.Admin;
import com.rido.entity.CourierEbike;
import com.rido.entity.Driver;
import com.rido.entity.Hub;
import com.rido.entity.Vehicle;
import com.rido.entity.enums.DriverAndVehicleType;
import com.rido.entity.enums.RentalPackageType;
import com.rido.entity.enums.Status;
import com.rido.entity.enums.VehicleStatus;
import com.rido.repository.AdminRepository;
import com.rido.repository.CourierEbikeRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.VehicleRepository;
import com.rido.service.VehicleService;

@Service
public class VehicleServiceImpl implements VehicleService {

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	DriverServiceImpl driverServiceImpl;

	@Value("${project.image}")
	private String path;

	@Value("${bucketName}") // Assuming you have bucket name configured in properties file
	private String bucketName;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private CourierEbikeRepository courierEbikeRepo;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private DriverRepository driverRepository;

	@Override
	public Vehicle findById(Long id) {
		return vehicleRepository.findById(id).orElse(null);
	}

//	@Override
//	public String addNewCarAdmin(VehicleDataDto carData, MultipartFile file) throws IOException {
//		if(file==null || file.isEmpty()) {
//			throw new IllegalArgumentException("File is required");
//		}
//		
//		 // Save file to the upload directory
//        String fileName = saveFile(file);
//
//        // Create Car entity and populate with data
//        Vehicle car = new Vehicle();
//        car.setVehicleName(carData.getVehicleName());
//        car.setPrice(carData.getPrice());
//        car.setBattery(carData.getBattery());
//        car.setChargingTime(carData.getChargingTime());
//        car.setSeatingCapacity(carData.getSeatingCapacity());
//        car.setTransmissionTypo(carData.getTransmissionTypo());
//        car.setVehicleType(carData.getVehicleType());
//        car.setVehicleNo(carData.getVehicleNo());
//        car.setInsuranceNo(carData.getInsuranceNo());
//        car.setVehicleImgLink(ServletUriComponentsBuilder.fromCurrentContextPath().path("/RidoApp/images").path(file.getOriginalFilename()).toUriString());
//        car.setVehicleImg(file.getBytes());
//        car.setVehicleImageName(fileName);
//        vehicleRepository.save(car);
//        
//        return "Vehicle data and file saved successfully";
//      
//	}

	private String saveFile(MultipartFile file) throws IOException {
		if (file != null && !file.isEmpty()) {
			// Save the image to the specified folder
			String name = file.getOriginalFilename();
			String uniqueIdentifier = UUID.randomUUID().toString();
			String fileName = uniqueIdentifier + "_" + name;
			String filePath = path + File.separator + fileName;
			File destinationFile = new File(filePath);
			FileOutputStream fos = new FileOutputStream(destinationFile);
			fos.write(file.getBytes());
			fos.close();
			return fileName;

		}
		return null;
	}

	
	private static final int MAX_FILE_NAME_LENGTH = 1000; // Maximum length allowed for S3 object key

	@Override
	public String uploadFile(MultipartFile file) throws IOException {
	    // Generate a unique file name
	    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
	    if (fileName.length() > MAX_FILE_NAME_LENGTH) {
	        fileName = fileName.substring(0, MAX_FILE_NAME_LENGTH);
	    }

	    File convertedFile = convertMultiPartToFile(file);

	    // Upload the file to S3
	    amazonS3.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));

	    // Return the S3 URL of the uploaded file
	    return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convertedFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		}
		return convertedFile;
	}

	@Override
	public List<VehicleDataDto> getStandardRentalVehicles() {
		return driverRepository.findAll().stream().filter(driver -> driver.getStatus() == Status.AVAILABLE)
				.map(driver -> convertToVehicleDataDtoStandard(driver, driver.getVehicle())).filter(dto -> dto != null)
				.collect(Collectors.toList());
	}

	@Override
	public List<VehicleDataDto> getPremiumRentalVehicles() {
		return driverRepository.findAll().stream().filter(driver -> driver.getStatus() == Status.AVAILABLE)
				.map(driver -> convertToVehicleDataDtoPremium(driver, driver.getVehicle())).filter(dto -> dto != null)
				.collect(Collectors.toList());
	}

	private VehicleDataDto convertToVehicleDataDtoStandard(Driver driver, Vehicle vehicle) {
		if (vehicle != null && vehicle.getVehicleServiceType() == RentalPackageType.STANDARD
				&& vehicle.getVehicleType() == DriverAndVehicleType.FOUR_WHEELER) {
			return new VehicleDataDto(vehicle.getVehicleId(),
					vehicle.getAdmin() != null ? vehicle.getAdmin().getAdminId() : null,
					vehicle.getHub() != null ? vehicle.getHub().getHubId() : null, driver.getDriverId(),
					vehicle.getVehicleName(), vehicle.getHub() != null ? vehicle.getHub().getHubName() : null,
					vehicle.getHub() != null ? vehicle.getHub().getManagerName() : null, vehicle.getPrice(),
					 vehicle.getSeatingCapacity(),
					vehicle.getVehicleNo(), vehicle.getInsuranceNo(),
					vehicle.getPricePerKm(), vehicle.getVehicleImgLink(), vehicle.getDistance(),
					vehicle.getVehicleServiceType(), vehicle.getVehicleType(), vehicle.getVehicleStatus(),vehicle.getChassisNo(),vehicle.getVehiclerange(),vehicle.getDateOfPurchase(),vehicle.getInvoice());
			
			
			         
		}
		return null;
	}

	private VehicleDataDto convertToVehicleDataDtoPremium(Driver driver, Vehicle vehicle) {
		if (vehicle != null && vehicle.getVehicleServiceType() == RentalPackageType.PREMIUM
				&& vehicle.getVehicleType() == DriverAndVehicleType.FOUR_WHEELER) {
			return new VehicleDataDto(vehicle.getVehicleId(),
					vehicle.getAdmin() != null ? vehicle.getAdmin().getAdminId() : null,
					vehicle.getHub() != null ? vehicle.getHub().getHubId() : null, driver.getDriverId(),
					vehicle.getVehicleName(), vehicle.getHub() != null ? vehicle.getHub().getHubName() : null,
					vehicle.getHub() != null ? vehicle.getHub().getManagerName() : null, vehicle.getPrice(),
					 vehicle.getSeatingCapacity(),
					 vehicle.getVehicleNo(), vehicle.getInsuranceNo(),
					vehicle.getPricePerKm(), vehicle.getVehicleImgLink(), vehicle.getDistance(),
					vehicle.getVehicleServiceType(), vehicle.getVehicleType(), vehicle.getVehicleStatus(),vehicle.getChassisNo(),vehicle.getVehiclerange(),vehicle.getDateOfPurchase(),vehicle.getInvoice());
		}
		return null;
	}

	@Override
	public Vehicle getVehicleById(Long vehicleId) {
		return vehicleRepository.findById(vehicleId).orElse(null);
	}

	@Override
	public String addVehicle(VehicleDataDto DataDto, String s3Url, Long hubId) {
		try {
			Vehicle vehicle = new Vehicle();
			vehicle.setVehicleName(DataDto.getVehicleName());
			vehicle.setPrice(DataDto.getPrice());
			//vehicle.setBattery(DataDto.getBattery());
			//vehicle.setChargingTime(DataDto.getChargingTime());
			vehicle.setSeatingCapacity(DataDto.getSeatingCapacity());
			//vehicle.setTransmissionTypo(DataDto.getTransmissionTypo());
			// vehicle.setVehicleType(ebikeDataDto.getVehicleType());
			vehicle.setVehicleNo(DataDto.getVehicleNo());
			vehicle.setInsuranceNo(DataDto.getInsuranceNo());
			//vehicle.setVehicleImgLinks(s3Url);
			vehicle.setVehicleStatus(VehicleStatus.AVAILABLE);
			vehicle.setPricePerKm(DataDto.getPricePerKm());
			vehicle.setVehicleServiceType(DataDto.getVehicleServiceType());

			vehicle.setVehicleType(DataDto.getVehicleType());

			// Set the hub ID
			Optional<Hub> optionalHub = hubRepository.findById(hubId);
			if (optionalHub.isPresent()) {
				Hub hub = optionalHub.get();
				vehicle.setHub(hub);
			} else {
				throw new IllegalArgumentException("Hub not found with id: " + hubId);
			}

			// Save the new eBike
			vehicleRepository.save(vehicle);

			return "New vehicle added successfully!";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error adding eBike";
		}
	}

	@Override
	public String addebikeVehicle(CourierEbikeDto ebikeDataDto, String s3Url, Long adminId) {
		try {
			Optional<Admin> adminOptional = adminRepository.findById(adminId);
			if (!adminOptional.isPresent()) {
				return "Admin with ID " + adminId + " not found.";
			}
			Admin admin = adminOptional.get();

			CourierEbike courierEbike = new CourierEbike();
//			courierEbike.setBattery(ebikeDataDto.getBattery());
//			courierEbike.setChargingTime(ebikeDataDto.getChargingTime());
			courierEbike.setInsuranceNo(ebikeDataDto.getInsuranceNo());
			courierEbike.setVehicleName(ebikeDataDto.getVehicleName());
			courierEbike.setVehicleNo(ebikeDataDto.getVehicleNo());
			courierEbike.setWeight(ebikeDataDto.getWeight());
			courierEbike.setTopSpeed(ebikeDataDto.getTopSpeed());
			courierEbike.setPricePerKm(ebikeDataDto.getPricePerKm());
			courierEbike.setEbikeImage(s3Url);
			courierEbike.setRc(ebikeDataDto.getRc());
			courierEbike.setVehicleStatus(VehicleStatus.AVAILABLE);
			courierEbike.setAdmin(admin);

			courierEbikeRepo.save(courierEbike);

			return "Courier eBike added successfully.";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error adding Courier eBike.";
		}
	}

	@Override
	public List<CourierEbike> getAllEBikes() {
		return courierEbikeRepo.findAll();
	}

	@Override
	public CourierEbike getEBikeById(Long courierEbikeId) {
		Optional<CourierEbike> ebikeOptional = courierEbikeRepo.findById(courierEbikeId);
		return ebikeOptional.orElse(null);
	}

	@Override
	public VehicleDataDto getVehicleDetails(Long vehicleId) {
		Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);

		if (optionalVehicle.isPresent()) {
			Vehicle vehicle = optionalVehicle.get();
			VehicleDataDto vehicleDataDto = new VehicleDataDto();
			vehicleDataDto.setVehicleId(vehicle.getVehicleId());
			vehicleDataDto.setAdminId(vehicle.getAdmin().getAdminId());
			vehicleDataDto.setVehicleName(vehicle.getVehicleName());
			vehicleDataDto.setPrice(vehicle.getPrice());
			//vehicleDataDto.setBattery(vehicle.getBattery());
			//vehicleDataDto.setChargingTime(vehicle.getChargingTime());
			vehicleDataDto.setSeatingCapacity(vehicle.getSeatingCapacity());
			vehicleDataDto.setVehicleImgLink(vehicle.getVehicleImgLink());
			//vehicleDataDto.setTransmissionTypo(vehicle.getTransmissionTypo());
			vehicleDataDto.setVehicleNo(vehicle.getVehicleNo());
			vehicleDataDto.setInsuranceNo(vehicle.getInsuranceNo());
			vehicleDataDto.setPricePerKm(vehicle.getPricePerKm());
			vehicleDataDto.setVehicleServiceType(vehicle.getVehicleServiceType());
			vehicleDataDto.setVehicleType(vehicle.getVehicleType());
			return vehicleDataDto;
		} else {
			throw new RuntimeException("Vehicle not found with id: " + vehicleId);
		}
	}

	@Override
	public List<VehicleDataDto> getVehiclesByAdminId(Long adminId) {
		List<Vehicle> vehicles = vehicleRepository.findByAdmin_AdminId(adminId);

		if (vehicles.isEmpty()) {
			throw new DataAccessException("No vehicle found in the database.") {
			};
		}

		return vehicles.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	private VehicleDataDto convertToDto(Vehicle vehicle) {
		VehicleDataDto dto = new VehicleDataDto();
		dto.setHubname(vehicle.getHub().getManagerName());
		dto.setVehicleId(vehicle.getVehicleId());
		dto.setAdminId(vehicle.getAdmin().getAdminId());
		dto.setVehicleName(vehicle.getVehicleName());
		dto.setPrice(vehicle.getPrice());
		//dto.setBattery(vehicle.getBattery());
		//dto.setChargingTime(vehicle.getChargingTime());
		dto.setSeatingCapacity(vehicle.getSeatingCapacity());
		//dto.setTransmissionTypo(vehicle.getTransmissionTypo());
		dto.setVehicleNo(vehicle.getVehicleNo());
		dto.setInsuranceNo(vehicle.getInsuranceNo());
		dto.setPricePerKm(vehicle.getPricePerKm());
		dto.setVehicleServiceType(vehicle.getVehicleServiceType());
		dto.setVehicleType(vehicle.getVehicleType());
		return dto;
	}

	@Override
	public List<CourierEbike> getEbikeListByAdminId(Long adminId) {
		return courierEbikeRepo.findByAdminIdAndHubIsNotNull(adminId);
	}

	@Override
	public List<CourierEbike> getnonAssignEbikeListByAdminId(Long adminId) {
		return courierEbikeRepo.findByAdminIdAndHubIstNull(adminId);
	}

	}

	
	


