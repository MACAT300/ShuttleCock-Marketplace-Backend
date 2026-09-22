package com.example.demo.service;

import com.example.demo.model.Favorite;
import com.example.demo.repo.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public List<Favorite> getFavoritesByUser(int userId) {
        return favoriteRepository.findByUserId(userId);
    }

    public Favorite addFavorite(int userId, int productId) {
        return favoriteRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(() -> {
                    Favorite favorite = new Favorite();
                    favorite.setUserId(userId);
                    favorite.setProductId(productId);
                    return favoriteRepository.save(favorite);
                });
    }

    public boolean removeFavorite(int userId, int productId) {
        if (!favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            return false;
        }
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        return true;
    }

    public boolean isFavorite(int userId, int productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }


}