package com.sparta.reviewservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, length = 20, name = "id")
    private Long id;

    @Column(nullable = false, name = "reviewCount")
    private Long reviewCount;

    @Column(nullable = false, name = "score")
    private float score;

    @Builder
    public Product(Long id, Long reviewCount, float score) {
        this.id = id;
        this.reviewCount = reviewCount;
        this.score = score;
    }
}
