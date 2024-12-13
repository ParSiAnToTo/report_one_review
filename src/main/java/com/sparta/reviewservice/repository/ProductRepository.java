package com.sparta.reviewservice.repository;

import com.sparta.reviewservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Query("UPDATE Product p SET p.reviewCount = p.reviewCount + 1, p.score = ((p.score * p.reviewCount + :newScore) / (p.reviewCount + 1)) WHERE p.id = :productId")
    void updateStatistics(@Param("productId") Long productId, @Param("newScore") float newScore);
}
