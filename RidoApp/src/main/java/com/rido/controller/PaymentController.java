
package com.rido.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.shaded.gson.Gson;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.rido.Exceptions.UserNotFoundException;
import com.rido.config.RazorPayConfiguration;
import com.rido.dto.CourierBookingDto;
import com.rido.dto.OrderResponse;
import com.rido.dto.PaymentResponse;
import com.rido.entity.Driver;
import com.rido.entity.DriverPayment;
import com.rido.entity.Hub;
import com.rido.entity.HubEmployee;
import com.rido.entity.HubEmployeePayment;
import com.rido.entity.HubPayment;
import com.rido.entity.PaymentActivity;
import com.rido.entity.User;
import com.rido.repository.DriverPaymentDetailRepository;
import com.rido.repository.DriverPaymentRepository;
import com.rido.repository.DriverRepository;
import com.rido.repository.HubEmployeePaymentRepository;
import com.rido.repository.HubEmployeeRepository;
import com.rido.repository.HubPaymentRepository;
import com.rido.repository.HubRepository;
import com.rido.repository.PaymentRepository;
import com.rido.repository.RentalBookingRepository;
import com.rido.repository.UserRepository;
import com.rido.service.CourierService;
import com.rido.service.HubPaymentService;
import com.rido.service.UserService;

@CrossOrigin(origins = { "http://10.0.2.2:8080", "http://localhost:3000" })
@RestController
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private UserService userService;

	@Autowired
	private CourierService courierService;

	@Autowired
	private RentalBookingRepository rentalRepository;

	private static Gson gson = new Gson();

	private RazorpayClient client;

//	private static final String SECRET_ID = "rzp_test_EkAN9YBYF0HgSD";
//	private static final String SECRET_KEY = "1HCXIT2XLU1bQcHEKdiOiWyZ";

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private HubPaymentRepository hubPaymentRepository;

	@Autowired
	DriverRepository driverRepository;

	@Autowired
	private HubEmployeePaymentRepository hubEmployeePaymentRepository;

	@Autowired
	private HubEmployeeRepository hubEmployeeRepository;

	@Autowired
	private DriverPaymentRepository driverPaymentRepository;

	@Autowired
	private HubPaymentService hubPaymentService;

	@Autowired
	private HubRepository hubRepository;

	@Autowired
	private DriverPaymentDetailRepository driverPaymentDetailRepository;

	@Autowired
	private RazorPayConfiguration razorPayConfiguration;

//	User payment to admin
//	RAM
	@PostMapping("/order_intialiseduser/{userId}")
	@ResponseBody
	public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> data, @PathVariable Long userId)
			throws UserNotFoundException {

		int amount = Integer.parseInt(data.get("amount").toString());
		User existingUser = this.userRepo.findById(userId).orElseThrow(
				() -> new UserNotFoundException("the user with this " + userId + " id not present in the database"));

		try {
			RazorpayClient client = new RazorpayClient(razorPayConfiguration.getRazorpayKey(),
					razorPayConfiguration.getRazorpaySecret());
			JSONObject orderRequest = new JSONObject();

			orderRequest.put("amount", amount * 100);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "receipt #1");

			Order order = client.orders.create(orderRequest);

			LocalDateTime now = LocalDateTime.now();

			// Define a DateTimeFormatter to format the date and time with AM/PM indicator
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm: a");
			String formattedDateTime = now.format(formatter);
			LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

			PaymentActivity payment = new PaymentActivity();
			payment.setAmount(order.get("amount") + "");
			payment.setReceipt(order.get("receipt"));
			payment.setOrderId(order.get("id"));
			payment.setOrderStatus(order.get("status").toString());
			payment.setPayementId(order.get(null));
			payment.setUser(existingUser);

			payment.setLocalDatetime(parsedDateTime);
			paymentRepository.save(payment);

			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(order.toString());

		} catch (RazorpayException e) {

			e.printStackTrace();
			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Something went wrong");
		}

	}

