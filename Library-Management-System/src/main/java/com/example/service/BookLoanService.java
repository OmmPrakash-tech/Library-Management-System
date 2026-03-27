package com.example.service;

import com.example.domain.BookLoanStatus;
import com.example.payload.dto.BookLoanDTO;
import com.example.payload.request.BookLoanSearchRequest;
import com.example.payload.request.CheckinRequest;
import com.example.payload.request.CheckoutRequest;
import com.example.payload.request.RenewalRequest;
import com.example.payload.response.PageResponse;

public interface BookLoanService {

    // ================= CORE OPERATIONS =================

    BookLoanDTO checkoutBook(Long userId, CheckoutRequest request);

    BookLoanDTO checkInBook(CheckinRequest request);

    BookLoanDTO renewLoan(RenewalRequest request);

    BookLoanDTO getLoanById(Long loanId);

    // ================= FETCH =================

    PageResponse<BookLoanDTO> getUserLoans(
            Long userId,
            BookLoanStatus status,
            int page,
            int size
    );

    PageResponse<BookLoanDTO> searchLoans(BookLoanSearchRequest request);

    // ================= SYSTEM TASKS =================

    int markOverdueLoans();
}