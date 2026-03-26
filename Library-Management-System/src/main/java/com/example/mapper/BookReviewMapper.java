package com.example.mapper;

import org.springframework.stereotype.Component;

import com.example.model.BookReview;
import com.example.payload.dto.BookReviewDTO;

@Component
public class BookReviewMapper {

    public BookReviewDTO toDTO(BookReview bookReview, Long currentUserId) {

        if (bookReview == null) {
            return null;
        }

        var user = bookReview.getUser();
        var book = bookReview.getBook();

        return BookReviewDTO.builder()
                .id(bookReview.getId())

                // User info
                .userId(user != null ? user.getId() : null)
                .userName(user != null ? user.getFullName() : null)

                // Book info
                .bookId(book != null ? book.getId() : null)
                .bookTitle(book != null ? book.getTitle() : null)
                .bookAuthor(book != null ? book.getAuthor() : null)
                .bookIsbn(book != null ? book.getIsbn() : null)

                // Review
                .rating(bookReview.getRating())
                .reviewText(bookReview.getReviewText())
                .title(bookReview.getTitle())

                // Audit
                .createdAt(bookReview.getCreatedAt())
                .updatedAt(bookReview.getUpdatedAt())

                // 🔥 Frontend helper
                .isOwner(user != null && user.getId().equals(currentUserId))

                .build();
    }
}