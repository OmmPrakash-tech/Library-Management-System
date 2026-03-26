package com.example.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    List<BookLoan> findByBookId(Long bookId);

    Page<BookLoan> findByStatusAndUser(BookLoanStatus status, User user, Pageable pageable);

    Page<BookLoan> findByStatus(BookLoanStatus status, Pageable pageable);

    Page<BookLoan> findByBookId(Long bookId, Pageable pageable);

    boolean existsByUserIdAndBookIdAndStatusIn(
            Long userId,
            Long bookId,
            List<BookLoanStatus> statuses
    );

    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.user.id = :userId " +
           "AND bl.status IN ('CHECKED_OUT', 'OVERDUE')")
    long countActiveBookLoansByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.user.id = :userId " +
           "AND bl.status = 'OVERDUE'")
    long countOverdueBookLoansByUser(@Param("userId") Long userId);

    @Query("SELECT bl FROM BookLoan bl WHERE bl.dueDate < :currentDate " +
           "AND bl.status IN ('CHECKED_OUT', 'OVERDUE')")
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

    @Query("SELECT bl FROM BookLoan bl " +
           "JOIN FETCH bl.user JOIN FETCH bl.book " +
           "WHERE bl.id = :id")
    Optional<BookLoan> findByIdWithUserAndBook(@Param("id") Long id);

        boolean existsByUserIdAndBookIdAndStatus(
        Long userId,
        Long bookId,
        BookLoanStatus status
);
}