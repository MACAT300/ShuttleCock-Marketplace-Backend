package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int orderId;
    private double amount;
    private String paymentMethod;
    private String status = "PENDING";
    private String transactionRef;
    private String stripePaymentIntentId;   // 新增
}