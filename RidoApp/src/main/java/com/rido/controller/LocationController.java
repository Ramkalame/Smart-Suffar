package com.rido.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rido.dto.UserPhoneResponseDto;
import com.rido.entity.User;
import com.rido.entity.UserLocation;
import com.rido.entityDTO.ResponseLogin;
import com.rido.repository.UserLocationRepository;
import com.rido.repository.UserRepository;
import com.rido.service.LocationService;

@CrossOrigin(origins = { "http://10.0.2.2:8080", "http://localhost:3000" })
@RestController
@RequestMapping("/locationapi")
public class LocationController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserLocationRepository locationRepository;

	@Autowired
	private LocationService locationService;

	public static class VerifyOtpRequest {

		private Long userId;
		private String otp;

		public String getOtp() {
			return otp;
		}

		public void setOtp(String otp) {
			this.otp = otp;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public Long getUserId() {
			// TODO Auto-generated method stub
			return null;
		}

		// Getters and setters

	}

	@GetMapping("/map")
	public String showMap() {
		return "map"; // This should be the name of your HTML/Thymeleaf template
	}

	@PostMapping(value = "/savelocation/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveLocation(@PathVariable Long userId, @RequestBody UserLocation location)
			throws NotFoundException {
		locationService.saveLocation(userId, location);
		return ResponseEntity.ok("Location saved successfully");
	}

	@GetMapping("/locationId/{id}")
	public UserLocation getLocationById(@PathVariable("id") Long id) {
		return locationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
	}

	@GetMapping("/calculateDistance")
	public String calculateDistance(@RequestParam String origin, @RequestParam String destination) {
		return locationService.getDistanceAndTime(origin, destination);
	}

	@PostMapping("/sign-with-phonenumber")
	public ResponseEntity<ResponseLogin> signUpWithPhone(@RequestBody User user) {
		UserPhoneResponseDto savedUser = locationService.signwithPhone(user);

		Long userId = user.getUserId(); // Assuming getUserId() returns the user ID
		ResponseLogin response = new ResponseLogin(userId, "Login Successfully ");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/verifySmsOtp")
	public ResponseEntity<String> verifySmsOtp(@RequestBody VerifyOtpRequest request) {
		boolean verifySmsOtp = locationService.verifySmsOtp(request.getUserId(), request.getOtp());

		if (verifySmsOtp) {
			return new ResponseEntity<>("OTP verified successfully", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
		}
	}

//    @PostMapping("/saveLocation")
//    public ResponseEntity<String> saveLocation(@RequestParam Long userId, @RequestBody LocationRequest locationRequest) throws NotFoundException {
//        locationService.saveLocation(userId, locationRequest);
//		return ResponseEntity.ok("Location saved successfully");
//    }
}
