package com.sparta.reviewservice.controller;

import com.sparta.reviewservice.request.ReviewPostRequestDto;
import com.sparta.reviewservice.request.ReviewRequestDto;
import com.sparta.reviewservice.response.ReviewResponseDto;
import com.sparta.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping(value = "/{productId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReviews(@PathVariable("productId") Long productId,
                                        @RequestParam(value = "cursor", defaultValue = "0") Long cursor,
                                        @RequestParam(value = "size", defaultValue = "10") int size) {

        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                .productId(productId)
                .cursor(cursor)
                .size(size)
                .build();

        ReviewResponseDto reviewResponseDto = reviewService.getReviews(reviewRequestDto);
        return ResponseEntity.ok(reviewResponseDto);
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<?> postReview(@PathVariable("productId") Long productId,
                                        @Validated @RequestPart("data") ReviewPostRequestDto reviewPostRequestDto,
                                        @RequestPart(value = "image", required = false) MultipartFile img) {

        reviewService.post(productId, reviewPostRequestDto, img);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
