package com.example.demo.controller;

import com.example.demo.model.Brand;
import com.example.demo.model.Image;
import com.example.demo.service.BrandService;
import com.example.demo.service.ImageCacheService;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class BrandController {
    private final BrandService brandService;
    private final ImageCacheService imageCacheService;
    public BrandController(BrandService brandService,ImageCacheService imageCacheService){
        this.brandService = brandService;
        this.imageCacheService = imageCacheService;
    }

    // GET ALL
    @GetMapping(
            path= "/brands"
    )
    public ResponseEntity getBrands(){
        var currentBrand = brandService.getBrands();
        return ResponseEntity.ok(currentBrand);
    }

    // GET by id
    @GetMapping("/brands/{id}")
    public ResponseEntity<?> getBrand(
            @PathVariable int id) {

        Brand Brand = brandService.getBrandById(id);
        if (Brand == null) {
            return ResponseEntity.status
                    (HttpStatus.NOT_FOUND).body("Brand not found");
        }
        return ResponseEntity.ok(Brand);
    }

    // POST
    @PostMapping("/brands")
    public ResponseEntity<?> addBrand(
            @RequestBody Brand brand) {

        Brand newBrand = brandService.addBrand(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBrand);
    }

    // PUT
    @PutMapping("/brands/{id}")
    public ResponseEntity<?> updateBrand(
            @PathVariable int id,
            @RequestBody Brand brand) {

        Brand updated = brandService.updateBrand(id, brand);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/brands/{id}")
    public ResponseEntity<?> deleteBrand(
            @PathVariable int id) {

        boolean deleted = brandService.deleteBrand(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // 拿品牌logo的本地缓存URL
    // todo: cetnralized image instead of seperating out
    @GetMapping("/brands/{id}/image/cached")
    public ResponseEntity<?> getCachedBrandImage(
            @PathVariable int id) {

        Brand brand = brandService.getBrandById(id);

        if (brand == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Brand not found");
        }

        if (brand.getImage() == null) {
            return ResponseEntity
                    .badRequest()
                    .body("This brand has no image");
        }

        String url = imageCacheService.getCachedImageUrl(
                brand.getImage()
        );

        return ResponseEntity.ok(
                Map.of("url", url)
        );
    }

    // POST 新增 / 设置品牌 logo
    @PostMapping("/brands/{id}/image")
    public ResponseEntity<?> addBrandImage(
            @PathVariable int id,
            @RequestBody Image image) {

        Brand brand = brandService.getBrandById(id);

        if (brand == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Brand not found");
        }

        Brand updated = brandService.setBrandImage(id, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(updated);
    }

}
