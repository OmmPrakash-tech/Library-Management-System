package com.example.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    private final BookLoanMapper bookLoanMapper;

    @Override
    public BookLoanDTO checkoutBook(CheckoutRequest checkoutRequest) {
        User user = userService.getCurrentUser();
        return checkoutBookForUser(user.getId(), checkoutRequest);
    }

    @Override
    public BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) {

        // 1. validate user exist
        User user = userService.findById(userId);

        // 2. validate user has active subscription
        SubscriptionDTO subscription = subscriptionService
                .getUsersActiveSubscription(user.getId());

        // 3. validate book exists and is available
        Book book = bookRepository.findById(checkoutRequest.getBookId())
                .orElseThrow(() -> new BookException("Book not found with id " + checkoutRequest.getBookId()));

        if (!book.getActive()) {
            throw new BookException("Book is not active");
        }

        if (book.getAvailableCopies() <= 0) {
            throw new BookException("Book is not available");
        }

        // 4. check if user already has this book
        if (bookLoanRepository.hasActiveCheckout(userId, book.getId())) {
            throw new BookException("Book already has active checkout");
        }

        // 5. check user's active checkout limit
        long activeCheckouts = bookLoanRepository.countActiveBookLoansByUser(userId);
        int maxBooksAllowed = subscription.getMaxBooksAllowed();

        if (activeCheckouts >= maxBooksAllowed) {
            throw new BookException("You have reached your maximum number of books allowed");
        }

        // 6. check overdue books
        long overdueCount = bookLoanRepository.countOverdueBookLoansByUser(userId);

        if (overdueCount > 0) {
            throw new BookException("First return old overdue book!");
        }

        // 7. create book loan
        BookLoan bookLoan = BookLoan.builder()
                .user(user)
                .book(book)
                .type(BookLoanType.CHECKOUT)
                .status(BookLoanStatus.CHECKED_OUT)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(checkoutRequest.getCheckoutDays()))
                .renewalCount(0)
                .maxRenewals(2)
                .notes(checkoutRequest.getNotes())
                .isOverdue(false)
                .overdueDays(0)
                .build();

        // update available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // save loan
        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO checkinBook(CheckinRequest checkinRequest) {

        BookLoan bookLoan = bookLoanRepository
                .findById(checkinRequest.getBookLoanId())
                .orElseThrow(() -> new BookException("Book loan not found"));

        if (!bookLoan.isActive()) {
            throw new BookException("Book loan is not active");
        }

        bookLoan.setReturnDate(LocalDate.now());

        BookLoanStatus condition = checkinRequest.getCondition();
        if (condition == null) {
            condition = BookLoanStatus.RETURNED;
        }

        bookLoan.setStatus(condition);

        bookLoan.setOverdueDays(0);
        bookLoan.setIsOverdue(false);
        bookLoan.setNotes("Book returned by user");

        if (condition != BookLoanStatus.LOST) {
            Book book = bookLoan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }

        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO renewCheckout(RenewalRequest renewalRequest) {

        BookLoan bookLoan = bookLoanRepository
                .findById(renewalRequest.getBookLoanId())
                .orElseThrow(() -> new BookException("Book loan not found"));

        if (!bookLoan.canRenew()) {
            throw new BookException("Book cannot be renewed");
        }

        bookLoan.setDueDate(
                bookLoan.getDueDate()
                        .plusDays(renewalRequest.getExtensionDays())
        );

        bookLoan.setRenewalCount(bookLoan.getRenewalCount() + 1);
        bookLoan.setNotes("Book renewed by user");

        BookLoan savedBookLoan = bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    public PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size) {

        User currentUser = userService.getCurrentUser();
        Page<BookLoan> bookLoanPage;

        if (status != null) {

            Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());

            bookLoanPage = bookLoanRepository.findByStatusAndUser(
                    status, currentUser, pageable
            );

        } else {

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            bookLoanPage = bookLoanRepository.findByUserId(
                    currentUser.getId(), pageable
            );
        }

        return convertToPageResponse(bookLoanPage);
    }

    @Override
public PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest searchRequest) throws BookException {

    // Build pageable with sorting, size, etc.
    Pageable pageable = createPageable(
            searchRequest.getPage(),
            searchRequest.getSize(),
            searchRequest.getSortBy(),
            searchRequest.getSortDirection()
    );

    Page<BookLoan> bookLoanPage;

    // Apply filtering logic dynamically
    if (Boolean.TRUE.equals(searchRequest.getOverdueOnly())) {
        // Fetch overdue loans
        bookLoanPage = bookLoanRepository.findOverdueBookLoans(LocalDate.now(), pageable);

    } else if (searchRequest.getUserId() != null) {
        // Fetch loans by specific user
        bookLoanPage = bookLoanRepository.findByUserId(searchRequest.getUserId(), pageable);

    } else if (searchRequest.getBookId() != null) {
        // Fetch loans by specific book
        bookLoanPage = bookLoanRepository.findByBookId(searchRequest.getBookId(), pageable);

    } else if (searchRequest.getStatus() != null) {
        // Fetch loans by loan status
        bookLoanPage = bookLoanRepository.findByStatus(searchRequest.getStatus(), pageable);

    } else if (searchRequest.getStartDate() != null && searchRequest.getEndDate() != null) {
        // Fetch loans within date range
        bookLoanPage = bookLoanRepository.findBookLoansByDateRange(
                searchRequest.getStartDate(),
                searchRequest.getEndDate(),
                pageable
        );

    } else {
        // Default: return all loans
        bookLoanPage = bookLoanRepository.findAll(pageable);
    }

    // Convert entities to DTOs and wrap in response object
    return convertToPageResponse(bookLoanPage);
}

 @Override
public int updateOverdueBookLoan() {

    Pageable pageable = PageRequest.of(0, 1000); // Process in batches

    Page<BookLoan> overduePage = bookLoanRepository
            .findOverdueBookLoans(LocalDate.now(), pageable);

    int updateCount = 0;

    for (BookLoan bookLoan : overduePage.getContent()) {

        if (bookLoan.getStatus() == BookLoanStatus.CHECKED_OUT) {
            bookLoan.setStatus(BookLoanStatus.OVERDUE);
            bookLoan.setIsOverdue(true);
        }

        // Calculate overdue days
        int overdueDays = calculateOverdueDate(bookLoan.getDueDate(), LocalDate.now());
        bookLoan.setOverdueDays(overdueDays);

        // // Calculate fine
        // BigDecimal fine = fineCalculationService.calculateOverdueFine(bookLoan);

        bookLoanRepository.save(bookLoan);
        updateCount++;
    }

    return updateCount;
}

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {

        size = Math.min(size, 100);
        size = Math.max(size, 1);

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }

    private PageResponse<BookLoanDTO> convertToPageResponse(Page<BookLoan> bookLoanPage) {

        List<BookLoanDTO> bookLoanDTOs = bookLoanPage.getContent()
                .stream()
                .map(bookLoanMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                bookLoanDTOs,
                bookLoanPage.getNumber(),
                bookLoanPage.getSize(),
                bookLoanPage.getTotalElements(),
                bookLoanPage.getTotalPages(),
                bookLoanPage.isLast(),
                bookLoanPage.isFirst(),
                bookLoanPage.isEmpty()
        );
    }


    public int calculateOverdueDate(LocalDate dueDate, LocalDate today) {
    if (today.isBefore(dueDate) || today.isEqual(dueDate)) {
        return 0;
    }

    return (int) ChronoUnit.DAYS.between(dueDate, today);
}
}