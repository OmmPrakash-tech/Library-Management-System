package com.example.mapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.example.model.BookLoan;
import com.example.payload.dto.BookLoanDTO;

@Component
public class BookLoanMapper {

    public BookLoanDTO toDTO(BookLoan bookLoan) {

        if (bookLoan == null) {
            return null;
        }

        return BookLoanDTO.builder()
                .id(bookLoan.getId())

                // ================= USER =================
                .userId(bookLoan.getUser() != null ? bookLoan.getUser().getId() : null)
                .userName(bookLoan.getUser() != null ? bookLoan.getUser().getFullName() : null)
                .userEmail(bookLoan.getUser() != null ? bookLoan.getUser().getEmail() : null)

                // ================= BOOK =================
                .bookId(bookLoan.getBook() != null ? bookLoan.getBook().getId() : null)
                .bookTitle(bookLoan.getBook() != null ? bookLoan.getBook().getTitle() : null)
                .bookIsbn(bookLoan.getBook() != null ? bookLoan.getBook().getIsbn() : null)
                .bookAuthor(bookLoan.getBook() != null ? bookLoan.getBook().getAuthor() : null)
                .bookCoverImage(bookLoan.getBook() != null ? bookLoan.getBook().getCoverImageUrl() : null)

                // ================= LOAN =================
                .type(bookLoan.getType())
                .status(bookLoan.getStatus())
                .checkoutDate(bookLoan.getCheckoutDate())
                .dueDate(bookLoan.getDueDate())
                .returnDate(bookLoan.getReturnDate())
                .renewalCount(bookLoan.getRenewalCount())
                .maxRenewals(bookLoan.getMaxRenewals())
                .notes(bookLoan.getNotes())

                // ================= CALCULATED =================
                .remainingDays(calculateRemainingDays(bookLoan.getDueDate()))
                .overdue(bookLoan.isOverdue())
                .overdueDays(bookLoan.getOverdueDays())
                .active(bookLoan.isActive())
                .canRenew(bookLoan.canRenew())

                // ================= AUDIT =================
                .createdAt(bookLoan.getCreatedAt())
                .updatedAt(bookLoan.getUpdatedAt())

                .build();
    }

    // ================= HELPER METHOD =================
    private Integer calculateRemainingDays(LocalDate dueDate) {
        if (dueDate == null) return 0;

        long days = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        return (int) Math.max(days, 0);
    }
}