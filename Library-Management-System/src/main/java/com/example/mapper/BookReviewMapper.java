package com.example.mapper;

import org.springframework.stereotype.Component;

import com.example.model.BookReview;
import com.example.payload.dto.BookReviewDTO;

@Component
public class BookReviewMapper {

    public BookReviewDTO toDTO(BookReview bookReview) {

        if (bookReview == null) {
            return null;
        }

        return BookReviewDTO.builder()
                .id(bookReview.getId())

                // User info
                .userId(bookReview.getUser() != null ? bookReview.getUser().getId() : null)
                .userName(bookReview.getUser() != null ? bookReview.getUser().getFullName() : null)
                .userEmail(bookReview.getUser() != null ? bookReview.getUser().getEmail() : null)

                // Book info
                .bookId(bookReview.getBook() != null ? bookReview.getBook().getId() : null)
                .bookTitle(bookReview.getBook() != null ? bookReview.getBook().getTitle() : null)
                .bookAuthor(bookReview.getBook() != null ? bookReview.getBook().getAuthor() : null)
                .bookIsbn(bookReview.getBook() != null ? bookReview.getBook().getIsbn() : null)

                // Review details
                .rating(bookReview.getRating())
                .reviewText(bookReview.getReviewText())
                .title(bookReview.getTitle())

                // Audit
                .createdAt(bookReview.getCreatedAt())
                .updatedAt(bookReview.getUpdatedAt())

                .build();
    }
}