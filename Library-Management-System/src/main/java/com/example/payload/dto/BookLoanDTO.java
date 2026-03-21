package com.example.payload.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.domain.BookLoanStatus;
import com.example.domain.BookLoanType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLoanDTO {

    private Long id;

    // User info
    private Long userId;
    private String userName;
    private String userEmail;

    // Book info
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private String bookAuthor;
    private String bookCoverImage;

    // Loan info
    private BookLoanType type;
    private BookLoanStatus status;

    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private Long remainingDays;

    private LocalDate returnDate;

    private Integer renewalCount;
    private Integer maxRenewals;

    // Fine info
    private BigDecimal fineAmount;
    private Boolean finePaid;
    private String fineStatus;

    private String notes;

    // Computed flags
    private Boolean overdue;
    private Integer overdueDays;
    private Boolean active;
    private Boolean canRenew;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}