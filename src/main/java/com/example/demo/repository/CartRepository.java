package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;


@Repository
public interface CartRepository extends JpaRepository<CartItem, Integer> {
	@Query("SELECT c FROM CartItem c WHERE c.user.userId = :userId AND c.product.productId = :productId")
	Optional<CartItem> findByUserAndProduct( int userId,  int productId);
    
	
	@Query("SELECT COALESCE(sum(c. quantity), 0) FROM CartItem c where c.user.userId = :userId")
	int countTotalItems( int userId);
	
	@Query("SELECT c FROM CartItem c JOIN FETCH c.product p LEFT JOIN FETCH ProductImage pi On p.productId = pi.product.productId WHERE c.user.userId = :userId")
	List<CartItem> findCartItemsWithProductDetails( int userId);
	
	@Query("UPDATE CartItem c set c.quantity = :quantity where c.id = :cartItemId")
	void updateCartItemQuantity(int cartItemId, int quantity) ;
	

	@Modifying
	@Transactional
	@Query("DELETE FROM CartItem c where c.user.userId = :userId AND c.product.productId = :productId")
	void deleteCartItem(int userId, int productId);


	@Modifying
	@Transactional
	@Query("DELETE FROM CartItem c where c.user.userId = :userId")
	void deleteAllCartItemsByUserId(int userId);

}
