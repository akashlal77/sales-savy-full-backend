package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entity.ProductImage;

import jakarta.transaction.Transactional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

	List<ProductImage> findByProduct_ProductId(int productId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM ProductImage pi WHERE pi.product.productId = :productId")
	void deleteByProductId(Integer productId);
}
