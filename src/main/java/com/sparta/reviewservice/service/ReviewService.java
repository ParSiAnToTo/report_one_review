package com.sparta.reviewservice.service;

import com.sparta.reviewservice.entity.Product;
import com.sparta.reviewservice.entity.Review;
import com.sparta.reviewservice.repository.ProductRepository;
import com.sparta.reviewservice.repository.ReviewRepository;
import com.sparta.reviewservice.request.ReviewPostRequestDto;
import com.sparta.reviewservice.request.ReviewRequestDto;
import com.sparta.reviewservice.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public ReviewResponseDto getReviews(ReviewRequestDto reviewRequestDto) {
        Product product = productRepository.findById(reviewRequestDto.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product Not Found"));

        Pageable pageable = PageRequest.of(reviewRequestDto.getCursor(), reviewRequestDto.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviews = reviewRepository.findByProductId(product.getId(), pageable);

        return ReviewResponseDto.builder()
                .totalCount(reviews.getTotalElements())
                .score(product.getScore())
                .cursor(reviews.getNumber() + 1)
                .reviews(reviews.getContent())
                .build();
    }

    public void post(Long productId, ReviewPostRequestDto reviewPostRequestDto, MultipartFile img) throws IOException {

        String imgUrl = null;
        try {
            if (img != null && !img.isEmpty()) {
                String fileName = img.getOriginalFilename();
                String extension = fileName.substring(fileName.lastIndexOf("."));
                if (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png")) {
                    throw new IllegalArgumentException("Invalid File Format");
                }
                String newFileName = UUID.randomUUID() + extension;

                Path imgPath = Paths.get(uploadDir, newFileName);
                Files.createDirectories(imgPath.getParent());
                Files.write(imgPath, img.getBytes());

                imgUrl = imgPath.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("Image Save Failed", e);
        }

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product Not Found"));

        Review review = Review.builder()
                .productId(productId)
                .userId(reviewPostRequestDto.getUserId())
                .score(reviewPostRequestDto.getScore())
                .content(reviewPostRequestDto.getContent())
                .imageUrl(imgUrl)
                .build();

        reviewRepository.save(review);

        Long newcount = product.getReviewCount() + 1;
        float newScore = (product.getScore() * product.getReviewCount() + reviewPostRequestDto.getScore()) / newcount;

        Product updatedProduct = Product.builder()
                .id(productId)
                .reviewCount(newcount)
                .score(newScore)
                .build();

        productRepository.save(updatedProduct);
    }


    public String postImgTest(MultipartFile img) throws IOException {
        try {
            String imgUrl = null;

            String fileName = img.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID() + extension;

            Path imgPath = Paths.get(uploadDir, fileName);
            Files.createDirectories(imgPath.getParent());
            Files.write(imgPath, img.getBytes());

            imgUrl = imgPath.toString();
            return imgUrl;

        } catch (Exception e) {
            throw new RuntimeException("Image Save Test Failed", e);
        }
    }


}
