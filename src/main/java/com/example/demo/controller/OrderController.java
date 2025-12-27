package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true") 
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

   
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrdersForUser(HttpServletRequest request) {
        try {
            // Retrieve the authenticated user from the request
            User authenticatedUser = (User) request.getAttribute("authenticatedUser");

            // Handle unauthenticated requests
            if (authenticatedUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            // Fetch orders for the user via the service layer
            Map<String, Object> response = orderService.getOrdersForUser(authenticatedUser);

            
            return ResponseEntity.ok(response);
            
            
        } catch (IllegalArgumentException e) {
        	
            // Handle cases where user details are invalid or missing
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        
        } catch (Exception e) {
            
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}