package com.example.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import com.example.payload.request.BookLoanSearchRequest;
import com.example.payload.request.CheckinRequest;
import com.example.payload.request.CheckoutRequest;
import com.example.payload.request.RenewalRequest;
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

    // ================= FETCH USER =================
    User user = userService.findById(userId);

    // ================= VALIDATE REQUEST =================
    if (request.getCheckoutDays() == null || request.getCheckoutDays() <= 0) {
        throw new BookException("Invalid checkout duration");
    }

    // ================= FETCH BOOK =================
    Book book = bookRepository.findById(request.getBookId())
            .orElseThrow(() -> new BookException("Book not found"));

    if (!Boolean.TRUE.equals(book.getActive())) {
        throw new BookException("Book is inactive");
    }

    if (book.getAvailableCopies() <= 0) {
        throw new BookException("Book is not available");
    }

    // ================= SUBSCRIPTION =================
    SubscriptionDTO subscription = subscriptionService.getUsersActiveSubscription();

    // ================= BUSINESS VALIDATIONS =================

    // Prevent duplicate active loan
    boolean alreadyBorrowed = bookLoanRepository.existsByUserIdAndBookIdAndStatusIn(
            userId,
            book.getId(),
            List.of(BookLoanStatus.CHECKED_OUT, BookLoanStatus.OVERDUE)
    );

    if (alreadyBorrowed) {
        throw new BookException("You have already borrowed this book");
    }

    // Check borrowing limit
    long activeLoans = bookLoanRepository.countActiveBookLoansByUser(
            userId,
            List.of(BookLoanStatus.CHECKED_OUT, BookLoanStatus.OVERDUE)
    );

    if (activeLoans >= subscription.getMaxBooksAllowed()) {
        throw new BookException("Borrowing limit reached");
    }

    // Check overdue loans
    long overdueLoans = bookLoanRepository.countBookLoansByUserAndStatus(
            userId,
            BookLoanStatus.OVERDUE
    );

    if (overdueLoans > 0) {
        throw new BookException("Please return overdue books first");
    }

    // ================= CREATE LOAN =================
    LocalDate today = LocalDate.now();

    BookLoan loan = BookLoan.builder()
            .user(user)
            .book(book)
            .type(BookLoanType.CHECKOUT)
            .status(BookLoanStatus.CHECKED_OUT)
            .checkoutDate(today)
            .dueDate(today.plusDays(request.getCheckoutDays()))
            .renewalCount(0)
            .maxRenewals(2)
            .notes(request.getNotes())
            .build();

    // ================= UPDATE BOOK STOCK =================
    book.setAvailableCopies(book.getAvailableCopies() - 1);

    // ================= SAVE =================
    bookRepository.save(book);
    BookLoan savedLoan = bookLoanRepository.save(loan);

    return mapper.toDTO(savedLoan);
}

    // ================= CHECKIN =================
@Override
@Transactional
public BookLoanDTO checkInBook(CheckinRequest request) {

    // ================= FETCH LOAN =================
    BookLoan loan = bookLoanRepository.findById(request.getBookLoanId())
            .orElseThrow(() -> new BookException("Loan not found"));

    // ================= VALIDATION =================
    if (!loan.isActive()) {
        throw new BookException("Loan is not active or already closed");
    }

    // ================= SET RETURN =================
    LocalDate today = LocalDate.now();
    loan.setReturnDate(today);

    // ================= DETERMINE STATUS =================
    BookLoanStatus status = (request.getCondition() != null)
            ? request.getCondition()
            : BookLoanStatus.RETURNED;

    loan.setStatus(status);

    // ================= NOTES =================
    if (request.getNotes() != null && !request.getNotes().isBlank()) {
        loan.setNotes(request.getNotes());
    } else {
        loan.setNotes("Book returned");
    }

    // ================= BOOK STOCK UPDATE =================
    if (status != BookLoanStatus.LOST) {
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }

    // ================= OPTIONAL: OVERDUE CHECK =================
    if (loan.isOverdue()) {
        // You can plug fine calculation here later
        // e.g. loan.calculateFine(...)
    }

    // ================= SAVE =================
    BookLoan savedLoan = bookLoanRepository.save(loan);

    return mapper.toDTO(savedLoan);
}

    // ================= RENEW =================
@Override
@Transactional
public BookLoanDTO renewLoan(RenewalRequest request) {

    // ================= FETCH LOAN =================
    BookLoan loan = bookLoanRepository.findById(request.getBookLoanId())
            .orElseThrow(() -> new BookException("Loan not found"));

    // ================= VALIDATION =================
    if (request.getExtensionDays() == null || request.getExtensionDays() <= 0) {
        throw new BookException("Invalid extension days");
    }

    if (!loan.canRenew()) {
        throw new BookException("Loan cannot be renewed");
    }

    if (loan.getDueDate() == null) {
        throw new BookException("Invalid loan state: due date missing");
    }

    // ================= RENEW =================
    loan.setDueDate(loan.getDueDate().plusDays(request.getExtensionDays()));
    loan.setRenewalCount(loan.getRenewalCount() + 1);

    // ================= NOTES =================
    if (request.getNotes() != null && !request.getNotes().isBlank()) {
        loan.setNotes(request.getNotes());
    } else {
        loan.setNotes("Loan renewed");
    }

    // ================= SAVE =================
    BookLoan savedLoan = bookLoanRepository.save(loan);

    return mapper.toDTO(savedLoan);
}

    // ================= USER LOANS =================
