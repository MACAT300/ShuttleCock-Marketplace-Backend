package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Entity
@Table(name="Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private int brandId;
    private Integer mainImageId;
    private String imageIds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brandId",insertable = false, updatable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mainImageId",insertable = false, updatable = false)
    private Image image;

    @Transient
    private List<Integer> imageIdList;


    public List<Integer> getImageIdList() {
        if (imageIds == null || imageIds.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(imageIds.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }

}
