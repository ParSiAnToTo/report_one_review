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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public ReviewResponseDto getReviews(ReviewRequestDto reviewRequestDto) {
        Product product = productRepository.findById(reviewRequestDto.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product Not Found"));

        Pageable pageable = PageRequest.of(Math.max(0, reviewRequestDto.getCursor() - 1),
                reviewRequestDto.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Review> reviewPage = reviewRepository.findByProductId(product.getId(), pageable);

        List<ReviewListDto> reviews = reviewPage.stream().map(review -> ReviewListDto.builder()
                        .id(review.getId())
                        .userId(review.getUserId())
                        .score(review.getScore())
                        .content(review.getContent())
                        .imageUrl(review.getImageUrl())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ReviewResponseDto.builder()
                .totalCount(reviewPage.getTotalElements())
                .score(product.getScore())
                .cursor(reviewPage.getNumber() + 1)
                .reviews(reviews)
                .build();
    }

    @Transactional
    public void post(Long productId, ReviewPostRequestDto reviewPostRequestDto, MultipartFile img) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product Not Found"));

        if (reviewPostRequestDto.getUserId() == null || reviewPostRequestDto.getContent() == null ||
                reviewPostRequestDto.getScore() < 0 || reviewPostRequestDto.getScore() > 5) {
            throw new IllegalArgumentException("Invalid Product or Review Post Request");
        }

        String imgUrl = null;
        if (img != null && !img.isEmpty()) {
            imgUrl = s3Service.uploadFile(img);
        }

        Review review = Review.builder()
                .product(product)
                .userId(reviewPostRequestDto.getUserId())
                .score(reviewPostRequestDto.getScore())
                .content(reviewPostRequestDto.getContent())
                .imageUrl(imgUrl)
                .build();

        reviewRepository.save(review);

        long newcount = product.getReviewCount() + 1;
        float newScore = Math.round((product.getScore() * product.getReviewCount() + reviewPostRequestDto.getScore()) / newcount);

        Product updatedProduct = Product.builder()
                .id(productId)
                .reviewCount(newcount)
                .score(newScore)
                .build();

        productRepository.save(updatedProduct);
    }
}
