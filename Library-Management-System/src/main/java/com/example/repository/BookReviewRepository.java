package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.BookReview;

public interface BookReviewRepository extends JpaRepository<BookReview, Long> {

    Page<BookReview> findByBookId(Long bookId, Pageable pageable);

    Page<BookReview> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    long countByBookId(Long bookId);

    BookReview findByUserIdAndBookId(Long userId, Long bookId);

    @Query("SELECT COALESCE(AVG(br.rating), 0) FROM BookReview br WHERE br.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);

    @Query("""
    SELECT br.rating, COUNT(br)
    FROM BookReview br
    WHERE br.book.id = :bookId
    GROUP BY br.rating
""")
List<Object[]> getRatingBreakdown(@Param("bookId") Long bookId);
}