package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;


@Service
public class OrderService {
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired 
	private ProductImageRepository productImageRepository; 
	
	
	public Map<String, Object> getOrdersForUser(User user) {
		List<OrderItem> orderItems = orderItemRepository.findSuccessfulOrderItemsByUserId(user.getUserId());
		
		
		Map<String, Object> response  = new HashMap<>();
		response.put("username", user.getUsername());
		response.put("role",  user.getRole());
		
		
		List<Map<String, Object>> products = new ArrayList<>();
		
		for(OrderItem item : orderItems) {
			Product product = productRepository.findById(item.getProductId()).orElse(null);
			if(product == null) {
				continue;
			}
			
			//Fetch the product image (if available)
			List<ProductImage> images = productImageRepository.findByProduct_ProductId(product.getProductId());
			String imageUrl = images.isEmpty()? null: images.get(0).getImageUrl();
			
			//Create a product details map
			Map<String, Object> productDetails = new HashMap<>();
			productDetails.put("order_id", item.getOrder().getOrderId());
			productDetails.put("quantity", item.getQuantity());
			productDetails.put("total_price", item.getTotalPrice());
			productDetails.put("image_url", imageUrl);
			productDetails.put("product_id", item.getProductId());
			productDetails.put("name", product.getName());
			productDetails.put("description", product.getDescription());
			productDetails.put("price_per_unit", item.getPricePerUnit());
			
			
			products.add(productDetails);
		}
		
		//Add the products list to the response
		response.put("products", products);
		return response;
	}
}
