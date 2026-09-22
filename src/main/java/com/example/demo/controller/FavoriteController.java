package com.example.demo.controller;

import com.example.demo.model.Favorite;
import com.example.demo.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites(@RequestParam int userId) {
        List<Favorite> favorites = favoriteService.getFavoritesByUser(userId);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/favorites")
    public ResponseEntity<?> addFavorite(@RequestBody Map<String, Integer> body) {
        Integer userId = body.get("userId");
        Integer productId = body.get("productId");

        if (userId == null || productId == null) {
            return ResponseEntity.badRequest().body("userId and productId are required");
        }

        Favorite favorite = favoriteService.addFavorite(userId, productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
    }

    @DeleteMapping("/favorites")
    public ResponseEntity<?> removeFavorite(
            @RequestParam int userId,
            @RequestParam int productId) {

        boolean removed = favoriteService.removeFavorite(userId, productId);
        if (!removed) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}