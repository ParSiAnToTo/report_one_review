package com.sparta.reviewservice.repository;

import com.sparta.reviewservice.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT t FROM Review t WHERE t.productId = :productId")
    Page<Review> findByProductId(@Param("productId") Long productId, Pageable pageable);
}
