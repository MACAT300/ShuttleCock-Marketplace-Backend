package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ImageCacheService;
import com.example.demo.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ProductController {
    private final ProductService productService;
    private final ImageCacheService imageCacheService;
    public ProductController(ProductService productService,ImageCacheService imageCacheService){
        this.productService = productService;
        this.imageCacheService = imageCacheService;
    }

    // 拿商品预览图的本地缓存URL
    // todo: centralized image.
    @GetMapping("/products/{id}/preview-image/cached")
    public ResponseEntity<?> getCachedPreviewImage(
            @PathVariable int id) {

        Product product = productService.getProductId(id);

        if (product.getImage() == null) {
            return ResponseEntity
                    .badRequest()
                    .body("This product has no preview image");
        }

        String url =
                imageCacheService.getCachedImageUrl(
                        product.getImage()
                );

        return ResponseEntity.ok(
                Map.of("url", url)
        );
    }

    // GET ALL
    @GetMapping(
            path= "/products")
    public ResponseEntity<?> getProducts(
            @RequestParam(required = false)String name,
            @RequestParam(required = false)Integer brandId
    ){
        var result = productService.searchProducts(name, brandId);
        return ResponseEntity.ok(result);
    }

    // GET by id
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductId(
            @PathVariable int id) {

        Product product = productService.getProductId(id);
        if (product == null) {
            return ResponseEntity.status
                    (HttpStatus.NOT_FOUND).body("Product not found");
        }
        return ResponseEntity.ok(product);
    }

    // POST
    @PostMapping("/products")
    public ResponseEntity<?> addProduct(
            @RequestBody Product product) {

        Product newProduct = productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    // PUT
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable int id,
            @RequestBody Product product) {

        Product updated = productService.updateProduct(id, product);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }


    // DELETE
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable int id) {

        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