//    Admin payment to hub
	@PostMapping("/order_intialisedadmin/{hubId}")
	@ResponseBody
	public ResponseEntity<String> createOrderHub(@RequestBody Map<String, Object> data, @PathVariable Long hubId)
			throws UserNotFoundException {

		int amount = Integer.parseInt(data.get("amount").toString());
		HubPayment existingUser = hubPaymentRepository.findById(hubId).orElseThrow(
				() -> new UserNotFoundException("the user with this " + hubId + " id not present in the database"));

		try {
			RazorpayClient client = new RazorpayClient(razorPayConfiguration.getRazorpayKey(),
					razorPayConfiguration.getRazorpaySecret());
			JSONObject orderRequest = new JSONObject();

			orderRequest.put("amount", amount * 100);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "receipt #1");

			Order order = client.orders.create(orderRequest);

			LocalDateTime now = LocalDateTime.now();

			// Define a DateTimeFormatter to format the date and time with AM/PM indicator
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm: a");
			String formattedDateTime = now.format(formatter);
			LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

			HubPayment payment = new HubPayment();
			payment.setAmount(order.get("amount") + "");
			payment.setReceipt(order.get("receipt"));
			payment.setOrderId(order.get("id"));
			payment.setOrderStatus(order.get("status").toString());
			payment.setPayementId(order.get(null));
			payment.setHub(existingUser.getHub());

			payment.setLocalDatetime(parsedDateTime);
			hubPaymentRepository.save(payment);

			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(order.toString());

		} catch (RazorpayException e) {

			e.printStackTrace();
			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Something went wrong");
		}

	}

//	@PostMapping("/order_intialisedhub/{driverId}/{hubId}")
//	@ResponseBody
//	public ResponseEntity<String> createOrderHubToDriver(@PathVariable Long driverId, @PathVariable Long hubId) {
//		try {
//			LocalDate today = LocalDate.now();
//			List<DriverPaymentDetail> paymentDetails = driverPaymentDetailRepository
//					.findByDriverIdAndHubIdAndDate(driverId, hubId, today);
//
//			if (paymentDetails.isEmpty()) {
//				return ResponseEntity.badRequest().body("No payment details found for today.");
//			}
//
////			double totalAmount = paymentDetails.stream().mapToDouble(detail -> Double.parseDouble(detail.getAmount())).sum();
//			// Calculate the total amount for today
//			double totalAmount = paymentDetails.stream().mapToDouble(detail -> Double.parseDouble(detail.getAmount()))
//					.sum();
//			int amountInPaise = (int) (totalAmount * 100); // Convert to paise
//
//			RazorpayClient client = new RazorpayClient(razorPayConfiguration.getRazorpayKey(),
//					razorPayConfiguration.getRazorpaySecret());
//			JSONObject orderRequest = new JSONObject();
////			orderRequest.put("amount", totalAmount * 100); // Convert to paise
//			orderRequest.put("amount", amountInPaise);
//			orderRequest.put("currency", "INR");
//			orderRequest.put("receipt", "receipt#1");
//			Order order = client.orders.create(orderRequest);
//
//			DriverPayment payment = new DriverPayment();
////			payment.setAmount(totalAmount);
////			payment.setAmount(String.valueOf(totalAmount)); // Set amount as String
//			payment.setAmount(String.valueOf(amountInPaise)); // Set amount as String in paise
//
//			payment.setReceipt(order.get("receipt"));
//			payment.setOrderId(order.get("id"));
//			payment.setOrderStatus(order.get("status").toString());
//			payment.setPaymentId(order.get("payment_id"));
//			payment.setDriver(driverRepository.findById(driverId).orElse(null));
//			payment.setHub(hubRepository.findById(hubId).orElse(null));
//			payment.setLocalDatetime(LocalDateTime.now());
//			driverPaymentRepository.save(payment);
//
//			return ResponseEntity.ok(order.toString());
//		} catch (RazorpayException e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong with Razorpay");
//		} catch (Exception e) {
//			// Log the exception properly
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("An error occurred while creating the order");
//		}
//	}

	@PostMapping("/order_intialisedhub/{driverId}/{hubId}")
	@ResponseBody
	public ResponseEntity<String> createOrderHubToDriver(@RequestBody Map<String, Object> data,
			@PathVariable Long driverId, @PathVariable Long hubId) throws UserNotFoundException {

		int amount = Integer.parseInt(data.get("amount").toString());
		Driver existingDriver = this.driverRepository.findById(driverId).orElseThrow(
				() -> new UserNotFoundException("the Driver with this " + driverId + " id not present in the database"));

		Hub hub = this.hubRepository.findById(hubId).orElseThrow(
				() -> new UserNotFoundException("the hub with this " + hubId + " id not present in the database"));

		try {
			RazorpayClient client = new RazorpayClient(razorPayConfiguration.getRazorpayKey(),
					razorPayConfiguration.getRazorpaySecret());
			JSONObject orderRequest = new JSONObject();

			orderRequest.put("amount", amount * 100);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "receipt #1");

			Order order = client.orders.create(orderRequest);

			LocalDateTime now = LocalDateTime.now();

			// Define a DateTimeFormatter to format the date and time with AM/PM indicator
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm: a");
			String formattedDateTime = now.format(formatter);
			LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

			DriverPayment payment = new DriverPayment();
			payment.setAmount(order.get("amount") + "");

			payment.setReceipt(order.get("receipt"));
			payment.setOrderId(order.get("id"));
			payment.setOrderStatus(order.get("status").toString());
			payment.setPaymentId(order.get(null));
			payment.setDriver(existingDriver);
			payment.setHub(hub);
			payment.setLocalDatetime(parsedDateTime);
			driverPaymentRepository.save(payment);

			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(order.toString());

		} catch (RazorpayException e) {

			e.printStackTrace();
			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Something went wrong");
		}

	}

