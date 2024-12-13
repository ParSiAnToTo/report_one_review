package com.sparta.reviewservice.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewPostRequestDto {
    private Long userId;
    private float score;
    private String content;

    @Builder
    public ReviewPostRequestDto(Long userId, float score, String content) {
        this.userId = userId;
        this.score = score;
        this.content = content;
    }
}
