package com.example.demo.controller;

import com.example.demo.dto.PaymentIntentResponse;
import com.example.demo.model.Payment;
import com.example.demo.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    // 改成：创建 Stripe PaymentIntent，返回 client_secret 给 App
    @PostMapping("/payments/checkout")
    public ResponseEntity<?> checkout(
            @Valid @RequestBody com.example.demo.dto.PaymentRequest request) {

        try {
            PaymentIntentResponse response =
                    paymentService.createCheckoutSession(request.getOrderId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/payments")
    public ResponseEntity<?> getAllPayments(){
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/payments/{id}")
    public ResponseEntity<?> getPayment(@PathVariable int id){
        try {
            return ResponseEntity.ok(paymentService.getPaymentById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/payments/orders/{orderId}")
    public ResponseEntity<?> getPaymentsByOrder(@PathVariable int orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrder(orderId));
    }
}