//Rishi
	@GetMapping("/allPaymentsOfDriverOfCurrentMonth")
	public ResponseEntity<Double> getSumOfAllAmountsOfDriverForCurrentMonth() {
		Double sum = hubPaymentService.getSumOfAllAmountsOfDriverForCurrentMonth();
		return ResponseEntity.ok(sum);
	}

//Rishi	
	@GetMapping("/allPaymentsOfDriverOfCurrentMonth/{hubId}")
	public ResponseEntity<Double> getSumOfAllAmountsOfDriverForCurrentMonthByHub(@PathVariable Long hubId) {
		Double sum = hubPaymentService.getSumOfAllAmountsOfDriverForCurrentMonthByHub(hubId);
		return ResponseEntity.ok(sum);
	}

	// hub payment to Employee
	@PostMapping("/order_intialisedhubtoemployee/{employeeId}/{hubId}")
	@ResponseBody
	public ResponseEntity<String> createOrderHubToEmployee(@RequestBody Map<String, Object> data,
			@PathVariable Long employeeId, @PathVariable Long hubId) throws UserNotFoundException {

		int amount = Integer.parseInt(data.get("amount").toString());
		HubEmployee existingUser = this.hubEmployeeRepository.findById(employeeId)
				.orElseThrow(() -> new UserNotFoundException(
						"the Hub Emp with this " + employeeId + " id not present in the database"));

		Hub hub = this.hubRepository.findById(hubId).orElseThrow(
				() -> new UserNotFoundException("the hub with this " + hubId + " id not present in the database"));

		try {
			RazorpayClient client = new RazorpayClient(razorPayConfiguration.getRazorpayKey(),
					razorPayConfiguration.getRazorpaySecret());
			JSONObject orderRequest = new JSONObject();

			orderRequest.put("amount", amount * 100);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "receipt #1");

			Order order = client.orders.create(orderRequest);

			LocalDateTime now = LocalDateTime.now();

			// Define a DateTimeFormatter to format the date and time with AM/PM indicator
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm: a");
			String formattedDateTime = now.format(formatter);
			LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);

			HubEmployeePayment payment = new HubEmployeePayment();
			payment.setAmount(order.get("amount") + "");

			payment.setReceipt(order.get("receipt"));
			payment.setOrderId(order.get("id"));
			payment.setOrderStatus(order.get("status").toString());
			payment.setPaymentId(order.get(null));
			payment.setHubEmployee(existingUser);
			payment.setHub(hub);
			payment.setLocalDatetime(parsedDateTime);
			hubEmployeePaymentRepository.save(payment);

			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(order.toString());

		} catch (RazorpayException e) {

			e.printStackTrace();
			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Something went wrong");
		}

	}

