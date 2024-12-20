package com.sparta.reviewservice.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {
    private Long totalCount;
    private float score;
    private Long cursor;
    private List<ReviewListDto> reviews;

    @Builder
    public ReviewResponseDto(Long totalCount, float score, Long cursor, List<ReviewListDto> reviews) {
        this.totalCount = totalCount;
        this.score = score;
        this.cursor = cursor;
        this.reviews = reviews;
    }
}
