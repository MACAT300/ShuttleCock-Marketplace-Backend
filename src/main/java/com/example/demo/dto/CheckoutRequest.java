package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CheckoutRequest {

    @NotNull(message = "userId is required")
    private Integer userId;

    // 可选：只结账这些购物车项目的id。不传或传空 = 结账购物车里全部商品（保留旧行为）
    private List<Integer> cartItemIds;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Integer> getCartItemIds() {
        return cartItemIds;
    }

    public void setCartItemIds(List<Integer> cartItemIds) {
        this.cartItemIds = cartItemIds;
    }
}