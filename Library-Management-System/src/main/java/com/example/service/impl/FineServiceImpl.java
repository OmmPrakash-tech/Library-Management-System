package com.example.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.domain.FineStatus;
import com.example.domain.FineType;
import com.example.domain.PaymentGateway;
import com.example.domain.PaymentType;
import com.example.mapper.FineMapper;
import com.example.model.BookLoan;
import com.example.model.Fine;
import com.example.model.User;
import com.example.payload.dto.FineDTO;
import com.example.payload.request.CreateFineRequest;
import com.example.payload.request.PaymentInitiateRequest;
import com.example.payload.request.WaiveFineRequest;
import com.example.payload.response.PageResponse;
import com.example.payload.response.PaymentInitiateResponse;
import com.example.repository.BookLoanRepository;
import com.example.repository.FineRepository;
import com.example.service.FineService;
import com.example.service.PaymentService;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
public class FineServiceImpl implements FineService {

    private final BookLoanRepository bookLoanRepository;
    private final FineRepository fineRepository;
    private final FineMapper fineMapper;
    private final UserService userService;
    private final PaymentService paymentService;

    public FineServiceImpl(
            BookLoanRepository bookLoanRepository,
            FineRepository fineRepository,
            FineMapper fineMapper,
            UserService userService,
            PaymentService paymentService) {

        this.bookLoanRepository = bookLoanRepository;
        this.fineRepository = fineRepository;
        this.fineMapper = fineMapper;
        this.userService = userService;
        this.paymentService = paymentService;
    }

@Override
public FineDTO createFine(CreateFineRequest request) {

    BookLoan bookLoan = bookLoanRepository.findById(request.getBookLoanId())
            .orElseThrow(() -> new RuntimeException("Book loan not found"));

    // ✅ Validate type (admin must select)
    if (request.getType() == null) {
        throw new IllegalArgumentException("Fine type is required");
    }

    // ❌ Prevent duplicate SAME TYPE only (not all fines)
    if (fineRepository.existsByBookLoanIdAndType(
            request.getBookLoanId(), request.getType())) {

        throw new IllegalStateException(
                "Fine of type " + request.getType() + " already exists for this loan"
        );
    }

    // ❌ Optional: restrict invalid manual types
    if (request.getType() == FineType.PROCESSING) {
        throw new IllegalArgumentException("Processing fee cannot be created manually");
    }

    String reason = request.getReason() != null ? request.getReason().trim() : null;
    String note = request.getNote() != null ? request.getNote().trim() : null;

    // 🔥 AUTO CALCULATE (core logic)
    BigDecimal amount = calculateFineByType(bookLoan, request.getType());

    // ❗ Safety check
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalStateException("Calculated fine amount is invalid");
    }

    Fine fine = Fine.builder()
            .bookLoan(bookLoan)
            .user(bookLoan.getUser())
            .type(request.getType())
            .amount(amount)
            .paidAmount(BigDecimal.ZERO)
            .status(FineStatus.PENDING)
            .reason(reason)
            .note(note)
            .build();

    Fine savedFine = fineRepository.save(fine);

    return fineMapper.toDTO(savedFine);
}

 @Override
public PaymentInitiateResponse payFine(Long fineId) {

    Fine fine = fineRepository.findById(fineId)
            .orElseThrow(() -> new RuntimeException("Fine not found"));

    // ✅ Status validation
    if (fine.getStatus() == FineStatus.PAID) {
        throw new IllegalStateException("Fine already paid");
    }

    if (fine.getStatus() == FineStatus.WAIVED) {
        throw new IllegalStateException("Fine already waived");
    }

    User user = userService.getCurrentUser();

    // ✅ Ownership check
    if (!fine.getUser().getId().equals(user.getId())) {
        throw new IllegalStateException("You are not allowed to pay this fine");
    }

    // ✅ Calculate remaining amount (BigDecimal)
    BigDecimal remainingAmount =
            fine.getAmount().subtract(fine.getPaidAmount());

    if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalStateException("No remaining amount to pay");
    }

    // ✅ Convert ₹ → paise (Razorpay requirement)
    long amountInPaise = remainingAmount
            .multiply(BigDecimal.valueOf(100))
            .longValue();

    PaymentInitiateRequest request = PaymentInitiateRequest.builder()
            .fineId(fine.getId())
            .paymentType(PaymentType.FINE)
            .gateway(PaymentGateway.RAZORPAY)
            .amount(amountInPaise) // ✅ Razorpay expects paise
            .description("Payment for fine ID: " + fine.getId())
            .build();

    return paymentService.initiatePayment(request);
}

    
   
