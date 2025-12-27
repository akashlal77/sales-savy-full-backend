package com.example.demo.repository;

import java.util.Optional;
import java.util.OptionalDouble;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.JWTToken;


public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer> {
	
	//Custom query to find  tokens by user id
	@Query("SELECT t FROM JWTToken t WHERE t.user.userId = :userId")
	JWTToken findByUserId(@Param("userId") int userId);
	
	
	//Find a token by its value
	Optional<JWTToken> findByToken(String token);
	
	//Custom query to find tokens by user id
	@Modifying
	@Transactional
	@Query("DELETE  FROM JWTToken t WHERE t.user.userId = :userId")
	void deleteByUserId(@Param("userId") int userId);
}
