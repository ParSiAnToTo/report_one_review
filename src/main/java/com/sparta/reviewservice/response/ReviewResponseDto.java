package com.sparta.reviewservice.response;

import com.sparta.reviewservice.entity.Review;
import lombok.Builder;

import java.util.List;

public class ReviewResponseDto {
    private Long totalCount;
    private float score;
    private int cursor;
    private List<Review> reviews;

    @Builder
    public ReviewResponseDto(Long totalCount, float score, int cursor, List<Review> reviews) {
        this.totalCount = totalCount;
        this.score = score;
        this.cursor = cursor;
        this.reviews = reviews;
    }
}
