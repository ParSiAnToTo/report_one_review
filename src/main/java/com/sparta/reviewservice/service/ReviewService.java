package com.sparta.reviewservice.service;

import com.sparta.reviewservice.entity.Product;
import com.sparta.reviewservice.entity.Review;
import com.sparta.reviewservice.repository.ProductRepository;
import com.sparta.reviewservice.repository.ReviewRepository;
import com.sparta.reviewservice.request.ReviewPostRequestDto;
import com.sparta.reviewservice.request.ReviewRequestDto;
import com.sparta.reviewservice.response.ReviewListDto;
import com.sparta.reviewservice.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviews(ReviewRequestDto reviewRequestDto) {
        Product product = productRepository.findById(reviewRequestDto.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product Not Found"));

        Pageable pageable = PageRequest.of(0, reviewRequestDto.getSize(), Sort.by(Sort.Direction.DESC, "id"));

        List<Review> reviews;
        if (reviewRequestDto.getCursor() == 0) {
            reviews = reviewRepository.findTopByProductId(product.getId(), pageable);
        } else {
            reviews = reviewRepository.findByProductIdByCursor(product.getId(), reviewRequestDto.getCursor(), pageable);
        }

        List<ReviewListDto> reviewDto = reviews.stream()
                .map(review -> ReviewListDto.builder()
                        .id(review.getId())
                        .userId(review.getUserId())
                        .score(review.getScore())
                        .content(review.getContent())
                        .imageUrl(review.getImageUrl())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        Long nextCursor = reviews.isEmpty() ? null : reviews.get(reviews.size() - 1).getId();

        return ReviewResponseDto.builder()
                .totalCount(reviewRepository.countByProductId(product.getId()))
                .score(product.getScore())
                .cursor(nextCursor)
                .reviews(reviewDto)
                .build();
    }

    @Transactional
    public void post(Long productId, ReviewPostRequestDto reviewPostRequestDto, MultipartFile img) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product Not Found"));

        if (reviewPostRequestDto.getUserId() == null || reviewPostRequestDto.getContent() == null ||
                reviewPostRequestDto.getScore() < 0.0f || reviewPostRequestDto.getScore() > 5.0f) {
            throw new IllegalArgumentException("Invalid Product or Review Post Request");
        }

        Review review = Review.builder()
                .product(product)
                .userId(reviewPostRequestDto.getUserId())
                .score(reviewPostRequestDto.getScore())
                .content(reviewPostRequestDto.getContent())
                .imageUrl(null)
                .build();

        reviewRepository.save(review);
        productRepository.updateStatistics(product.getId(), reviewPostRequestDto.getScore());

        if (img != null && !img.isEmpty()) {
            uploadImageAndUpdateReview(img, review);
        }

    }

    @Async
    public void uploadImageAndUpdateReview(MultipartFile img, Review review) {
        try {
            String imgUrl = s3Service.uploadFile(img);
            Review updatedReview = review.updateImageUrl(imgUrl);
            reviewRepository.save(updatedReview);
        } catch (Exception e) {
            throw new RuntimeException("Review update failed");
        }
    }
}
