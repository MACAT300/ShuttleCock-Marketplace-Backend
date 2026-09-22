package com.example.demo.service;

import com.example.demo.model.Cart;

import com.example.demo.repo.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    // GET ALL
    public List<Cart> getCarts() {
        return cartRepository.findAll();
    }

    // GET by ID
    public Cart getCartById(int id) {
        return cartRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart not found"));
    }

    // Create Cart for User
    public Cart getOrCreateCartForUser(int userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    try {
                        Cart cart = new Cart();
                        cart.setUserId(userId);
                        return cartRepository.save(cart);
                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        // 极小概率的并发情况：几乎同时有两个请求都判断"没有购物车"、都想新建，
                        // 数据库的唯一约束会挡掉第二个insert。这种情况下直接重新查一次，
                        // 用第一个请求成功建好的那条记录，而不是报错。
                        return cartRepository.findByUserId(userId)
                                .orElseThrow(() -> e);
                    }
                });
    }

    // DELETE
    public boolean deleteCart(int id) {

        if (!cartRepository.existsById(id)) {
            return false;
        }

        cartRepository.deleteById(id);
        return true;
    }
}