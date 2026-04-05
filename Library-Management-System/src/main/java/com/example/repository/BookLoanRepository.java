package com.example.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.domain.BookLoanStatus;
import com.example.model.BookLoan;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long>, JpaSpecificationExecutor<BookLoan> {

    // ================= BASIC QUERIES =================

    Page<BookLoan> findByUserId(Long userId, Pageable pageable);

    Page<BookLoan> findByBookId(Long bookId, Pageable pageable);

    List<BookLoan> findByBookId(Long bookId);

    Page<BookLoan> findByStatus(BookLoanStatus status, Pageable pageable);

    Page<BookLoan> findByStatusAndUserId(BookLoanStatus status, Long userId, Pageable pageable);

    // ================= EXISTENCE CHECKS =================

    boolean existsByUserIdAndBookIdAndStatus(
            Long userId,
            Long bookId,
            BookLoanStatus status
    );

    boolean existsByUserIdAndBookIdAndStatusIn(
            Long userId,
            Long bookId,
            Collection<BookLoanStatus> statuses
    );

    // ================= COUNT QUERIES =================

    @Query("""
        SELECT COUNT(bl)
        FROM BookLoan bl
        WHERE bl.user.id = :userId
        AND bl.status IN :activeStatuses
    """)
    long countActiveBookLoansByUser(
            @Param("userId") Long userId,
            @Param("activeStatuses") Collection<BookLoanStatus> activeStatuses
    );

    @Query("""
        SELECT COUNT(bl)
        FROM BookLoan bl
        WHERE bl.user.id = :userId
        AND bl.status = :status
    """)
    long countBookLoansByUserAndStatus(
            @Param("userId") Long userId,
            @Param("status") BookLoanStatus status
    );

    // ================= OVERDUE =================

    @Query("""
        SELECT bl
        FROM BookLoan bl
        WHERE bl.dueDate < :currentDate
        AND bl.status IN :statuses
    """)
    Page<BookLoan> findOverdueBookLoans(
            @Param("currentDate") LocalDate currentDate,
            @Param("statuses") Collection<BookLoanStatus> statuses,
            Pageable pageable
    );

    // ================= DATE RANGE =================

    @Query("""
        SELECT bl
        FROM BookLoan bl
        WHERE bl.checkoutDate BETWEEN :startDate AND :endDate
    """)
    Page<BookLoan> findByCheckoutDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    // ================= FETCH OPTIMIZATION =================

    @Query("""
        SELECT bl
        FROM BookLoan bl
        JOIN FETCH bl.user
        JOIN FETCH bl.book
        WHERE bl.id = :id
    """)
    Optional<BookLoan> findByIdWithUserAndBook(@Param("id") Long id);


    @Query("""
    SELECT bl FROM BookLoan bl
    WHERE (:status IS NULL OR bl.status = :status)
""")
Page<BookLoan> findAllLoans(
        @Param("status") BookLoanStatus status,
        Pageable pageable);


List<BookLoan> findByStatus(BookLoanStatus status);

 long countByStatus(BookLoanStatus status);

   @Query("SELECT COUNT(b) FROM BookLoan b WHERE b.dueDate < CURRENT_DATE AND b.status = com.example.domain.BookLoanStatus.CHECKED_OUT")
long countOverdueBooks();

}