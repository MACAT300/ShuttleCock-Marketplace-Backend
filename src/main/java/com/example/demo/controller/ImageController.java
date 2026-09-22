package com.example.demo.controller;

import com.example.demo.model.Image;
import com.example.demo.service.ImageCacheService;
import com.example.demo.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ImageController {

    private final ImageService imageService;
    private final ImageCacheService imageCacheService;

    public ImageController(
            ImageService imageService,
            ImageCacheService imageCacheService) {

        this.imageService = imageService;
        this.imageCacheService = imageCacheService;
    }

    // GET all images belonging to a product
    @GetMapping("/products/{productId}/images")
    public ResponseEntity<?> getProductImages(
            @PathVariable int productId) {

        List<Image> images =
                imageService.getImagesByProductId(productId);

        return ResponseEntity.ok(images);
    }

    // POST product image
    @PostMapping("/products/{productId}/images")
    public ResponseEntity<?> addProductImage(
            @PathVariable int productId,
            @RequestBody Image image) {

        image.setProductId(productId);

        Image newImage = imageService.addImage(image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newImage);
    }

    // UPDATE image
    @PutMapping("/images/{id}")
    public ResponseEntity<?> updateImage(
            @PathVariable int id,
            @RequestBody Image image) {

        Image updated = imageService.updateImage(id, image);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }

    // DELETE image
    @DeleteMapping("/images/{id}")
    public ResponseEntity<?> deleteImage(
            @PathVariable int id) {

        boolean deleted = imageService.deleteImage(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // GET cached product images
    @GetMapping("/products/{productId}/images/cached")
    public ResponseEntity<?> getCachedProductImages(
            @PathVariable int productId) {

        List<Image> images =
                imageService.getImagesByProductId(productId);

        var result = images.stream()
                .map(img -> Map.of(
                        "id", img.getId(),
                        "sortOrder", img.getSortOrder(),
                        "url", imageCacheService
                                .getCachedImageUrl(img)
                ))
                .toList();

        return ResponseEntity.ok(result);
    }
}