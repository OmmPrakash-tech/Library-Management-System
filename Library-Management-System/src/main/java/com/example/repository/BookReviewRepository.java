package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Book;
import com.example.model.BookReview;

public interface BookReviewRepository extends JpaRepository<BookReview, Long> {

    Page<BookReview> findByBook(Book book, Pageable pageable);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}
