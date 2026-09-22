package com.example.demo.service;

import com.example.demo.model.Image;
import com.example.demo.repo.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<Image> getImagesByProductId(int productId) {
        return imageRepository
                .findByProductIdOrderBySortOrderAsc(productId);
    }

    public Image getImageById(int id) {
        return imageRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Image not found"));
    }

    public Image addImage(Image image) {
        return imageRepository.save(image);
    }

    public Image updateImage(int id, Image image) {

        Image existing = imageRepository
                .findById(id)
                .orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setUrl(image.getUrl());
        existing.setProductId(image.getProductId());
        existing.setSortOrder(image.getSortOrder());

        return imageRepository.save(existing);
    }

    public boolean deleteImage(int id) {

        if (!imageRepository.existsById(id)) {
            return false;
        }

        imageRepository.deleteById(id);
        return true;
    }
}