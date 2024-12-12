package com.sparta.reviewservice.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewListDto {
    private Long id;
    private Long userId;
    private float score;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    @Builder
    public ReviewListDto(Long id, Long userId, float score, String content, String imageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.score = score;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

}
