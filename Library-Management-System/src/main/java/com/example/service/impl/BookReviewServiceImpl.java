package com.example.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.domain.BookLoanStatus;
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

    private final BookReviewRepository bookReviewRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final BookReviewMapper bookReviewMapper;
    private final BookLoanRepository bookLoanRepository;

    @Override
    public BookReviewDTO createReview(CreateReviewRequest request) {

        User user = userService.getCurrentUser();

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (bookReviewRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new RuntimeException("You have already reviewed this book");
        }

        boolean hasReadBook =
                bookLoanRepository.existsByUserIdAndBookIdAndStatus(
                        user.getId(),
                        book.getId(),
                        BookLoanStatus.RETURNED
                );

        if (!hasReadBook) {
            throw new RuntimeException("You must read the book before reviewing");
        }

        BookReview review = new BookReview();
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        review.setTitle(request.getTitle());

        BookReview saved = bookReviewRepository.save(review);

        return bookReviewMapper.toDTO(saved);
    }

    @Override
    public BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request) {

        User user = userService.getCurrentUser();

        BookReview review = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own review");
        }

        review.setReviewText(request.getReviewText());
        review.setTitle(request.getTitle());
        review.setRating(request.getRating());

        BookReview saved = bookReviewRepository.save(review);

        return bookReviewMapper.toDTO(saved);
    }

    @Override
    public void deleteReview(Long reviewId) {

        User user = userService.getCurrentUser();

        BookReview review = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        bookReviewRepository.delete(review);
    }

    @Override
    public PageResponse<BookReviewDTO> getReviewsByBookId(Long id, int page, int size) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found by id"));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<BookReview> reviews = bookReviewRepository.findByBook(book, pageable);

        return convertToPageResponse(reviews);
    }

    private PageResponse<BookReviewDTO> convertToPageResponse(Page<BookReview> reviewPage) {

        List<BookReviewDTO> reviewDTOs =
                reviewPage.getContent()
                        .stream()
                        .map(bookReviewMapper::toDTO)
                        .toList();

        return new PageResponse<>(
                reviewDTOs,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast(),
                reviewPage.isFirst(),
                reviewPage.isEmpty()
        );
    }
}