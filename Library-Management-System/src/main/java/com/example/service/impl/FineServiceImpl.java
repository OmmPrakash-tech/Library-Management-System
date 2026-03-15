package com.example.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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

        Fine fine = Fine.builder()
                .bookLoan(bookLoan)
                .user(bookLoan.getUser())
                .type(request.getType())
                .amount(request.getAmount())
                .status(FineStatus.PENDING)
                .reason(request.getReason())
                .note(request.getNotes())
                .build();

        Fine savedFine = fineRepository.save(fine);

        return fineMapper.toDTO(savedFine);
    }

    @Override
    public PaymentInitiateResponse payFine(Long fineId, String transactionId) {

        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));

        if (fine.getStatus() == FineStatus.PAID) {
            throw new RuntimeException("Fine already paid");
        }

        if (fine.getStatus() == FineStatus.WAIVED) {
            throw new RuntimeException("Fine already waived");
        }

        User user = userService.getCurrentUser();

        PaymentInitiateRequest request = PaymentInitiateRequest.builder()
                .userId(user.getId())
                .fineId(fine.getId())
                .paymentType(PaymentType.FINE)
                .gateway(PaymentGateway.RAZORPAY)
                .amount(fine.getAmount())
                .description("Library fine payment")
                .build();

        return paymentService.initiatePayment(request);
    }

    @Override
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
    public FineDTO waiveFine(WaiveFineRequest request) {

        Fine fine = fineRepository.findById(request.getFineId())
                .orElseThrow(() -> new RuntimeException("Fine not found"));

        if (fine.getStatus() == FineStatus.WAIVED) {
            throw new RuntimeException("Fine already waived");
        }

        if (fine.getStatus() == FineStatus.PAID) {
            throw new RuntimeException("Fine already paid and cannot be waived");
        }

        User admin = userService.getCurrentUser();

        fine.waive(admin, request.getReason());

        Fine savedFine = fineRepository.save(fine);

        return fineMapper.toDTO(savedFine);
    }

    @Override
    public List<FineDTO> getMyFines(FineStatus status, FineType type) {

        User user = userService.getCurrentUser();
        List<Fine> fines;

        if (status != null && type != null) {

            fines = fineRepository.findByUserId(user.getId())
                    .stream()
                    .filter(f -> f.getStatus() == status && f.getType() == type)
                    .collect(Collectors.toList());

        } else if (status != null) {

            fines = fineRepository.findByUserId(user.getId())
                    .stream()
                    .filter(f -> f.getStatus() == status)
                    .collect(Collectors.toList());

        } else if (type != null) {

            fines = fineRepository.findByUserIdAndType(user.getId(), type);

        } else {

            fines = fineRepository.findByUserId(user.getId());
        }

        return fines.stream()
                .map(fineMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<FineDTO> getAllFines(FineStatus status, FineType type, Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Fine> finePage = fineRepository.findAllWithFilters(
                userId,
                status,
                type,
                pageable
        );

        List<FineDTO> fineDTOs = finePage.getContent()
                .stream()
                .map(fineMapper::toDTO)
                .collect(Collectors.toList());

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
}