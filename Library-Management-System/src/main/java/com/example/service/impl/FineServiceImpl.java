package com.example.service.impl;

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

    // Prevent duplicate fine
    if (fineRepository.existsByBookLoanId(request.getBookLoanId())) {
        throw new IllegalStateException("Fine already exists for this book loan");
    }

    String reason = request.getReason() != null ? request.getReason().trim() : null;
    String note = request.getNote() != null ? request.getNote().trim() : null;

    Fine fine = Fine.builder()
            .bookLoan(bookLoan)
            .user(bookLoan.getUser())
            .type(request.getType())
            .amount(request.getAmount())
            .paidAmount(0L)
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

    if (fine.getStatus() == FineStatus.PAID) {
        throw new IllegalStateException("Fine already paid");
    }

    if (fine.getStatus() == FineStatus.WAIVED) {
        throw new IllegalStateException("Fine already waived");
    }

    User user = userService.getCurrentUser();

    // Optional: ownership check
    if (!fine.getUser().getId().equals(user.getId())) {
        throw new IllegalStateException("You are not allowed to pay this fine");
    }

    long remainingAmount = fine.getAmount() - fine.getPaidAmount();

    if (remainingAmount <= 0) {
        throw new IllegalStateException("No remaining amount to pay");
    }

    PaymentInitiateRequest request = PaymentInitiateRequest.builder()
            .fineId(fine.getId())
            .paymentType(PaymentType.FINE)
            .gateway(PaymentGateway.RAZORPAY)
            .amount(remainingAmount)
            .description("Payment for fine ID: " + fine.getId())
            .build();

    return paymentService.initiatePayment(request);
}

    
    public void markFineAsPaid(Long fineId, Long amount, String transactionId) {

        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found with id: " + fineId));

        fine.applyPayment(amount);

        fine.setTransactionId(transactionId);
        fine.setStatus(FineStatus.PAID);
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

    // Status validation
    if (fine.getStatus() == FineStatus.PAID) {
        throw new IllegalStateException("Fine already paid");
    }

    if (fine.getStatus() == FineStatus.WAIVED) {
        throw new IllegalStateException("Fine already waived");
    }

    User user = userService.getCurrentUser();

    // Ownership check (important)
    if (!fine.getUser().getId().equals(user.getId())) {
        throw new IllegalStateException("You are not allowed to pay this fine");
    }

    long remainingAmount = fine.getAmount() - fine.getPaidAmount();

    if (remainingAmount <= 0) {
        throw new IllegalStateException("No remaining amount to pay");
    }

    PaymentInitiateRequest request = PaymentInitiateRequest.builder()
            .fineId(fine.getId())
            .paymentType(PaymentType.FINE)
            .gateway(PaymentGateway.RAZORPAY)
            .amount(remainingAmount)
            .description("Payment for fine ID: " + fine.getId())
            .build();

    return paymentService.initiatePayment(request);
}

@Override
public FineDTO applyPayment(Long fineId, Long amount, String transactionId) {

    Fine fine = fineRepository.findById(fineId)
            .orElseThrow(() -> new RuntimeException("Fine not found"));

    // Validate fine state
    if (fine.getStatus() == FineStatus.WAIVED) {
        throw new IllegalStateException("Cannot pay a waived fine");
    }

    if (fine.getStatus() == FineStatus.PAID) {
        throw new IllegalStateException("Fine is already fully paid");
    }

    // Validate amount
    if (amount == null || amount <= 0) {
        throw new IllegalArgumentException("Payment amount must be positive");
    }

    long remainingAmount = fine.getAmount() - fine.getPaidAmount();

    if (amount > remainingAmount) {
        throw new IllegalArgumentException("Payment exceeds remaining fine amount");
    }

    // Apply payment using entity logic
    fine.applyPayment(amount);

    // Set transaction details
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
}