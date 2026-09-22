package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET ALL
    @GetMapping("/carts")
    public ResponseEntity<?> getCarts() {
        return ResponseEntity.ok(cartService.getCarts());
    }

    // GET by id
    @GetMapping("/carts/{id}")
    public ResponseEntity<?> getCart(@PathVariable int id) {
        try {
            return ResponseEntity.ok(cartService.getCartById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 获取或创建某用户的购物车，body 传 { "userId": 1 }
    @PostMapping("/carts")
    public ResponseEntity<?> getOrCreateCart(@RequestBody Cart cart) {
        Cart result = cartService.getOrCreateCartForUser(cart.getUserId());
        return ResponseEntity.ok(result);
    }

    // DELETE
    @DeleteMapping("/carts/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable int id) {
        boolean deleted = cartService.deleteCart(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}