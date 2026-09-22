package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    // GET ALL
    public List<Product> getProducts(){
        return productRepository.findAll();
    }
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    // GET id
    public Product getProductId(int id) {

        return productRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Product not found"));
    }

    // Search & Filter
    public List<Product> searchProducts(String name, Integer brandId) {

        boolean hasName = name != null && !name.isBlank();
        boolean hasBrand = brandId != null;

        if (hasName && hasBrand) {
            return productRepository.findByNameContainingIgnoreCaseAndBrandId(name, brandId);
        }
        if (hasName) {
            return productRepository.findByNameContainingIgnoreCase(name);
        }
        if (hasBrand) {
            return productRepository.findByBrandId(brandId);
        }
        return productRepository.findAll();
    }

    // Add product
    public Product addProduct(Product product){

        if (!org.springframework.util.StringUtils.hasText(product.getName())) {
            throw new IllegalArgumentException("Name is required");
        }

        if(product.getPrice() <= 0) {
            throw new IllegalArgumentException
                    ("Price cannot be less than or equal to zero");
        }

        if(product.getQuantity() <= 0) {
            throw new IllegalArgumentException
                    ("Price cannot be negative");
        }

        return productRepository.save(product);
    }

    public Product updateProduct(int id,Product product) {
        Product existing = productRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());
        existing.setBrandId(product.getBrandId());
        existing.setMainImageId(product.getMainImageId());
        return productRepository.save(existing);
    }

    public boolean deleteProduct(int id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);

        return true;
    }


}
