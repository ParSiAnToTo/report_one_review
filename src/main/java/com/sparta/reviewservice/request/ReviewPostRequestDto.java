package com.sparta.reviewservice.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ReviewPostRequestDto {

    @NotNull(message = "Required User ID")
    private Long userId;

    @DecimalMin(value = "0.0", message = "Score is too low")
    @DecimalMax(value = "5.0", message = "Score is too high")
    private float score;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 500, message = "Content must be 500 characters or less")
    private String content;

    @Builder
    public ReviewPostRequestDto(Long userId, float score, String content) {
        this.userId = userId;
        this.score = score;
        this.content = content;
    }
}
