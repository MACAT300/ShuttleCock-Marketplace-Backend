package com.example.demo.repo;

import com.example.demo.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUserId(int userId);
    Optional<Favorite> findByUserIdAndProductId(int userId, int productId);
    boolean existsByUserIdAndProductId(int userId, int productId);
    void deleteByUserIdAndProductId(int userId, int productId);
}