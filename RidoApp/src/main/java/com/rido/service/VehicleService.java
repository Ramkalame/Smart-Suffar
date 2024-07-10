
package com.rido.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rido.dto.CourierEbikeDto;
import com.rido.dto.VehicleDataDto;
import com.rido.entity.CourierEbike;
import com.rido.entity.Vehicle;

@Service
public interface VehicleService {

	public Vehicle findById(Long id);

	// public String addNewCarAdmin(VehicleDataDto carData, MultipartFile file)
	// throws IOException;

	public String uploadFile(MultipartFile file) throws IOException;

	public String addVehicle(VehicleDataDto DataDto, String s3Url, Long hubId);

	// public String addNewEbike(EBikeDto ebikeDataDto, String s3Url,Long hubId);

	public Vehicle getVehicleById(Long vehicleId);

	// public void deleteEbikeByHubIdAndEbikeId(Long hubId, Long ebikeId);

	// public EBike getEbikeByHubIdAndEbikeId(Long hubId, Long ebikeId);

	public String addebikeVehicle(CourierEbikeDto ebikeDataDto, String s3Url, Long adminId);

	public List<CourierEbike> getAllEBikes();

	public CourierEbike getEBikeById(Long courierEbikeId);

	public VehicleDataDto getVehicleDetails(Long vehicleId);

	public List<VehicleDataDto> getVehiclesByAdminId(Long adminId);

	public List<CourierEbike> getEbikeListByAdminId(Long adminId);

	public List<CourierEbike> getnonAssignEbikeListByAdminId(Long adminId);

	List<VehicleDataDto> getStandardRentalVehicles();

	List<VehicleDataDto> getPremiumRentalVehicles();

	
	

}
