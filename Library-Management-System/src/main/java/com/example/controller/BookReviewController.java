package com.example.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Pageable;
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

import com.example.exception.BookException;
import com.example.mapper.BookReviewMapper;
import com.example.model.BookReview;
import com.example.model.User;
import com.example.payload.dto.BookReviewDTO;
import com.example.payload.dto.RatingSummaryDTO;
import com.example.payload.request.CreateReviewRequest;
import com.example.payload.request.UpdateReviewRequest;
import com.example.payload.response.ApiResponse;
import com.example.payload.response.PageResponse;
import com.example.repository.BookReviewRepository;
import com.example.service.BookReviewService;
import com.example.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins="https://library-management-system-3d9t.onrender.com")
@RequestMapping("/api/reviews")
public class BookReviewController {

    private final BookReviewService bookReviewService;
    private final UserService userService;
    private final BookReviewRepository reviewRepository;
    private final BookReviewMapper mapper;

    // ================= CREATE =================
@PostMapping
public ResponseEntity<BookReviewDTO> createReview(
        @Valid @RequestBody CreateReviewRequest request) {

    BookReviewDTO response = bookReviewService.createReview(request);

    return ResponseEntity
            .created(URI.create("/api/reviews/" + response.getId()))
            .body(response);
}

    // ================= UPDATE =================
@PutMapping("/{id}")
public ResponseEntity<BookReviewDTO> updateReview(
        @PathVariable Long id,
        @Valid @RequestBody UpdateReviewRequest request) {

    // log.info("Updating review with id: {}", id);

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

    if (size > 50) size = 50;

    PageResponse<BookReviewDTO> response =
            bookReviewService.getReviewsByBook(bookId, page, size);

    return ResponseEntity.ok(response);
}

    // ================= GET SINGLE REVIEW =================
@GetMapping("/{id}")
public ResponseEntity<BookReviewDTO> getReviewById(@PathVariable Long id) {

    // log.info("Fetching review with id: {}", id);

    if (id == null || id <= 0) {
        throw new BookException("Invalid review ID");
    }

    BookReviewDTO response = bookReviewService.getReviewById(id);

    return ResponseEntity.ok(response);
}

// ================= RATING SUMMARY =================
@GetMapping("/book/{bookId}/rating-summary")
public ResponseEntity<RatingSummaryDTO> getRatingSummary(@PathVariable Long bookId) {

    RatingSummaryDTO response = bookReviewService.getRatingSummary(bookId);

    return ResponseEntity.ok(response);
}

// ================= MY REVIEWS =================
@GetMapping("/my")
public ResponseEntity<PageResponse<BookReviewDTO>> getMyReviews(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    User user = userService.getCurrentUser();

    return ResponseEntity.ok(
            bookReviewService.getReviewsByUser(user.getId(), page, size)
    );
}

// ================= CHECK REVIEW =================
@GetMapping("/book/{bookId}/exists")
public ResponseEntity<Boolean> hasUserReviewed(@PathVariable Long bookId) {

    User user = userService.getCurrentUser();

    boolean exists = reviewRepository.existsByUserIdAndBookId(
            user.getId(),
            bookId
    );

    return ResponseEntity.ok(exists);
}

// ================= MY REVIEW FOR BOOK =================
@GetMapping("/book/{bookId}/my-review")
public ResponseEntity<BookReviewDTO> getMyReviewForBook(@PathVariable Long bookId) {

    User user = userService.getCurrentUser();

    BookReview review = reviewRepository
            .findByUserIdAndBookId(user.getId(), bookId);

    if (review == null) {
        throw new BookException("No review found for this book");
    }

    return ResponseEntity.ok(mapper.toDTO(review, user.getId()));
}

// ================= DELETE ALL REVIEWS OF BOOK =================
@DeleteMapping("/book/{bookId}")
public ResponseEntity<ApiResponse> deleteAllReviewsByBook(@PathVariable Long bookId) {

    reviewRepository.deleteAll(
            reviewRepository.findByBookId(bookId, Pageable.unpaged()).getContent()
    );

    return ResponseEntity.ok(
            new ApiResponse("All reviews deleted for this book", true)
    );
}

@GetMapping("/book/{bookId}/rating-breakdown")
public ResponseEntity<List<Object[]>> getRatingBreakdown(@PathVariable Long bookId) {

    return ResponseEntity.ok(
            reviewRepository.getRatingBreakdown(bookId)
    );
}

@GetMapping
public ResponseEntity<PageResponse<BookReviewDTO>> getAllReviews(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size) {

    return ResponseEntity.ok(
            bookReviewService.getAllReviews(page, size)
    );
}

}