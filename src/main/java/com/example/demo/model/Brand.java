    package com.example.demo.model;

    import jakarta.persistence.*;
    import lombok.Data;

    @Data
    @Entity
    @Table(name="Brand")
    public class Brand {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String name;
        private String description;
        private Integer imageId;

        @ManyToOne
        @JoinColumn(name = "imageId", insertable = false, updatable = false)
        private Image image;
    }