public void markFineAsPaid(Long fineId, BigDecimal amount, String transactionId) {

    Fine fine = fineRepository.findById(fineId)
            .orElseThrow(() -> new RuntimeException("Fine not found with id: " + fineId));

    // ✅ Validate state
    if (fine.getStatus() == FineStatus.WAIVED) {
        throw new IllegalStateException("Cannot pay a waived fine");
    }

    if (fine.getStatus() == FineStatus.PAID) {
        throw new IllegalStateException("Fine already fully paid");
    }

    // ✅ Apply payment (this updates paidAmount + status internally)
    fine.applyPayment(amount);

    // ✅ Set transaction details
    fine.setTransactionId(transactionId);
    fine.setUpdatedAt(LocalDateTime.now());

    fineRepository.save(fine);
}

@Override
public FineDTO waiveFine(Long fineId, WaiveFineRequest request) {

    Fine fine = fineRepository.findById(fineId)
            .orElseThrow(() -> new RuntimeException("Fine not found"));

    if (fine.getStatus() == FineStatus.WAIVED) {
        throw new IllegalStateException("Fine already waived");
    }

    if (fine.getStatus() == FineStatus.PAID) {
        throw new IllegalStateException("Fine already paid and cannot be waived");
    }

    User admin = userService.getCurrentUser();

    // Optional: role check
    // if (!admin.getRole().equals(Role.ADMIN)) {
    //     throw new IllegalStateException("Only admin can waive fines");
    // }

    String reason = request.getReason().trim();

    fine.waive(admin, reason);

    Fine savedFine = fineRepository.save(fine);

    return fineMapper.toDTO(savedFine);
}

@Override
public List<FineDTO> getMyFines(FineStatus status, FineType type) {

    User user = userService.getCurrentUser();

    List<Fine> fines = fineRepository.findMyFinesWithFilters(
            user.getId(),
            status,
            type
    );

    return fines.stream()
            .map(fineMapper::toDTO)
            .collect(Collectors.toList());
}

@Override
public PageResponse<FineDTO> getAllFines(
        FineStatus status,
        FineType type,
        Long userId,
        int page,
        int size
) {

    int validPage = Math.max(page, 0);
    int validSize = Math.min(Math.max(size, 1), 50);

    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

    Pageable pageable = PageRequest.of(validPage, validSize, sort);

    Page<Fine> finePage = fineRepository.findAllWithFilters(
            userId,
            status,
            type,
            pageable
    );

    List<FineDTO> fineDTOs = finePage.getContent()
            .stream()
            .map(fineMapper::toDTO)
            .toList();

    return new PageResponse<>(
            fineDTOs,
            finePage.getNumber(),
            finePage.getSize(),
            finePage.getTotalElements(),
            finePage.getTotalPages(),
            finePage.isLast(),
            finePage.isFirst(),
            finePage.isEmpty()
    );
}

@Override
public PaymentInitiateResponse initiatePayment(Long fineId) {

    Fine fine = fineRepository.findById(fineId)
            .orElseThrow(() -> new RuntimeException("Fine not found"));

    // ✅ Status validation
    if (fine.getStatus() == FineStatus.PAID) {
        throw new IllegalStateException("Fine already paid");
    }

    if (fine.getStatus() == FineStatus.WAIVED) {
        throw new IllegalStateException("Fine already waived");
    }

    User user = userService.getCurrentUser();

    // ✅ Ownership check
    if (!fine.getUser().getId().equals(user.getId())) {
        throw new IllegalStateException("You are not allowed to pay this fine");
    }

    // ✅ BigDecimal calculation
    BigDecimal remainingAmount =
            fine.getAmount().subtract(fine.getPaidAmount());

    if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalStateException("No remaining amount to pay");
    }

    PaymentInitiateRequest request = PaymentInitiateRequest.builder()
            .fineId(fine.getId())
            .paymentType(PaymentType.FINE)
            .gateway(PaymentGateway.RAZORPAY)
            .amount(remainingAmount.longValue()) // ⚠️ convert if needed
            .description("Payment for fine ID: " + fine.getId())
            .build();

    return paymentService.initiatePayment(request);
}

