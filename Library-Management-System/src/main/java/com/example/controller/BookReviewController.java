package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.payload.dto.BookReviewDTO;
import com.example.payload.request.CreateReviewRequest;
import com.example.payload.request.UpdateReviewRequest;
import com.example.payload.response.ApiResponse;
import com.example.payload.response.PageResponse;
import com.example.service.BookReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/api/reviews")
public class BookReviewController {

    private final BookReviewService bookReviewService;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<BookReviewDTO> createReview(
            @Valid @RequestBody CreateReviewRequest request) {

        BookReviewDTO response = bookReviewService.createReview(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<BookReviewDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request) {

        BookReviewDTO response = bookReviewService.updateReview(id, request);

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long id) {

        bookReviewService.deleteReview(id);

        return ResponseEntity.ok(
                new ApiResponse("Review deleted successfully", true)
        );
    }

    // ================= GET REVIEWS BY BOOK =================
    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<BookReviewDTO>> getReviewsByBook(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<BookReviewDTO> response =
                bookReviewService.getReviewsByBook(bookId, page, size);

        return ResponseEntity.ok(response);
    }

    // ================= GET SINGLE REVIEW =================
    @GetMapping("/{id}")
    public ResponseEntity<BookReviewDTO> getReviewById(@PathVariable Long id) {

        BookReviewDTO response = bookReviewService.getReviewById(id);

        return ResponseEntity.ok(response);
    }

    // ================= AVG RATING =================
    @GetMapping("/book/{bookId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long bookId) {

        Double avg = bookReviewService.getAverageRating(bookId);

        return ResponseEntity.ok(avg);
    }
}