package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.BookLoanStatus;
import com.example.model.User;
import com.example.payload.dto.BookLoanDTO;
import com.example.payload.request.BookLoanSearchRequest;
import com.example.payload.request.CheckinRequest;
import com.example.payload.request.CheckoutRequest;
import com.example.payload.request.RenewalRequest;
import com.example.payload.response.ApiResponse;
import com.example.payload.response.PageResponse;
import com.example.repository.UserRepository;
import com.example.service.BookLoanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/book-loans")
public class BookLoanController {

    private final BookLoanService bookLoanService;
    private final UserRepository userRepository;

    // ================= CHECKOUT (USER) =================
    @PostMapping("/checkout")
    public ResponseEntity<BookLoanDTO> checkoutBook(
            @Valid @RequestBody CheckoutRequest request) {

        Long userId = getCurrentUserId(); // 🔥 from security

        BookLoanDTO response = bookLoanService.checkoutBook(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================= CHECKOUT (ADMIN) =================
    @PostMapping("/admin/checkout/{userId}")
    public ResponseEntity<BookLoanDTO> checkoutForUser(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest request) {

        BookLoanDTO response = bookLoanService.checkoutBook(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================= CHECKIN =================
    @PostMapping("/checkin")
    public ResponseEntity<BookLoanDTO> checkIn(
            @Valid @RequestBody CheckinRequest request) {

        return ResponseEntity.ok(
                bookLoanService.checkInBook(request)
        );
    }

    // ================= RENEW =================
    @PostMapping("/renew")
    public ResponseEntity<BookLoanDTO> renew(
            @Valid @RequestBody RenewalRequest request) {

        return ResponseEntity.ok(
                bookLoanService.renewLoan(request)
        );
    }

    // ================= CURRENT USER LOANS =================
    @GetMapping("/my")
    public ResponseEntity<PageResponse<BookLoanDTO>> getMyLoans(
            @RequestParam(required = false) BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long userId = getCurrentUserId();

        return ResponseEntity.ok(
                bookLoanService.getUserLoans(userId, status, page, size)
        );
    }

    // ================= USER LOANS (ADMIN) =================
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<BookLoanDTO>> getUserLoans(
            @PathVariable Long userId,
            @RequestParam(required = false) BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                bookLoanService.getUserLoans(userId, status, page, size)
        );
    }

    // ================= SEARCH =================
    @PostMapping("/search")
    public ResponseEntity<PageResponse<BookLoanDTO>> search(
            @RequestBody BookLoanSearchRequest request) {

        return ResponseEntity.ok(
                bookLoanService.searchLoans(request)
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{loanId}")
    public ResponseEntity<BookLoanDTO> getLoanById(
            @PathVariable Long loanId) {

        return ResponseEntity.ok(
                bookLoanService.getLoanById(loanId)
        );
    }

    // ================= OVERDUE UPDATE =================
    @PostMapping("/admin/update-overdue")
    public ResponseEntity<ApiResponse> updateOverdue() {

        int count = bookLoanService.markOverdueLoans();

        return ResponseEntity.ok(
                new ApiResponse("Updated " + count + " overdue loans", true)
        );
    }

    // ================= HELPER =================
 private Long getCurrentUserId() {

    Authentication authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        throw new RuntimeException("User not authenticated");
    }

    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return user.getId();
}
}