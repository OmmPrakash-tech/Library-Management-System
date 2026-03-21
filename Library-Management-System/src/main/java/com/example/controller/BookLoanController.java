package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.domain.BookLoanStatus;
import com.example.payload.dto.BookLoanDTO;
import com.example.payload.request.*;
import com.example.payload.response.ApiResponse;
import com.example.payload.response.PageResponse;
import com.example.service.BookLoanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/api/book-loans")
public class BookLoanController {

    private final BookLoanService bookLoanService;


    @PostMapping("/checkout")
public ResponseEntity<BookLoanDTO> checkoutBook(
        @Valid @RequestBody CheckoutRequest request) {

    // get user from service (JWT/session)
    Long userId = null; // will be fetched internally

    BookLoanDTO response = bookLoanService.checkoutBook(
            userId,
            request
    );

    return new ResponseEntity<>(response, HttpStatus.CREATED);
}

    // ================= CHECKOUT =================
    @PostMapping("/checkout/user/{userId}")
    public ResponseEntity<BookLoanDTO> checkoutBook(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest request) {

        BookLoanDTO response = bookLoanService.checkoutBook(userId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ================= CHECKIN =================
    @PostMapping("/checkin")
    public ResponseEntity<BookLoanDTO> checkIn(
            @Valid @RequestBody CheckinRequest request) {

        BookLoanDTO response = bookLoanService.checkInBook(request);

        return ResponseEntity.ok(response);
    }

    // ================= RENEW =================
    @PostMapping("/renew")
    public ResponseEntity<BookLoanDTO> renew(
            @Valid @RequestBody RenewalRequest request) {

        BookLoanDTO response = bookLoanService.renewCheckout(request);

        return ResponseEntity.ok(response);
    }

    // ================= USER LOANS =================
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<BookLoanDTO>> getUserLoans(
            @PathVariable Long userId,
            @RequestParam(required = false) BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<BookLoanDTO> response =
                bookLoanService.getUserBookLoans(userId, status, page, size);

        return ResponseEntity.ok(response);
    }

    // ================= SEARCH =================
    @PostMapping("/search")
    public ResponseEntity<PageResponse<BookLoanDTO>> search(
            @RequestBody BookLoanSearchRequest request) {

        PageResponse<BookLoanDTO> response =
                bookLoanService.getBookLoans(request);

        return ResponseEntity.ok(response);
    }

    // ================= OVERDUE UPDATE =================
    @PostMapping("/admin/update-overdue")
    public ResponseEntity<ApiResponse> updateOverdue() {

        int count = bookLoanService.markOverdueLoans();

        return ResponseEntity.ok(
                new ApiResponse("Updated " + count + " overdue loans", true)
        );
    }
}