@Override
public FineDTO applyPayment(Long fineId, BigDecimal amount, String transactionId) {

    Fine fine = fineRepository.findById(fineId)
            .orElseThrow(() -> new RuntimeException("Fine not found"));

    // ✅ Validate fine state
    if (fine.getStatus() == FineStatus.WAIVED) {
        throw new IllegalStateException("Cannot pay a waived fine");
    }

    if (fine.getStatus() == FineStatus.PAID) {
        throw new IllegalStateException("Fine is already fully paid");
    }

    // ✅ Validate amount
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Payment amount must be positive");
    }

    // ✅ Calculate remaining = total - paid
    BigDecimal remainingAmount = fine.getAmount().subtract(fine.getPaidAmount());

    // ❗ Prevent overpayment
    if (amount.compareTo(remainingAmount) > 0) {
        throw new IllegalArgumentException("Payment exceeds remaining fine amount");
    }

    // ✅ Apply payment (entity method already uses BigDecimal)
    fine.applyPayment(amount);

    // ✅ Set transaction details
    fine.setTransactionId(transactionId);
    fine.setProcessedBy(userService.getCurrentUser());

    Fine savedFine = fineRepository.save(fine);

    return fineMapper.toDTO(savedFine);
}



@Override
public FineDTO getFineById(Long fineId) {

    Fine fine = fineRepository.findById(fineId)
            .orElseThrow(() -> new RuntimeException("Fine not found"));

    return fineMapper.toDTO(fine);
}

public BigDecimal calculateFineByType(BookLoan loan, FineType type) {

    BigDecimal price = loan.getBook().getPrice();
    boolean subExpired = loan.isSubscriptionExpired();
    boolean overdue = loan.isOverdue();

    long days = loan.getOverdueDays();
    long blocks = days / 3;

    BigDecimal fine = BigDecimal.ZERO;

    switch (type) {

        case LOSS:
            return subExpired
                    ? price.multiply(BigDecimal.valueOf(10))
                    : price.multiply(BigDecimal.valueOf(5));

        case DAMAGE:
            return subExpired
                    ? price.multiply(BigDecimal.valueOf(2))
                        .divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP)
                    : price.divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);

        case OVERDUE:
            return BigDecimal.valueOf(
                    subExpired ? blocks * 10 : blocks * 5
            );

        case PROCESSING:
            return BigDecimal.valueOf(20); // optional

        default:
            throw new RuntimeException("Invalid fine type");
    }
}

@Override
public BigDecimal calculateFine(BookLoan loan) {

    BigDecimal fine = BigDecimal.ZERO;

    boolean overdue = loan.isOverdue();
    boolean damaged = loan.isDamaged();
    boolean lost = loan.isLost();
    boolean subExpired = loan.isSubscriptionExpired();

    BigDecimal price = loan.getBook().getPrice();

    long overdueDays = loan.getOverdueDays();
    long blocks = overdueDays / 3; // every 3 days

    // 🔥 LOSS (highest priority)
    if (lost) {
        return subExpired
                ? price.multiply(BigDecimal.valueOf(10))
                : price.multiply(BigDecimal.valueOf(5));
    }

    // 🔥 DAMAGE
    if (damaged) {
        BigDecimal damageFine;

        if (subExpired) {
            damageFine = price.multiply(BigDecimal.valueOf(2))
                    .divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);
        } else {
            damageFine = price.divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);
        }

        fine = fine.add(damageFine);
    }

    // 🔥 OVERDUE
    if (overdue) {
        BigDecimal overdueFine = BigDecimal.valueOf(
                subExpired ? blocks * 10 : blocks * 5
        );

        fine = fine.add(overdueFine);
    }

    return fine;
}

    @Override
public void updateFineForLoan(Long loanId) {

    BookLoan loan = bookLoanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));

    BigDecimal fine = calculateFine(loan);

    loan.setFineAmount(fine);
    bookLoanRepository.save(loan);
}

  @Override
public void updateAllFines() {

    List<BookLoan> loans = bookLoanRepository.findAll();

    for (BookLoan loan : loans) {
        BigDecimal fine = calculateFine(loan);
        loan.setFineAmount(fine);
    }

    bookLoanRepository.saveAll(loans);
}

    @Override
public BigDecimal getFineForLoan(Long loanId) {

    BookLoan loan = bookLoanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));

    return calculateFine(loan);
}
}