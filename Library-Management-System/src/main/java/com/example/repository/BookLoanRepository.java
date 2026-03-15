package com.example.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.domain.BookLoanStatus;
import com.example.model.BookLoan;
import com.example.model.User;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {

    Page<BookLoan> findByUserId(Long userId, Pageable pageable);

    Page<BookLoan> findByStatusAndUser(BookLoanStatus status, User user, Pageable pageable);

    Page<BookLoan> findByStatus(BookLoanStatus status, Pageable pageable);

    Page<BookLoan> findByBookId(Long bookId, Pageable pageable);

    List<BookLoan> findByBookId(Long bookId);

    @Query("SELECT CASE WHEN COUNT(bl) > 0 THEN true ELSE false END FROM BookLoan bl " +
           "WHERE bl.user.id = :userId AND bl.book.id = :bookId " +
           "AND (bl.status = 'CHECKED_OUT' OR bl.status = 'OVERDUE')")
    boolean hasActiveCheckout(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId
    );

    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.user.id = :userId " +
           "AND (bl.status = 'CHECKED_OUT' OR bl.status = 'OVERDUE')")
    long countActiveBookLoansByUser(@Param("userId") Long userId);

    // ❗ FIXED (your query was wrong)
    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.user.id = :userId " +
           "AND bl.status = 'OVERDUE'")
    long countOverdueActiveLoansByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.user.id = :userId " +
           "AND bl.status = 'OVERDUE'")
    long countOverdueBookLoansByUser(@Param("userId") Long userId);

    @Query("SELECT bl FROM BookLoan bl WHERE bl.dueDate < :currentDate " +
           "AND (bl.status = 'CHECKED_OUT' OR bl.status = 'OVERDUE')")
    Page<BookLoan> findOverdueBookLoans(
            @Param("currentDate") LocalDate currentDate,
            Pageable pageable
    );

    @Query("SELECT bl FROM BookLoan bl WHERE bl.checkoutDate BETWEEN :startDate AND :endDate")
    Page<BookLoan> findBookLoansByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    boolean existsByUserIdAndBookIdAndStatus(
            Long userId,
            Long bookId,
            BookLoanStatus status
    );
}