package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.BookReview;

public interface BookReviewRepository extends JpaRepository<BookReview, Long> {

    Page<BookReview> findByBookId(Long bookId, Pageable pageable);

    Page<BookReview> findByBookIdOrderByCreatedAtDesc(Long bookId, Pageable pageable);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    long countByBookId(Long bookId);

    @Query("SELECT AVG(br.rating) FROM BookReview br WHERE br.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);
}