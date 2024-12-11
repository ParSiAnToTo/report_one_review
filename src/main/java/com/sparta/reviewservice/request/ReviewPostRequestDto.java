package com.sparta.reviewservice.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewPostRequestDto {
    private Long userId;
    private float score;
    private String content;
}