//Rishi
	@GetMapping("/allPaymentsOfHubEmployeeOfCurrentMonth")
	public ResponseEntity<Double> getSumOfAllAmountsOfHubEmployeeForCurrentMonth() {
		Double sum = hubPaymentService.getSumOfAllAmountsOfHubEmployeeForCurrentMonth();
		return ResponseEntity.ok(sum);
	}

//Rishi	
	@GetMapping("/allPaymentsOfHubEmployeeOfCurrentMonth/{hubId}")
	public ResponseEntity<Double> getSumOfAllAmountsOfHubEmployeeForCurrentMonthByHub(@PathVariable Long hubId) {
		Double sum = hubPaymentService.getSumOfAllAmountsOfHubEmployeeForCurrentMonthByHub(hubId);
		return ResponseEntity.ok(sum);
	}

	// Ram
	@PostMapping("/payment_detail_update")
	public ResponseEntity<String> updatePaymentDetails(@RequestBody Map<String, Object> data) {

		PaymentActivity payment = this.paymentRepository.findByOrderId(data.get("orderId").toString());

		if (payment != null) {

			payment.setOrderStatus(data.get("status").toString());
			payment.setPayementId(data.get("paymentId").toString());
			this.paymentRepository.save(payment);
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(" payment details updated");
		} else {

			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Internal error ocurred");
		}

	}

	@RequestMapping(value = "/generateQRCode", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> generateQRCode(@RequestParam String orderId) {
		try {
			// Logic to generate QR code URL for the given order ID
			String qrCodeUrl = "Generate QR code URL logic here";
			return new ResponseEntity<>(qrCodeUrl, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to generate QR code", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/hubEmployeePaymentUpdate")
	public ResponseEntity<String> hubEmployeePaymentUpdate(@RequestBody Map<String, Object> data) {

		HubEmployeePayment payment = this.hubEmployeePaymentRepository.findByOrderId(data.get("orderId").toString());

		if (payment != null) {

			payment.setOrderStatus(data.get("status").toString());
			payment.setPaymentId(data.get("paymentId").toString());

			this.hubEmployeePaymentRepository.save(payment);
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(" payment details updated");
		} else {

			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Internal error ocurred");
		}

	}

	@PostMapping("/driverPaymentUpdate")
	public ResponseEntity<String> driverPaymentUpdate(@RequestBody Map<String, Object> data) {

		DriverPayment payment = this.driverPaymentRepository.findByOrderId(data.get("orderId").toString());

		if (payment != null) {

			payment.setOrderStatus(data.get("status").toString());
			payment.setPaymentId(data.get("paymentId").toString());

			this.driverPaymentRepository.save(payment);
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(" payment details updated");
		} else {

			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Internal error ocurred");
		}

	}

	// AADARSH KAUSHIK

	// http://localhost:8080/payment/courier/create-order
	@PostMapping("/courier/create-order")
	public ResponseEntity<OrderResponse> createOrder(@RequestBody CourierBookingDto courierBookingDto) {
		return ResponseEntity.ok(courierService.createOrder(courierBookingDto));
	}

	// http://localhost:8080/payment/courier/make-payment
	@PostMapping("courier/verify-payment")
	public ResponseEntity<PaymentResponse> verifyPayment(@RequestBody Map<String, Object> paymentData) {
		return ResponseEntity.ok(courierService.verifyPayment(paymentData));
	}

}