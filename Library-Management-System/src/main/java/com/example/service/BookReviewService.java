package com.example.service;

import com.example.payload.dto.BookReviewDTO;
import com.example.payload.request.CreateReviewRequest;
import com.example.payload.request.UpdateReviewRequest;
import com.example.payload.response.PageResponse;

public interface BookReviewService {

    // Create a new review
    BookReviewDTO createReview(CreateReviewRequest request);

    // Update an existing review
    BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request);

    // Delete a review
    void deleteReview(Long reviewId);

    // Get paginated reviews for a book
    PageResponse<BookReviewDTO> getReviewsByBookId(Long bookId, int page, int size);

}