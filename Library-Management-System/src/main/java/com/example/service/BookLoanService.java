package com.example.service;

import com.example.domain.BookLoanStatus;
import com.example.payload.dto.BookLoanDTO;
import com.example.payload.request.BookLoanSearchRequest;
import com.example.payload.request.CheckinRequest;
import com.example.payload.request.CheckoutRequest;
import com.example.payload.request.RenewalRequest;
import com.example.payload.response.PageResponse;

public interface BookLoanService {

    BookLoanDTO checkoutBook(CheckoutRequest checkoutRequest);

BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest);

BookLoanDTO checkinBook(CheckinRequest checkinRequest);

BookLoanDTO renewCheckout(RenewalRequest renewalRequest);

PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size);

PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request);

int updateOverdueBookLoan();

}
