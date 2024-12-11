package com.sparta.reviewservice.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    private Long productId;
    private int cursor;
    private int size;

    @Builder
    public ReviewRequestDto(Long productId, int cursor, int size) {
        this.productId = productId;
        this.cursor = cursor;
        this.size = size;
    }
}