@Override
public PageResponse<BookLoanDTO> getUserLoans(
        Long userId,
        BookLoanStatus status,
        int page,
        int size
) {

    // ================= PAGINATION =================
    Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "createdAt")
    );

    // ================= VALIDATION =================
    if (userId == null) {
        throw new BookException("User ID is required");
    }

    // ================= FETCH =================
    Page<BookLoan> result;

    if (status != null) {
        result = bookLoanRepository.findByStatusAndUserId(
                status,
                userId,
                pageable
        );
    } else {
        result = bookLoanRepository.findByUserId(userId, pageable);
    }

    // ================= CONVERT =================
    return convert(result);
}

    // ================= SEARCH =================
 @Override
public PageResponse<BookLoanDTO> searchLoans(BookLoanSearchRequest req) {

    Pageable pageable = createPageable(req);

    Specification<BookLoan> spec = (root, query, cb) -> cb.conjunction();

    // ================= FILTERS =================

    if (req.getUserId() != null) {
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("user").get("id"), req.getUserId()));
    }

    if (req.getBookId() != null) {
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("book").get("id"), req.getBookId()));
    }

    if (req.getStatus() != null) {
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), req.getStatus()));
    }

    if (req.isOverdueOnly()) {
        spec = spec.and((root, query, cb) ->
                cb.and(
                        cb.lessThan(root.get("dueDate"), LocalDate.now()),
                        root.get("status").in(
                                BookLoanStatus.CHECKED_OUT,
                                BookLoanStatus.OVERDUE
                        )
                ));
    }

    if (req.getStartDate() != null && req.getEndDate() != null) {
        spec = spec.and((root, query, cb) ->
                cb.between(root.get("checkoutDate"),
                        req.getStartDate(),
                        req.getEndDate()));
    }

    // ================= QUERY =================
    Page<BookLoan> page = bookLoanRepository.findAll(spec, pageable);

    return convert(page);
}

    // ================= OVERDUE =================
@Override
@Transactional
public int markOverdueLoans() {

    int updated = 0;
    int page = 0;

    Page<BookLoan> result;
    LocalDate today = LocalDate.now();

    do {
        Pageable pageable = PageRequest.of(page, 500);

        result = bookLoanRepository.findOverdueBookLoans(
                today,
                List.of(BookLoanStatus.CHECKED_OUT, BookLoanStatus.OVERDUE),
                pageable
        );

        for (BookLoan loan : result.getContent()) {

            // Only update if still CHECKED_OUT
            if (loan.getStatus() == BookLoanStatus.CHECKED_OUT) {
                loan.setStatus(BookLoanStatus.OVERDUE);
                updated++;
            }

            // Optional: trigger fine calculation or logging
            // int overdueDays = loan.getOverdueDays();
        }

        // Save batch (important for performance)
        bookLoanRepository.saveAll(result.getContent());

        page++;

    } while (!result.isLast());

    return updated;
}

    // ================= HELPERS =================
// ================= HELPERS =================

private Pageable createPageable(BookLoanSearchRequest req) {

    // ================= DEFAULTS =================
    int page = (req.getPage() < 0) ? 0 : req.getPage();
    int size = (req.getSize() <= 0) ? 20 : Math.min(req.getSize(), 100);

    String sortBy = (req.getSortBy() == null || req.getSortBy().isBlank())
            ? "createdAt"
            : req.getSortBy();

    String direction = (req.getSortDirection() == null)
            ? "DESC"
            : req.getSortDirection();

    // ================= SORT =================
    Sort sort = direction.equalsIgnoreCase("ASC")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    return PageRequest.of(page, size, sort);
}

private PageResponse<BookLoanDTO> convert(Page<BookLoan> page) {

    if (page == null) {
        return new PageResponse<>(
                List.of(),
                0, 0, 0, 0,
                true, true, true
        );
    }

    return new PageResponse<>(
            page.getContent()
                    .stream()
                    .map(mapper::toDTO)
                    .toList(),   // Java 16+ cleaner than collect()
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast(),
            page.isFirst(),
            page.isEmpty()
    );
}

  private int calculateOverdueDays(LocalDate dueDate, LocalDate today) {

    if (dueDate == null || today == null) {
        return 0;
    }

    if (!today.isAfter(dueDate)) {
        return 0;
    }

    return (int) ChronoUnit.DAYS.between(dueDate, today);
}



@Override
public BookLoanDTO getLoanById(Long loanId) {

    BookLoan loan = bookLoanRepository.findByIdWithUserAndBook(loanId)
            .orElseThrow(() -> new BookException("Loan not found"));

    return mapper.toDTO(loan);
}


}