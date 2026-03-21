package com.example.service;

import com.example.domain.BookLoanStatus;
import com.example.payload.dto.BookLoanDTO;
import com.example.payload.request.BookLoanSearchRequest;
import com.example.payload.request.CheckinRequest;
import com.example.payload.request.CheckoutRequest;
import com.example.payload.request.RenewalRequest;
import com.example.payload.response.PageResponse;

public interface BookLoanService {

    BookLoanDTO checkoutBook(Long userId, CheckoutRequest checkoutRequest);

    BookLoanDTO checkInBook(CheckinRequest checkinRequest);

    BookLoanDTO renewCheckout(RenewalRequest renewalRequest);

    PageResponse<BookLoanDTO> getUserBookLoans(Long userId, BookLoanStatus status, int page, int size);

    PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request);

    int markOverdueLoans();
}
