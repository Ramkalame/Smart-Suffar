package com.rido.controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rido.dto.VerifyRequest;
import com.rido.entity.Admin;
import com.rido.entity.Courier;
import com.rido.entity.Driver;
import com.rido.entity.Hub;
import com.rido.entity.HubEmployee;
import com.rido.entity.ManageOtp;
import com.rido.entity.RegisterOtp;
import com.rido.entity.Role;
import com.rido.entity.User;
import com.rido.entity.UserIdentity;
import com.rido.entity.enums.ERole;
import com.rido.entity.enums.Status;
import com.rido.entity.enums.VehicleAssignStatus;
import com.rido.entityDTO.JwtResponse;
import com.rido.payload.request.LoginRequest;
import com.rido.payload.request.SignupRequest;
import com.rido.repository.AdminRepository;
import com.rido.repository.CourierRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubEmployeeRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.ManageOtpRepository;
import com.rido.repository.RegisterOtpRepository;
import com.rido.repository.RoleRepository;
import com.rido.repository.UserIdentityRepository;
import com.rido.repository.UserRepository;
import com.rido.security.jwt.JwtUtils;
import com.rido.security.jwt.TokenBlacklistService;
import com.rido.security.services.UserDetailsImpl;
import com.rido.service.HubService;
import com.rido.service.LocationService;
import com.rido.service.impl.LocationImpl;
import com.rido.service.impl.UserServiceImpl;
import com.rido.utils.ApiResponse;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private UserIdentityRepository userIdentityRepository;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private HubEmployeeRepository hubEmployeeRepository;

	@Autowired
	private LocationImpl locationImpl;

	@Autowired
	private LocationService locationService;

	@Autowired
	private ManageOtpRepository manageOtpRepository;

	@Autowired
	private RegisterOtpRepository registerOtpRepository;

	@Autowired
	private CourierRepository courierRepository;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private HubService hubService;

	@Autowired
	private TokenBlacklistService tokenBlacklistService;

	private static final Map<String, AtomicInteger> addressCounters = new ConcurrentHashMap<>();

	@PostMapping("/user/signup")
	public ResponseEntity<ApiResponse<JwtResponse>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

		try {

			if (userRepository.existsByUsername(signUpRequest.getUsername())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Username is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			if (userRepository.existsByEmail(signUpRequest.getEmail())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Email is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			if (userRepository.existsByPhoneNo(signUpRequest.getPhoneNumber())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: PhoneNo is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			User user = new User();
			UserIdentity userIdentity = new UserIdentity();

			userIdentity.setUsername(signUpRequest.getUsername());
			userIdentity.setEmail(signUpRequest.getEmail());
			userIdentity.setPhoneNo(signUpRequest.getPhoneNumber());
			userIdentity.setPassword(encoder.encode(signUpRequest.getPassword()));

			user.setName(signUpRequest.getName());
			user.setUsername(userIdentity.getUsername());
			user.setEmail(userIdentity.getEmail());
			user.setPhoneNo(userIdentity.getPhoneNo());
			user.setPassword(userIdentity.getPassword());

			Set<Role> roles = new HashSet<>();
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
//			user.setRoles(roles);
			userIdentity.setRoles(roles);

			userIdentityRepository.save(userIdentity);
			userRepository.save(user);

			ManageOtp manageOtp = new ManageOtp();
			manageOtp.setUser(user);

			manageOtpRepository.save(manageOtp);

			JwtResponse signupResponse = new JwtResponse();

			signupResponse.setId(user.getUserId());
			signupResponse.setEmail(user.getEmail());
			signupResponse.setPhoneNo(user.getPhoneNo());
			signupResponse.setUsername(user.getUsername());
			signupResponse.setName(user.getName());

			ApiResponse<JwtResponse> response = new ApiResponse<>(signupResponse, HttpStatus.OK, true,
					"User registered successfully!");

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
					"Account Exists: Proceed to login to access your account.");
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/driver/signup")
	public ResponseEntity<ApiResponse<JwtResponse>> registerDriver(@Valid @RequestBody SignupRequest signUpRequest) {

		try {
			if (driverRepository.existsByUsername(signUpRequest.getUsername())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Username is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			if (driverRepository.existsByEmail(signUpRequest.getEmail())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Email is already in use!");
				return ResponseEntity.badRequest().body(response);
			}

			if (driverRepository.existsByPhoneNo(signUpRequest.getPhoneNumber())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: PhoneNo is already in use!");
				return ResponseEntity.badRequest().body(response);
			}

			Hub hub = hubRepository.findByHubName(signUpRequest.getHubName())
					.orElseThrow(() -> new RuntimeException("Error: Hub location not found."));

			String driverUniqueId = generateDriverId(signUpRequest.getHubName());
			String encodedPassword = encoder.encode(signUpRequest.getPassword());

			UserIdentity userIdentity = new UserIdentity(signUpRequest.getUsername(), signUpRequest.getEmail(),
					signUpRequest.getPhoneNumber(), encodedPassword);

			Driver driver = new Driver(userIdentity.getUsername(), userIdentity.getEmail(), userIdentity.getPhoneNo(),
					encodedPassword);

			driver.setDriverType(signUpRequest.getDriverType());
			driver.setStatus(Status.AVAILABLE);
			driver.setVehicleAssignStatus(VehicleAssignStatus.CHECKOUT);
			driver.setHub(hub);

			Set<Role> roles = new HashSet<>();
			Role driverRole = roleRepository.findByName(ERole.ROLE_DRIVER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(driverRole);
			userIdentity.setRoles(roles);

			driver.setDriverUniqeId(driverUniqueId);

			driverRepository.save(driver);
			ManageOtp manageOtp = new ManageOtp();
			manageOtp.setDriver(driver);

			manageOtpRepository.save(manageOtp);
			userIdentityRepository.save(userIdentity);
			JwtResponse signupResponse = new JwtResponse();

			signupResponse.setId(driver.getDriverId());
			signupResponse.setEmail(driver.getEmail());
			signupResponse.setPhoneNo(driver.getPhoneNo());
			signupResponse.setUsername(driver.getUsername());
			signupResponse.setDriverType(driver.getDriverType());
			signupResponse.setUniqeId(driver.getDriverUniqeId());

			ApiResponse<JwtResponse> response = new ApiResponse<>(signupResponse, HttpStatus.OK, true,
					"Driver registered successfully!");

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
					"Account Exists: Proceed to login to access your account.");
			return ResponseEntity.badRequest().body(response);
		}
	}

	private String generateDriverId(String hubName) {
		Optional<Hub> hubOptional = hubRepository.findByHubName(hubName);
		String prefix = hubOptional.map((Hub hub) -> hub.getHubName().toUpperCase().substring(0, 3)).orElse("UNK");

		return prefix + generateRandomNumber();
	}

	private String generateRandomNumber() {
		Random random = new Random();
		return String.valueOf(random.nextInt(900) + 100); // Generates a 3-digit random number
	}

	@PostMapping("/hub/signup")
	public ResponseEntity<ApiResponse<JwtResponse>> registerHub(@Valid @RequestBody SignupRequest signUpRequest) {

		try {

			if (hubRepository.existsByEmail(signUpRequest.getEmail())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Email is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			if (hubRepository.existsByPhoneNo(signUpRequest.getPhoneNumber())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: PhoneNo is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			Admin admin = adminRepository.findByAdminUniqeId(signUpRequest.getAdminId())
					.orElseThrow(() -> new RuntimeException("Error: Admin not found with this Id"));

			Hub hub = new Hub();
			UserIdentity userIdentity = new UserIdentity();

			userIdentity.setEmail(signUpRequest.getEmail());
			userIdentity.setPhoneNo(signUpRequest.getPhoneNumber());
			userIdentity.setPassword(encoder.encode(signUpRequest.getPassword()));

			hub.setManagerName(signUpRequest.getManagerName());
			hub.setEmail(userIdentity.getEmail());
			hub.setPhoneNo(userIdentity.getPhoneNo());
			hub.setPassword(userIdentity.getPassword());
//			hub.setDesignation("Manager");
			hub.setHubName(signUpRequest.getHubName());
			hub.setCity(signUpRequest.getCity());
			hub.setState(signUpRequest.getState());
			hub.setAdmin(admin);

			hub.setHubUniqeId(generateUniqueHubId());

			Set<Role> roles = new HashSet<>();
			Role hubRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(hubRole);
//			hub.setRoles(roles);
			userIdentity.setRoles(roles);
			ManageOtp manageOtp = new ManageOtp();
			manageOtp.setHub(hub);
			hubRepository.save(hub);
			userIdentityRepository.save(userIdentity);
			manageOtpRepository.save(manageOtp);

			JwtResponse signupResponse = new JwtResponse();

			signupResponse.setId(hub.getHubId());
			signupResponse.setEmail(hub.getEmail());
			signupResponse.setPhoneNo(hub.getPhoneNo());
			signupResponse.setUsername(userIdentity.getUsername());
			signupResponse.setName(hub.getManagerName());

			ApiResponse<JwtResponse> response = new ApiResponse<>(signupResponse, HttpStatus.OK, true,
					"Hub registered successfully!");

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
					"Account Exists: Proceed to login to access your account.");
			return ResponseEntity.badRequest().body(response);
		}
	}

	public String generateUniqueHubId() {
		String prefix = "SSH";
		String maxId = hubRepository.findMaxHubUniqueId();
		int nextIdNumber = 1;

		if (maxId != null) {
			String numberPart = maxId.substring(prefix.length());
			nextIdNumber = Integer.parseInt(numberPart) + 1;
		}

		return String.format("%s%04d", prefix, nextIdNumber);
	}

	@PostMapping("/hub/employee/signup")
	public ResponseEntity<ApiResponse<JwtResponse>> registerEmployee(@Valid @RequestBody SignupRequest signUpRequest) {

		try {

			if (hubEmployeeRepository.existsByUsername(signUpRequest.getUsername())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Username is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			if (hubEmployeeRepository.existsByEmail(signUpRequest.getEmail())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Email is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			if (hubEmployeeRepository.existsByPhoneNo(signUpRequest.getPhoneNumber())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Username is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			HubEmployee hubEmp = new HubEmployee();
			UserIdentity userIdentity = new UserIdentity();
			Hub hub = hubRepository.findByHubName(signUpRequest.getHubName())
					.orElseThrow(() -> new RuntimeException("Error: Hub location not found."));

			userIdentity.setUsername(signUpRequest.getUsername());
			userIdentity.setEmail(signUpRequest.getEmail());
			userIdentity.setPhoneNo(signUpRequest.getPhoneNumber());
			userIdentity.setPassword(encoder.encode(signUpRequest.getPassword()));

			hubEmp.setUsername(userIdentity.getUsername());
			hubEmp.setEmail(userIdentity.getEmail());
			hubEmp.setPhoneNo(userIdentity.getPhoneNo());
			hubEmp.setPassword(userIdentity.getPassword());
			hubEmp.setHub(hub);

			Set<Role> roles = new HashSet<>();
			Role empRole = roleRepository.findByName(ERole.ROLE_SUBMODERATOR)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(empRole);
//			hubEmp.setRoles(roles);
			userIdentity.setRoles(roles);

			hubEmployeeRepository.save(hubEmp);
			userIdentityRepository.save(userIdentity);

			ManageOtp manageOtp = new ManageOtp();
			manageOtp.setHubEmployee(hubEmp);

			manageOtpRepository.save(manageOtp);
			JwtResponse signupResponse = new JwtResponse();

			signupResponse.setId(hubEmp.getHubEmployeeId());
			signupResponse.setEmail(hubEmp.getEmail());
			signupResponse.setPhoneNo(hubEmp.getPhoneNo());
			signupResponse.setUsername(hubEmp.getUsername());
			signupResponse.setName(hubEmp.getName());

			ApiResponse<JwtResponse> response = new ApiResponse<>(signupResponse, HttpStatus.OK, true,
					"Hub Employee registered successfully!");

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
					"Account Exists: Proceed to login to access your account.");
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/courierowner/signup")
	public ResponseEntity<ApiResponse<JwtResponse>> registerCourierOwner(
			@Valid @RequestBody SignupRequest signUpRequest) {

		try {
			if (courierRepository.existsByOwnerName(signUpRequest.getUsername())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Username is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			if (courierRepository.existsByEmail(signUpRequest.getEmail())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Email is already in use!");
				return ResponseEntity.badRequest().body(response);
			}

			if (courierRepository.existsByPhoneNo(signUpRequest.getPhoneNumber())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: PhoneNo is already in use!");
				return ResponseEntity.badRequest().body(response);
			}

			Courier courier = new Courier();
			UserIdentity userIdentity = new UserIdentity();
			Hub hub = hubRepository.findByHubName(signUpRequest.getHubName())
					.orElseThrow(() -> new RuntimeException("Error: Hub location not found."));

			userIdentity.setName(signUpRequest.getName());
			userIdentity.setEmail(signUpRequest.getEmail());
			userIdentity.setPhoneNo(signUpRequest.getPhoneNumber());
			userIdentity.setPassword(encoder.encode(signUpRequest.getPassword()));

			courier.setVehicleType(signUpRequest.getVehicleType());
			courier.setOwnerName(userIdentity.getName());
			courier.setEmail(userIdentity.getEmail());
			courier.setPhoneNo(userIdentity.getPhoneNo());
			courier.setPassword(userIdentity.getPassword());
			courier.setHub(hub);

			Set<Role> roles = new HashSet<>();
			Role empRole = roleRepository.findByName(ERole.ROLE_COURIER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(empRole);
			userIdentity.setRoles(roles);

			courierRepository.save(courier);
			userIdentityRepository.save(userIdentity);

			ManageOtp manageOtp = new ManageOtp();
			manageOtp.setCourier(courier);

			manageOtpRepository.save(manageOtp);

			JwtResponse signupResponse = new JwtResponse();

			signupResponse.setId(courier.getCourierId());
			signupResponse.setEmail(courier.getEmail());
			signupResponse.setPhoneNo(courier.getPhoneNo());
			signupResponse.setUsername(userIdentity.getUsername());
			signupResponse.setDriverType(courier.getVehicleType());
			signupResponse.setName(courier.getOwnerName());

			ApiResponse<JwtResponse> response = new ApiResponse<>(signupResponse, HttpStatus.OK, true,
					"Courier registered successfully!");

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
					"Account Exists: Proceed to login to access your account.");
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			// Perform authentication
			org.springframework.security.core.Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmailOrPhoneNo(),
							loginRequest.getPassword()));

			// Set authentication in security context
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Generate JWT token
			String jwt = jwtUtils.generateJwtToken(authentication);

			// Get user details from authenticated principal
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

			// Get user roles
			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());
			// Return JWT response
			return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
					userDetails.getEmail(), userDetails.getPhoneNo(), roles));
		} catch (BadCredentialsException ex) {
			// Handle authentication failure due to bad credentials
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Access Denied: Your login credentials are incorrect. Please try again");
		}
	}

	@PostMapping("/verify-phoneno")
	public ResponseEntity<String> verifyPhoneNoOtp(@RequestBody com.rido.dto.VerifyRequest request) {
		if (locationService.verifyRegisterPhoneNoOtp(request.getPhoneNo(), request.getSmsOtp())) {
			return ResponseEntity.ok("Phone number verified successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to verify phone number. Please check OTP and try again....");
		}
	}

	@PostMapping("/verify-email-otp")
	public ResponseEntity<String> verifyEmailOtp(@RequestBody VerifyRequest request) {
		if (locationService.verifyRegisterEmailOtp(request.getEmail(), request.getEmailOtp())) {
			return ResponseEntity.ok("Email verified successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to verify email. Please check OTP and try again....");
		}
	}

	@PostMapping("/signUpWithPhone")
	public ResponseEntity<String> signUpWithPhone(@RequestBody VerifyRequest request) {
		try {
			// Check if the user already exists by phone number
			UserIdentity existingUser = userIdentityRepository.findByPhoneNo(request.getPhoneNo()).orElse(null);

			if (existingUser == null) {
				// User does not exist, proceed with OTP generation and sending

				// Check if the phone number already exists in OTP register otp repository
				Optional<RegisterOtp> existingRegisterOtp = registerOtpRepository.findByPhoneNo(request.getPhoneNo());
				String otp = null;

				if (existingRegisterOtp.isPresent()) {
					// Phone number exists, check if the OTP is older than 1 minute
					RegisterOtp registerOtp = existingRegisterOtp.get();
					LocalDateTime currentTime = LocalDateTime.now();
					LocalDateTime otpTime = registerOtp.getTimestamp();
					System.out.println(currentTime);
					if (otpTime.plusMinutes(1).isBefore(currentTime)) {
						// OTP is older than 1 minute, generate a new OTP
						otp = locationService.generateRandomOtp();
						System.out.println(otp);
						registerOtp.setRegisterPhoneOtp(otp);
						registerOtp.setTimestamp(currentTime);
						registerOtpRepository.save(registerOtp);
					}
				} else {
					// Phone number doesn't exist, create new OTP entry and save
					otp = locationService.generateRandomOtp();
					RegisterOtp registerOtp = new RegisterOtp();
					registerOtp.setPhoneNo(request.getPhoneNo());
					registerOtp.setRegisterPhoneOtp(otp);
					System.out.println(LocalDateTime.now());
					registerOtp.setTimestamp(LocalDateTime.now());
					registerOtpRepository.save(registerOtp);
				}

				// Send OTP to phone number
				locationImpl.sendVerificationCode(request.getPhoneNo(), otp);

				return ResponseEntity.status(HttpStatus.OK).body("Successfully sent OTP");
			} else {
				// User with the same phone number already exists
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with this phone number already exists");
			}
		} catch (Exception e) {
			// Handle any unexpected errors
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP");
		}
	}

	@PostMapping("/emailSendOtp")
	public ResponseEntity<String> emailVerifysend(@RequestBody VerifyRequest request) {

		UserIdentity existingUser = userIdentityRepository.findByEmail(request.getEmail()).orElse(null);
		// check the user is there or not in useridentity
		if (existingUser == null) {
			Optional<RegisterOtp> registerotp = registerOtpRepository.findByEmail(request.getEmail());

			String emailOtp = null;

			if (registerotp.isPresent()) {
				RegisterOtp registerOtp = registerotp.get();
				LocalDateTime currentTime = LocalDateTime.now();
				LocalDateTime otpTime = registerOtp.getTimestamp();

				if (otpTime.plusMinutes(1).isBefore(currentTime)) {
					emailOtp = locationService.generateRandomOtp();
					RegisterOtp registerotp2 = registerotp.get();
					registerotp2.setRegisterEmailOtp(emailOtp);
					registerOtp.setTimestamp(currentTime);
					registerOtpRepository.save(registerotp2);

				}
			} else {
				emailOtp = locationService.generateRandomOtp();
				RegisterOtp registerOtp = new RegisterOtp();
				registerOtp.setRegisterEmailOtp(emailOtp);
				registerOtp.setEmail(request.getEmail());
				registerOtp.setTimestamp(LocalDateTime.now());
				registerOtpRepository.save(registerOtp);

				userServiceImpl.sendOtpByEmail(request.getEmail(), emailOtp);

				return ResponseEntity.status(HttpStatus.OK).body("suceesfully send otp");
			}

		}
		return ResponseEntity.status(HttpStatus.FOUND).body("User with this email already exist");
	}

	@PostMapping("/admin/signup")
	public ResponseEntity<ApiResponse<JwtResponse>> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {

		try {

			if (adminRepository.existsByUsername(signUpRequest.getUsername())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Username is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			if (adminRepository.existsByEmail(signUpRequest.getEmail())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: Email is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			if (adminRepository.existsByPhoneNo(signUpRequest.getPhoneNumber())) {
				ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
						"Error: PhoneNo is already taken!");
				return ResponseEntity.badRequest().body(response);
			}

			Admin admin = new Admin();
			UserIdentity userIdentity = new UserIdentity();

			userIdentity.setUsername(signUpRequest.getUsername());
			userIdentity.setEmail(signUpRequest.getEmail());
			userIdentity.setPhoneNo(signUpRequest.getPhoneNumber());
			userIdentity.setPassword(encoder.encode(signUpRequest.getPassword()));

			admin.setUsername(userIdentity.getUsername());
			admin.setEmail(userIdentity.getEmail());
			admin.setPhoneNo(userIdentity.getPhoneNo());
			admin.setPassword(userIdentity.getPassword());

			// Generate adminUniqeId
			admin.setAdminUniqeId(generateUniqueAdminId());

			Set<Role> roles = new HashSet<>();
			Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
//			user.setRoles(roles);
			userIdentity.setRoles(roles);

			userIdentityRepository.save(userIdentity);
			adminRepository.save(admin);

			ManageOtp manageOtp = new ManageOtp();
			manageOtp.setAdmin(admin);

			manageOtpRepository.save(manageOtp);

			JwtResponse signupResponse = new JwtResponse();

			signupResponse.setId(admin.getAdminId());
			signupResponse.setEmail(admin.getEmail());
			signupResponse.setPhoneNo(admin.getPhoneNo());
			signupResponse.setUsername(admin.getUsername());
			signupResponse.setName(admin.getName());
			signupResponse.setUniqeId(admin.getAdminUniqeId());

			ApiResponse<JwtResponse> response = new ApiResponse<>(signupResponse, HttpStatus.OK, true,
					"Admin registered successfully!");

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<JwtResponse> response = new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false,
					"Account Exists: Proceed to login to access your account.");
			return ResponseEntity.badRequest().body(response);
		}
	}

	public String generateUniqueAdminId() {
		String prefix = "SS";
		String maxId = adminRepository.findMaxAdminUniqueId();
		int nextIdNumber = 1;

		if (maxId != null) {
			String numberPart = maxId.substring(prefix.length());
			nextIdNumber = Integer.parseInt(numberPart) + 1;
		}

		return String.format("%s%04d", prefix, nextIdNumber);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String tokenHeader) {
		if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
			return ResponseEntity.badRequest().body(
					new ApiResponse<>(null, HttpStatus.BAD_REQUEST, false, "Invalid or missing Authorization header"));
		}

		String token = tokenHeader.replace("Bearer ", "");
		try {
			tokenBlacklistService.blacklistToken(token);
			return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK, true, "User logged out successfully!"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(null,
					HttpStatus.INTERNAL_SERVER_ERROR, false, "An error occurred during logout"));
		}
	}

}