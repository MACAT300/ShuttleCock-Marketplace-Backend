package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repo.OrderItemRepository;
import com.example.demo.repo.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final ProductService productService;
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartService cartService,
                        CartItemService cartItemService,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.productService = productService;
    }

    // checkout：cart to order
    // checkout：cart to order
    public Order checkout(int userId, List<Integer> selectedCartItemIds) {

        Cart cart = cartService.getOrCreateCartForUser(userId);
        List<CartItem> allCartItems = cartItemService.getItemsByCartId(cart.getId());

        // 没指定就当作"全部结账"，兼容旧行为
        List<CartItem> cartItems;
        if (selectedCartItemIds == null || selectedCartItemIds.isEmpty()) {
            cartItems = allCartItems;
        } else {
            cartItems = allCartItems.stream()
                    .filter(item -> selectedCartItemIds.contains(item.getId()))
                    .toList();
        }

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("No items selected for checkout");
        }

        // 1. 建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");

        double total = 0;

        // 2. 先算总价（这里也顺便检查库存）
        for (CartItem item : cartItems) {

            Product product =
                    productService.getProductId(item.getProductId());

            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Not enough stock for product: "
                                + product.getName()
                );
            }

            total += product.getPrice() * item.getQuantity();
        }
        order.setTotalAmount(total);
        order = orderRepository.save(order);

        // 3. 把每个购物车项目转成订单明细（价格快照）
        for (CartItem item : cartItems) {
            Product product = productService.getProductId(item.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItemRepository.save(orderItem);
        }

        // 4. 把已经下单的这些项目从购物车移除，没选的留着
        for (CartItem item : cartItems) {
            cartItemService.deleteCartItem(item.getId());
        }

        return order;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUser(int userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public List<OrderItem> getOrderItems(int orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public void updateOrderStatus(int orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }
}
