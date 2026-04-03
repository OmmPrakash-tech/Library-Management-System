package com.example.payload.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.domain.BookLoanStatus;
import com.example.domain.BookLoanType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookLoanDTO {

    private Long id;

    // ================= USER INFO =================
    private Long userId;
    private String userName;
    private String userEmail;

    // ================= BOOK INFO =================
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private String bookAuthor;
    private String bookCoverImage;

    // ================= LOAN INFO =================
    private BookLoanType type;
    private BookLoanStatus status;

    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private Integer remainingDays;

    private LocalDate returnDate;

    private Integer renewalCount;
    private Integer maxRenewals;

    // ================= FINE INFO =================
    private BigDecimal fineAmount;
    private boolean finePaid;

    // ================= FLAGS =================
    private boolean overdue;
    private Integer overdueDays;
    private boolean active;
    private boolean canRenew;

    // ================= EXTRA =================
    private String notes;

    // ================= AUDIT =================
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
}
