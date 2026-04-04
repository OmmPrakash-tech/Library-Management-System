package com.example.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.BookLoanStatus;
import com.example.exception.BookException;
import com.example.mapper.BookReviewMapper;
import com.example.model.Book;
import com.example.model.BookReview;
import com.example.model.User;
import com.example.payload.dto.BookReviewDTO;
import com.example.payload.dto.RatingSummaryDTO;
import com.example.payload.request.CreateReviewRequest;
import com.example.payload.request.UpdateReviewRequest;
import com.example.payload.response.PageResponse;
import com.example.repository.BookLoanRepository;
import com.example.repository.BookRepository;
import com.example.repository.BookReviewRepository;
import com.example.repository.UserRepository;
import com.example.service.BookReviewService;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {

    private final BookReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

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

    BookReview review = BookReview.builder()
            .user(user)
            .book(book)
            .rating(request.getRating())
            .reviewText(request.getReviewText().trim())
            .title(request.getTitle() != null ? request.getTitle().trim() : null)
            .build();

    BookReview saved = reviewRepository.save(review);

    return mapper.toDTO(saved, user.getId());
}


    // ================= UPDATE =================
@Override
@Transactional
public BookReviewDTO updateReview(Long reviewId, UpdateReviewRequest request) {

    User user = userService.getCurrentUser();

    BookReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BookException("Review not found"));

    // 🔐 Ownership check
    if (!review.getUser().getId().equals(user.getId())) {
        throw new BookException("Not your review");
    }

    // ✅ Update fields safely
    review.setReviewText(
            request.getReviewText() != null ? request.getReviewText().trim() : review.getReviewText()
    );

    review.setTitle(
            request.getTitle() != null ? request.getTitle().trim() : null
    );

    review.setRating(request.getRating());

    // ✅ No need to call save() (JPA handles it)
    return mapper.toDTO(review, user.getId());
}


    // ================= DELETE =================
    @Override
    @Transactional
    public void deleteReview(Long reviewId) {

      

        BookReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BookException("Review not found"));

       

        reviewRepository.delete(review);
    }

    // ================= GET REVIEWS =================
@Override
public PageResponse<BookReviewDTO> getReviewsByBook(Long bookId, int page, int size) {

    if (!bookRepository.existsById(bookId)) {
        throw new BookException("Book not found");
    }

    Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("createdAt").descending()
    );

    Page<BookReview> reviews =
            reviewRepository.findByBookId(bookId, pageable);

    return convert(reviews);
}

    // ================= HELPER =================


   private PageResponse<BookReviewDTO> convert(Page<BookReview> page) {

    Long currentUserId = userService.getCurrentUser().getId();

    List<BookReviewDTO> dtoList = page.getContent()
            .stream()
            .map(review -> mapper.toDTO(review, currentUserId))
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
public RatingSummaryDTO getRatingSummary(Long bookId) {

    if (!bookRepository.existsById(bookId)) {
        throw new BookException("Book not found");
    }

    Double avg = reviewRepository.findAverageRatingByBookId(bookId);
    Long count = reviewRepository.countByBookId(bookId);

    return RatingSummaryDTO.builder()
            .averageRating(avg)
            .totalReviews(count)
            .build();
}

 @Override
public BookReviewDTO getReviewById(Long reviewId) {

    User user = userService.getCurrentUser(); // for isOwner

    BookReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BookException("Review not found"));

    return mapper.toDTO(review, user.getId());
}

@Override
public PageResponse<BookReviewDTO> getReviewsByUser(Long userId, int page, int size) {

    // Optional: validate user exists
    if (!userRepository.existsById(userId)) {
        throw new BookException("User not found");
    }

    Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("createdAt").descending()
    );

    Page<BookReview> reviews =
            reviewRepository.findByUserId(userId, pageable);

    return convert(reviews);
}


@Override
public PageResponse<BookReviewDTO> getAllReviews(int page, int size) {

    // 🔒 safety check
    if (size > 50) size = 50;

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    Page<BookReview> reviewPage = reviewRepository.findAll(pageable);

    List<BookReviewDTO> dtoList = reviewPage.getContent()
            .stream()
            .map(review -> mapper.toDTO(review, review.getUser().getId()))
            .toList();

    return PageResponse.<BookReviewDTO>builder()
            .content(dtoList)
            .pageNumber(reviewPage.getNumber())
            .pageSize(reviewPage.getSize())
            .totalElements(reviewPage.getTotalElements())
            .totalPages(reviewPage.getTotalPages())
            .last(reviewPage.isLast())
            .first(reviewPage.isFirst())
            .build();
}


}