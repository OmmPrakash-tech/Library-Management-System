package com.example.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.BookLoanStatus;
import com.example.domain.BookLoanType;
import com.example.exception.BookException;
import com.example.mapper.BookLoanMapper;
import com.example.model.Book;
import com.example.model.BookLoan;
import com.example.model.User;
import com.example.payload.dto.BookLoanDTO;
import com.example.payload.dto.SubscriptionDTO;
import com.example.payload.request.*;
import com.example.payload.response.PageResponse;
import com.example.repository.BookLoanRepository;
import com.example.repository.BookRepository;
import com.example.service.BookLoanService;
import com.example.service.SubscriptionService;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookLoanServiceImpl implements BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final SubscriptionService subscriptionService;
    private final BookLoanMapper mapper;

    // ================= CHECKOUT =================
    @Override
    @Transactional
    public BookLoanDTO checkoutBook(Long userId, CheckoutRequest request) {

        User user = userService.findById(userId);

        SubscriptionDTO subscription = subscriptionService.getUsersActiveSubscription();

        if (request.getCheckoutDays() <= 0) {
            throw new BookException("Invalid checkout days");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookException("Book not found"));

        if (!book.getActive()) throw new BookException("Book inactive");
        if (book.getAvailableCopies() <= 0) throw new BookException("Not available");

        boolean exists = bookLoanRepository.existsByUserIdAndBookIdAndStatusIn(
                userId,
                book.getId(),
                List.of(BookLoanStatus.CHECKED_OUT, BookLoanStatus.OVERDUE)
        );

        if (exists) throw new BookException("Already borrowed");

        long active = bookLoanRepository.countActiveBookLoansByUser(userId);
        if (active >= subscription.getMaxBooksAllowed())
            throw new BookException("Limit reached");

        long overdue = bookLoanRepository.countOverdueBookLoansByUser(userId);
        if (overdue > 0)
            throw new BookException("Return overdue books first");

        BookLoan loan = BookLoan.builder()
                .user(user)
                .book(book)
                .type(BookLoanType.CHECKOUT)
                .status(BookLoanStatus.CHECKED_OUT)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(request.getCheckoutDays()))
                .renewalCount(0)
                .maxRenewals(2)
                .notes(request.getNotes())
                .build();

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return mapper.toDTO(bookLoanRepository.save(loan));
    }

    // ================= CHECKIN =================
    @Override
    @Transactional
    public BookLoanDTO checkInBook(CheckinRequest request) {

        BookLoan loan = bookLoanRepository.findById(request.getBookLoanId())
                .orElseThrow(() -> new BookException("Loan not found"));

        if (!loan.isActive()) throw new BookException("Not active");

        loan.setReturnDate(LocalDate.now());

        BookLoanStatus status = request.getCondition() != null
                ? request.getCondition()
                : BookLoanStatus.RETURNED;

        loan.setStatus(status);
        loan.setNotes("Returned");

        if (status != BookLoanStatus.LOST) {
            Book book = loan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }

        return mapper.toDTO(bookLoanRepository.save(loan));
    }

    // ================= RENEW =================
    @Override
    @Transactional
    public BookLoanDTO renewCheckout(RenewalRequest request) {

        BookLoan loan = bookLoanRepository.findById(request.getBookLoanId())
                .orElseThrow(() -> new BookException("Loan not found"));

        if (!loan.canRenew()) throw new BookException("Cannot renew");

        loan.setDueDate(loan.getDueDate().plusDays(request.getExtensionDays()));
        loan.setRenewalCount(loan.getRenewalCount() + 1);
        loan.setNotes("Renewed");

        return mapper.toDTO(bookLoanRepository.save(loan));
    }

    // ================= USER LOANS =================
    @Override
    public PageResponse<BookLoanDTO> getUserBookLoans(Long userId, BookLoanStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<BookLoan> result = (status != null)
                ? bookLoanRepository.findByStatusAndUser(status, userService.findById(userId), pageable)
                : bookLoanRepository.findByUserId(userId, pageable);

        return convert(result);
    }

    // ================= SEARCH =================
    @Override
    public PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest req) {

        Pageable pageable = createPageable(req);

        Page<BookLoan> page;

        if (Boolean.TRUE.equals(req.getOverdueOnly())) {
            page = bookLoanRepository.findOverdueBookLoans(LocalDate.now(), pageable);
        } else if (req.getUserId() != null) {
            page = bookLoanRepository.findByUserId(req.getUserId(), pageable);
        } else if (req.getBookId() != null) {
            page = bookLoanRepository.findByBookId(req.getBookId(), pageable);
        } else if (req.getStatus() != null) {
            page = bookLoanRepository.findByStatus(req.getStatus(), pageable);
        } else {
            page = bookLoanRepository.findAll(pageable);
        }

        return convert(page);
    }

    // ================= OVERDUE =================
    @Override
    @Transactional
    public int markOverdueLoans() {

        int updated = 0;
        int page = 0;

        Page<BookLoan> result;

        do {
            Pageable pageable = PageRequest.of(page, 500);
            result = bookLoanRepository.findOverdueBookLoans(LocalDate.now(), pageable);

            for (BookLoan loan : result.getContent()) {

                if (loan.getStatus() == BookLoanStatus.CHECKED_OUT) {
                    loan.setStatus(BookLoanStatus.OVERDUE);
                }

                // computed, not stored
                int overdueDays = calculateOverdueDate(loan.getDueDate(), LocalDate.now());

                updated++;
            }

            page++;

        } while (!result.isLast());

        return updated;
    }

    // ================= HELPERS =================
    private Pageable createPageable(BookLoanSearchRequest req) {

        Sort sort = req.getSortDirection().equalsIgnoreCase("ASC")
                ? Sort.by(req.getSortBy()).ascending()
                : Sort.by(req.getSortBy()).descending();

        return PageRequest.of(req.getPage(), Math.min(req.getSize(), 100), sort);
    }

    private PageResponse<BookLoanDTO> convert(Page<BookLoan> page) {

        return new PageResponse<>(
                page.getContent().stream().map(mapper::toDTO).collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst(),
                page.isEmpty()
        );
    }

    private int calculateOverdueDate(LocalDate due, LocalDate today) {
        if (due == null || !today.isAfter(due)) return 0;
        return (int) ChronoUnit.DAYS.between(due, today);
    }
}