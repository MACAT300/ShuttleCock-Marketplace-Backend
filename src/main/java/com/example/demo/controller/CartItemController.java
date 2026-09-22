package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartItemController {
    private final CartItemService cartItemService;
    public CartItemController(CartItemService cartItemService){
        this.cartItemService = cartItemService;
    }

    // GET items belonging to a specific cart
    @GetMapping("/carts/{cartId}/items")
    public ResponseEntity<?> getItemsByCart(@PathVariable int cartId) {
        return ResponseEntity.ok(cartItemService.getItemsByCartId(cartId));
    }

    // GET ALL
    @GetMapping("/cartItems")
    public ResponseEntity<?> getCartItems() {
        return ResponseEntity.ok(cartItemService.getCartItems());
    }

    // POST 加商品到购物车
    @PostMapping("/cartItems")
    public ResponseEntity<?> addCartItem(@RequestBody CartItem item) {
        CartItem newItem = cartItemService.addCartItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }

    // PUT 改数量
    @PutMapping("/cartItems/{id}")
    public ResponseEntity<?> updateCartItem(@PathVariable int id, @RequestBody CartItem item) {
        CartItem updated = cartItemService.updateCartItem(id, item);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/cartItems/{id}")
    public ResponseEntity<?> deleteCartItem(@PathVariable int id) {
        boolean deleted = cartItemService.deleteCartItem(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
