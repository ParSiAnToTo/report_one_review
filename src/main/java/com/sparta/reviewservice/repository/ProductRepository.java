package com.sparta.reviewservice.repository;

import com.sparta.reviewservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
