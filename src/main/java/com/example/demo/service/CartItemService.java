package com.example.demo.service;

import com.example.demo.model.CartItem;
import com.example.demo.repo.CartItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    public CartItemService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public List<CartItem> getCartItems() {
        return cartItemRepository.findAll();
    }

    public List<CartItem> getItemsByCartId(int cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    public CartItem getCartItemById(int id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
    }

    public CartItem addCartItem(CartItem item) {
        if (item.getQuantity() <= 0) {
            item.setQuantity(1);
        }

        return cartItemRepository
                .findByCartIdAndProductId(item.getCartId(), item.getProductId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + item.getQuantity());
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> cartItemRepository.save(item));
    }

    public CartItem updateCartItem(int id, CartItem item) {
        CartItem existing = cartItemRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setQuantity(item.getQuantity());
        return cartItemRepository.save(existing);
    }

    public boolean deleteCartItem(int id) {
        if (!cartItemRepository.existsById(id)) {
            return false;
        }
        cartItemRepository.deleteById(id);
        return true;
    }
}
