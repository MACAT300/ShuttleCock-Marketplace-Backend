package com.example.demo.repo;

import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCartId(int cartId);
    Optional<CartItem> findByCartIdAndProductId(int cartId, int productId);
}
