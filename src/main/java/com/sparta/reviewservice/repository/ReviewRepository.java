package com.sparta.reviewservice.repository;

import com.sparta.reviewservice.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.id DESC")
    List<Review> findTopByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.id < :cursor ORDER BY r.id DESC")
    List<Review> findByProductIdByCursor(@Param("productId") Long productId, @Param("cursor") Long cursor, Pageable pageable);

    Long countByProductId(Long productId);
}
