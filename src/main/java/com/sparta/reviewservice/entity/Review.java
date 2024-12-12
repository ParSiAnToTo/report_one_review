package com.sparta.reviewservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review", indexes = @Index(name = "composite_idx", columnList = "productId, userId", unique = true))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private float score;

    @Column(nullable = false, length = 500)
    private String content;

    @Column()
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Review(Product product, Long userId, float score, String content, String imageUrl, LocalDateTime createdAt) {
        this.product = product;
        this.userId = userId;
        this.score = score;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
