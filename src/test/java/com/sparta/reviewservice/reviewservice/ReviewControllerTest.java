package com.sparta.reviewservice.reviewservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.reviewservice.controller.ReviewController;
import com.sparta.reviewservice.request.ReviewPostRequestDto;
import com.sparta.reviewservice.request.ReviewRequestDto;
import com.sparta.reviewservice.response.ReviewListDto;
import com.sparta.reviewservice.response.ReviewResponseDto;
import com.sparta.reviewservice.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    private ReviewResponseDto mockResponseDto;

    @BeforeEach
    void setUp() {
        ReviewListDto review = ReviewListDto.builder()
                .id(1L)
                .userId(1L)
                .score(4.5f)
                .content("Great product!")
                .imageUrl("http://example.com/image.jpg")
                .createdAt(LocalDateTime.now())
                .build();

        mockResponseDto = ReviewResponseDto.builder()
                .totalCount(1L)
                .score(4.5f)
                .cursor(1L)
                .reviews(List.of(review))
                .build();
    }

    @Test
    void testGetReviews() throws Exception {
        when(reviewService.getReviews(any(ReviewRequestDto.class))).thenReturn(mockResponseDto);

        String jsonResponse = objectMapper.writeValueAsString(mockResponseDto);

        mockMvc.perform(get("/products/1/reviews")
                        .param("cursor", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testPostReview() throws Exception {
        Mockito.doNothing().when(reviewService).post(Mockito.anyLong(), Mockito.any(ReviewPostRequestDto.class), Mockito.any());

        ReviewPostRequestDto requestDto = ReviewPostRequestDto.builder()
                .userId(1L)
                .score(5.0f)
                .content("Amazing product")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MockMultipartFile data = new MockMultipartFile(
                "data", "", MediaType.APPLICATION_JSON_VALUE, jsonRequest.getBytes());

        MockMultipartFile image = new MockMultipartFile(
                "image", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test-image-content".getBytes());

        mockMvc.perform(multipart("/products/1/reviews")
                        .file(data)
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    void testPostReviewValidationErrors() throws Exception {
        ReviewPostRequestDto invalidRequestDto = ReviewPostRequestDto.builder()
                .userId(null)
                .score(6.0f)
                .content("")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(invalidRequestDto);

        MockMultipartFile data = new MockMultipartFile(
                "data", "", MediaType.APPLICATION_JSON_VALUE, jsonRequest.getBytes());

        mockMvc.perform(multipart("/products/1/reviews")
                        .file(data)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Required User ID")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Score is too high")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Content cannot be blank")));
    }
}
