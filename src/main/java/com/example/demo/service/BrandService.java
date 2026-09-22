package com.example.demo.service;

import com.example.demo.model.Brand;
import com.example.demo.model.Image;
import com.example.demo.repo.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BrandService {
    private final BrandRepository brandRepository;
    private final ImageService imageService;

    public BrandService(BrandRepository brandRepository,ImageService imageService) {
        this.brandRepository = brandRepository;
        this.imageService = imageService;
    }

    // GET ALL
    public List<Brand> getBrands() {
        return brandRepository.findAll();
    }

    // GET by ID
    public Brand getBrandById(int id) {
        return brandRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Brand detail not found"));
    }

    // ADD
    public Brand addBrand(Brand Brand) {

        return brandRepository.save(Brand);
    }

    // UPDATE
    public Brand updateBrand(int id, Brand brand) {

        Brand existing = brandRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setName(brand.getName());
        existing.setDescription(brand.getDescription());
        existing.setImageId(brand.getImageId());
        return brandRepository.save(existing);
    }

    // DELETE
    public boolean deleteBrand(int id) {

        if (!brandRepository.existsById(id)) {
            return false;
        }

        brandRepository.deleteById(id);
        return true;
    }



    // 新增/替换品牌 logo：先建 Image，再绑定到 Brand
    public Brand setBrandImage(int brandId, Image image) {

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Brand not found"));

        // logo 图片不属于任何 product
        image.setProductId(null);

        Image savedImage = imageService.addImage(image);

        brand.setImageId(savedImage.getId());

        return brandRepository.save(brand);
    }
}
