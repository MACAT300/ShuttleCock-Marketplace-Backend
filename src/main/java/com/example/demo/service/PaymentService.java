package com.example.demo.service;

import com.example.demo.dto.PaymentIntentResponse;
import com.example.demo.model.*;
import com.example.demo.repo.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final CartService cartService;
    private final CartItemService cartItemService;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderService orderService,
            ProductService productService,
            CartService cartService,
            CartItemService cartItemService) {

        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.productService = productService;
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    // ========== 1. 创建 Checkout Session，返回付款链接 ==========
    public PaymentIntentResponse createCheckoutSession(int orderId) {

        Order order = orderService.getOrderById(orderId);

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalArgumentException(
                    "Order is not payable, current status: " + order.getStatus());
        }

        List<OrderItem> orderItems = orderService.getOrderItems(orderId);
        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order has no items");
        }

        // 检查库存
        for (OrderItem item : orderItems) {
            Product product = productService.getProductId(item.getProductId());
            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Not enough stock for product: " + product.getName());
            }
        }

        try {
            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .putMetadata("orderId", String.valueOf(orderId));

            // 每个商品做成一个 line item
            for (OrderItem item : orderItems) {
                Product product = productService.getProductId(item.getProductId());

                long unitAmountCents = Math.round(product.getPrice() * 100);

                SessionCreateParams.LineItem lineItem =
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) item.getQuantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(unitAmountCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(product.getName())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build();

                paramsBuilder.addLineItem(lineItem);
            }

            Session session = Session.create(paramsBuilder.build());

            // 建一条 PENDING 的 Payment 记录
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(order.getTotalAmount());
            payment.setPaymentMethod("STRIPE");
            payment.setStatus("PENDING");
            payment.setStripePaymentIntentId(session.getId()); // 先存 session id
            Payment saved = paymentRepository.save(payment);

            return new PaymentIntentResponse(session.getUrl(), saved.getId());

        } catch (StripeException e) {
            throw new IllegalArgumentException("Stripe error: " + e.getMessage());
        }
    }

    // ========== 2. Webhook 收到"支付成功"事件后调用 ==========
    public void handleCheckoutSessionCompleted(String sessionId, String paymentIntentId) {

        Payment payment = paymentRepository
                .findByStripePaymentIntentId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for session: " + sessionId));

        if ("SUCCESS".equals(payment.getStatus())) {
            return; // 幂等，避免 webhook 重复触发
        }

        int orderId = payment.getOrderId();
        List<OrderItem> orderItems = orderService.getOrderItems(orderId);

        // 扣库存
        for (OrderItem item : orderItems) {
            Product product = productService.getProductId(item.getProductId());
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productService.saveProduct(product);
        }

        payment.setStatus("SUCCESS");
        payment.setTransactionRef(paymentIntentId);
        paymentRepository.save(payment);

        orderService.updateOrderStatus(orderId, "PAID");

        // 清空购物车
        Order order = orderService.getOrderById(orderId);
        Cart cart = cartService.getOrCreateCartForUser(order.getUserId());
        List<CartItem> cartItems = cartItemService.getItemsByCartId(cart.getId());
        for (CartItem item : cartItems) {
            cartItemService.deleteCartItem(item.getId());
        }
    }

    public void handlePaymentFailure(String sessionId) {
        paymentRepository.findByStripePaymentIntentId(sessionId)
                .ifPresent(payment -> {
                    payment.setStatus("FAILED");
                    paymentRepository.save(payment);
                });
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(int id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    public List<Payment> getPaymentsByOrder(int orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}