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

        BookLoanDTO dto = new BookLoanDTO();
        dto.setId(bookLoan.getId());

        // User info
        if (bookLoan.getUser() != null) {
            dto.setUserId(bookLoan.getUser().getId());
            dto.setUserName(bookLoan.getUser().getFullName());
            dto.setUserEmail(bookLoan.getUser().getEmail());
        }

        // Book info
        if (bookLoan.getBook() != null) {
            dto.setBookId(bookLoan.getBook().getId());
            dto.setBookTitle(bookLoan.getBook().getTitle());
            dto.setBookIsbn(bookLoan.getBook().getIsbn());
            dto.setBookAuthor(bookLoan.getBook().getAuthor());
            dto.setBookCoverImage(bookLoan.getBook().getCoverImageUrl());
        }

        // Loan details
        dto.setType(bookLoan.getType());
        dto.setStatus(bookLoan.getStatus());
        dto.setCheckoutDate(bookLoan.getCheckoutDate());
        dto.setDueDate(bookLoan.getDueDate());

        // Remaining days (safe + non-negative)
        if (bookLoan.getDueDate() != null) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), bookLoan.getDueDate());
            dto.setRemainingDays(Math.max(days, 0L));
        } else {
            dto.setRemainingDays(0L);
        }

        dto.setReturnDate(bookLoan.getReturnDate());
        dto.setRenewalCount(bookLoan.getRenewalCount());
        dto.setMaxRenewals(bookLoan.getMaxRenewals());
        dto.setNotes(bookLoan.getNotes());

        // Computed fields
        dto.setOverdue(bookLoan.isOverdue()); // ✅ FIXED
        dto.setOverdueDays(bookLoan.getOverdueDays());
        dto.setActive(bookLoan.isActive());
        dto.setCanRenew(bookLoan.canRenew());

        dto.setCreatedAt(bookLoan.getCreatedAt());
        dto.setUpdatedAt(bookLoan.getUpdatedAt());

        return dto;
    }
}