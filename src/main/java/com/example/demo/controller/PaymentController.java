package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.OrderItem;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
@RequestMapping("api/payment")
public class PaymentController {
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/create")
	public ResponseEntity<Map<String, String>> createPaymentOrder(
	        @RequestBody Map<String, Object> requestBody,
	        HttpServletRequest request) {

	    try {
	        User user = (User) request.getAttribute("authenticatedUser");
	        if (user == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(Map.of("error", "User not authenticated"));
	        }

	        BigDecimal totalAmount = new BigDecimal(requestBody.get("totalAmount").toString());
	        List<Map<String, Object>> cartItemsRaw = (List<Map<String, Object>>) requestBody.get("cartItems");

	        List<OrderItem> cartItems = cartItemsRaw.stream().map(item -> {
	            OrderItem orderItem = new OrderItem();
	            orderItem.setProductId((Integer) item.get("productId"));
	            orderItem.setQuantity((Integer) item.get("quantity"));
	            BigDecimal pricePerUnit = new BigDecimal(item.get("price").toString());
	            orderItem.setPricePerUnit(pricePerUnit);
	            orderItem.setTotalPrice(pricePerUnit.multiply(BigDecimal.valueOf((Integer) item.get("quantity"))));
	            return orderItem;
	        }).collect(Collectors.toList());

	        String razorpayOrderId = paymentService.createOrder(user.getUserId(), totalAmount, cartItems);

	        return ResponseEntity.ok(Map.of("order_id", razorpayOrderId));

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of("error", "Invalid request data: " + e.getMessage()));
	    }
	}

	@PostMapping("/verify")
	public ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
		try {
			User user = (User) request.getAttribute("authenticatedUser");
			if(user == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
			}
			
			int userId = user.getUserId();
			
			String razorpayOrderId = (String) requestBody.get("razorpay_order_id");
			String razorpayPaymentId = (String) requestBody.get("razorpay_payment_id");
			String razorpaySignature = (String) requestBody.get("razorpay_signature");

			
			boolean isVerified = paymentService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature, userId);
			
			if(isVerified) {
				return ResponseEntity.ok("Payment verified successfully");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying payment: " + e.getMessage());
		}
	}
	
}
