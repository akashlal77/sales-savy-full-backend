package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.entity.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

@Service
public class CartService {
	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ProductImageRepository productImageRepository;
	
	public void addToCart(int userId, int productId, int quantity) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
		Product product = productRepository.findById(productId).orElseThrow(()->new IllegalArgumentException("Product not found with Id: " + productId));
		
		
		Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(userId, productId);
		if(existingItem.isPresent()) {
			CartItem cartItem = existingItem.get();
		    cartItem.setQuantity(cartItem.getQuantity() + quantity);
		    cartRepository.save(cartItem);
		}
		else 
		{
			CartItem newItem = new CartItem(user, product, quantity);
			cartRepository.save(newItem);
		}
	}
	public int getCartItemCount(Integer userId) {
		return cartRepository.countTotalItems(userId);
	}


	public Map<String, Object> getCartItems(int userId) {
		List<CartItem> cartItems= cartRepository.findCartItemsWithProductDetails(userId);
		
		Map<String, Object> response = new HashMap<>();
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		response.put("username", user.getUsername());
		response.put("role", user.getRole().toString());
		
		List<Map<String, Object>> products = new ArrayList<>();
		int overallTotalPrice=0;
		
		for(CartItem cartItem: cartItems) {
			Map<String, Object> productDetails = new HashMap<>();
			
			Product product = cartItem.getProduct();
			
			List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(product.getProductId());
			String imageUrl = (productImages != null && !productImages.isEmpty())? productImages.get(0).getImageUrl(): "default-image-url";
			
			
			productDetails.put("product_id", product.getProductId());
			productDetails.put("image_url", imageUrl);
			productDetails.put("name", product.getName());
			productDetails.put("description", product.getDescription());
			productDetails.put("price_per_unit", product.getPrice());
			productDetails.put("quantity", cartItem.getQuantity());
			productDetails.put("total_price", cartItem.getQuantity() * product.getPrice().doubleValue());
			
			products.add(productDetails);
			
			overallTotalPrice += cartItem.getQuantity()*product.getPrice().doubleValue();
		}
		
		Map<String, Object> cart = new HashMap<>();
		cart.put("products", products);
		cart.put("overall_total_price", overallTotalPrice);
		
		response.put("cart", cart);
		
		return response;
		
	}


public void updateCartItemQuantity(int userId, int productId, int quantity) {
		Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(userId, productId);
		
		if(existingItem.isPresent()) 
		{
			if(quantity == 0) {
 			deleteItem (userId, productId);
			}
			else {
			CartItem cartitem  = existingItem.get();
			cartitem.setQuantity(cartitem.getQuantity() +quantity);
			cartRepository.save(cartitem);
			}
		}
	}
	
	public void deleteItem(int userId, int productId) {
		cartRepository.deleteCartItem(userId, productId);
	}

	public void deleteCartItem(Integer userId, int productId) {
		User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("User not found"));
		
		Product product = productRepository.findById(productId).orElseThrow(()-> new IllegalArgumentException("Product not found"));		
  	cartRepository.deleteCartItem(userId, productId);
		
   }


	
 }


