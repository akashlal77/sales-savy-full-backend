package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class AdminProductService {
	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;
	private final CategoryRepository categoryRepository;
	
	public AdminProductService(ProductRepository productRepository, ProductImageRepository productImageRepository,
			CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
		this.categoryRepository = categoryRepository;
	}
	
	public Product addProductWithImage(String name, String description, Double price, Integer stock, Integer categoryId,String imageUrl) {
		//Validate the category
		Optional<Category> category = categoryRepository.findById(categoryId);
		
		if(category.isEmpty()) {
			throw new IllegalArgumentException("Invalid category ID");
		}
		
		//Create and save the product the product 
		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setPrice(BigDecimal.valueOf(price));
		product.setStock(stock);
		product.setCategory(category.get());
		product.setCreatedAt(LocalDateTime.now());
		product.setUpdatedAt(LocalDateTime.now());
		
		Product savedProduct = productRepository.save(product);
		
		//create and save the product image
		
		if(imageUrl  != null && !imageUrl.isEmpty()) {
			ProductImage productImage = new ProductImage();
			productImage.setProduct(savedProduct);
			productImage.setImageUrl(imageUrl);
			productImageRepository.save(productImage);
		} else  {
			throw new IllegalArgumentException("Product image URL cannot be empty");
		} 
		return savedProduct;
	}
	public void deleteProduct(Integer productId) {
		//Check if the product exists
		if(!productRepository.existsById(productId)) {
			throw new IllegalArgumentException("Product not found");
		}
		
		//Delete associated product images
		productImageRepository.deleteById(productId);
	}
	
}
