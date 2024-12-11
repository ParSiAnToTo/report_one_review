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

//    public void post(Long productId, ReviewPostRequestDto reviewPostRequestDto, MultipartFile img) throws IOException {
//        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product Not Found"));
//
//        String imgUrl = null;
//        try {
//            if (img != null && !img.isEmpty()) {
//                String fileName = img.getOriginalFilename();
//                String extension = fileName.substring(fileName.lastIndexOf("."));
//                String newFileName = UUID.randomUUID() + extension;
//
//                Path imgPath = Paths.get(uploadDir, fileName);
//                Files.createDirectories(imgPath.getParent());
//                Files.write(imgPath, img.getBytes());
//
//                imgUrl = imgPath.toString();
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Image Save Failed", e);
//        }
//
//        Review review = Review.builder()
//                .id(new ReviewId(productId, reviewPostRequestDto.getUserId()))
//                .score(reviewPostRequestDto.getScore())
//                .content(reviewPostRequestDto.getContent())
//                .imageUrl(imgUrl)
//                .build();
//
//        reviewRepository.save(review);
//    }


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
