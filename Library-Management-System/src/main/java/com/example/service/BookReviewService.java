package com.example.service;

import com.example.payload.dto.BookReviewDTO;
import com.example.payload.dto.RatingSummaryDTO;
import com.example.payload.request.CreateReviewRequest;
import com.example.payload.request.UpdateReviewRequest;
import com.example.payload.response.PageResponse;

public interface BookReviewService {

    BookReviewDTO createReview(CreateReviewRequest request);

    BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long reviewId);

    PageResponse<BookReviewDTO> getReviewsByBook(Long bookId, int page, int size);

    PageResponse<BookReviewDTO> getReviewsByUser(Long userId, int page, int size);

RatingSummaryDTO getRatingSummary(Long bookId);

    BookReviewDTO getReviewById(Long reviewId);
}