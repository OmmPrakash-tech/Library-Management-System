package com.example.service.impl;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.BookLoanStatus;
import com.example.exception.BookException;
import com.example.mapper.BookReviewMapper;
import com.example.model.Book;
import com.example.model.BookReview;
import com.example.model.User;
import com.example.payload.dto.BookReviewDTO;
import com.example.payload.request.CreateReviewRequest;
import com.example.payload.request.UpdateReviewRequest;
import com.example.payload.response.PageResponse;
import com.example.repository.BookLoanRepository;
import com.example.repository.BookRepository;
import com.example.repository.BookReviewRepository;
import com.example.service.BookReviewService;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {

    private final BookReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final BookReviewMapper mapper;
    private final BookLoanRepository bookLoanRepository;

    // ================= CREATE =================
    @Override
    @Transactional
    public BookReviewDTO createReview(CreateReviewRequest request) {

        User user = userService.getCurrentUser();

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookException("Book not found"));

        // Prevent duplicate
        if (reviewRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new BookException("Already reviewed");
        }

        // Must have read book
boolean hasRead = bookLoanRepository.existsByUserIdAndBookIdAndStatusIn(
        user.getId(),
        book.getId(),
        List.of(BookLoanStatus.RETURNED)
);

        if (!hasRead) {
            throw new BookException("Read book before reviewing");
        }

        BookReview review = new BookReview();
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText().trim());
        review.setTitle(request.getTitle());

        return mapper.toDTO(reviewRepository.save(review));
    }

    // ================= UPDATE =================
    @Override
    @Transactional
    public BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request) {

        User user = userService.getCurrentUser();

        BookReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BookException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new BookException("Not your review");
        }

        review.setReviewText(request.getReviewText().trim());
        review.setTitle(request.getTitle());
        review.setRating(request.getRating());

        return mapper.toDTO(reviewRepository.save(review));
    }

    // ================= DELETE =================
    @Override
    @Transactional
    public void deleteReview(Long reviewId) {

        User user = userService.getCurrentUser();

        BookReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BookException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new BookException("Not your review");
        }

        reviewRepository.delete(review);
    }

    // ================= GET REVIEWS =================
    @Override
    public PageResponse<BookReviewDTO> getReviewsByBook(Long bookId, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<BookReview> reviews =
                reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId, pageable);

        return convert(reviews);
    }

    // ================= HELPER =================
    private PageResponse<BookReviewDTO> convert(Page<BookReview> page) {

        List<BookReviewDTO> dtoList = page.getContent()
                .stream()
                .map(mapper::toDTO)
                .toList();

        return new PageResponse<>(
                dtoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst(),
                page.isEmpty()
        );
    }

    @Override
    public Double getAverageRating(Long bookId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAverageRating'");
    }

    @Override
    public BookReviewDTO getReviewById(Long reviewId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getReviewById'");
    }
}